// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.RangeDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.etl.ucsf.web.rules.model.MedsSet;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.io.ResourceFactory;

/**
 * Utilities supporting medication administration processing.
 */
@Slf4j
public class UcsfMedicationUtils {
  enum NotGivenReason {
    DUE,
    NOT_GIVEN
  }

  enum GivenReason {
    SYSTEM_ONLY,
    ANESTHESIA,
    PROCEDURE,
    CIRCUIT
  }

  /**
   * Populate a MedicationAdministration.
   *
   * @param adminJson The base JSON.
   * @param adminId The ID of the administration.
   * @param orderId The ID of the prescription.
   * @param encounterId The encounter.
   * @param med The medication.
   * @param medsSet A search of medication group sets.
   * @param clientBuilder The resource client builder.
   * @return The populated administration.
   * @throws java.sql.SQLException When there is a problem communicating with SQL.
   */
  public static MedicationAdministration populateAdministration(JSONObject adminJson,
      String adminId, String orderId, String encounterId, Medication med, List<MedsSet> medsSet,
      ClientBuilder clientBuilder) throws SQLException {
    MedicationAdministration admin = new MedicationAdministration();

    String[] adminAction = adminJson.get("AdminAction").toString().split("\\^");
    if (adminAction.length > 1) {
      UcsfAdminStatusEnum.populateAdminStatus(admin, adminAction[1]);
    }

    ResourceReferenceDt prescriptionRef = new ResourceReferenceDt();
    prescriptionRef.setResource(clientBuilder.getMedicationOrderClient()
        .read(orderId, encounterId));
    admin.setPrescription(prescriptionRef);

    if (encounterId != null) {
      Encounter encounter = clientBuilder.getEncounterClient().getEncounter(encounterId);
      if (encounter != null) {
        admin.setEncounter(new ResourceReferenceDt(encounter.getId()));
      } else {
        log.warn("Could not find encounter with CSN " + encounterId + ". HAPI FHIR doesn't allow "
            + "dangling references, so the linkage between the encounter and admin " + orderId
            + "-" + adminId + " is lost.");
      }
    } else {
      log.info("No encounter given for admin " + adminId + " on order " + orderId + "!!!!!");
    }
    admin.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_MEDICATION_ADMINISTRATION)
        .setValue(orderId + "-" + adminId);

    for (MedsSet medsSetInst : medsSet) {
      IdentifierDt groupIdent = admin.addIdentifier();
      groupIdent.setSystem(CodingSystems.UCSF_MEDICATION_GROUP_NAME);
      groupIdent.setValue(medsSetInst.getName());
    }

    String timeTaken = adminJson.get("AdministrationTime").toString();
    Instant timeTakenInstant = UcsfWebGetProcessor.epicDateToInstant(timeTaken);
    DateTimeDt timeTakenDt = new DateTimeDt(Date.from(timeTakenInstant));

    String dose = adminJson.get("Dose").toString();
    String[] doseUnitParts = adminJson.get("DoseUnit").toString().split("\\^");
    String doseUnit = "";
    if (doseUnitParts.length > 1) {
      doseUnit = doseUnitParts[1];
    }

    // It's possible that the web service doesn't have an SCD for an order, so there is no med.
    if (med != null) {
      ResourceReferenceDt medRef = new ResourceReferenceDt();
      medRef.setReference(med.getId());
      admin.setMedication(medRef);
    }

    if (!dose.isEmpty()) {
      try {
        MedicationAdministration.Dosage dosage = new MedicationAdministration.Dosage();
        SimpleQuantityDt quantity = new SimpleQuantityDt();
        quantity.setUnit(doseUnit);
        quantity.setValue(new BigDecimal(dose));
        dosage.setQuantity(quantity);
        admin.setDosage(dosage);
      } catch (NumberFormatException e) {
        log.error("Dose value of '" + dose + "' for admin " + adminId + " is not a valid number.");
      }
    }

    admin.setEffectiveTime(timeTakenDt);

    log.info("Populated admin with encounter " + admin.getEncounter().getReference().getIdPart());

    return admin;
  }

  /**
   * Convert the order status given by the web service to a status understood by FHIR.
   *
   * @param orderStatus The original status from the web service.
   * @return A FHIR prescription status.
   */
  public static MedicationOrderStatusEnum webOrderStatusToFhir(String orderStatus) {
    UcsfOrderStatusEnum orderStatusEnum
        = UcsfOrderStatusEnum.values()[Integer.parseInt(orderStatus)];
    switch (orderStatusEnum) {
      case PENDING:
        return MedicationOrderStatusEnum.ACTIVE;
      case SUSPEND:
        return MedicationOrderStatusEnum.ON_HOLD;
      case DISPENSED:
        return MedicationOrderStatusEnum.ACTIVE;
      case PENDINGVERIFY:
        return MedicationOrderStatusEnum.DRAFT;
      case VERIFIED:
        return MedicationOrderStatusEnum.ACTIVE;
      case SENT:
        return MedicationOrderStatusEnum.DRAFT;
      case COMPLETED:
        return MedicationOrderStatusEnum.COMPLETED;
      case DISCONTINUED:
        return MedicationOrderStatusEnum.STOPPED;
      default:
        return null;
    }
  }

  /**
   * Returns the SCD segment for a given medication order from the UCSF web services.
   *
   * @param json The JSON from which to extract the SCD.
   * @return The SCD for the medication.
   */
  public static String extractSCD(JSONObject json) {
    JSONArray rxnorm = (JSONArray) json.get("RxNorm");
    for (Object elem : rxnorm) {
      JSONObject entry = (JSONObject) elem;
      if (entry.get("TermType").toString().equals("9^Semantic Clinical Drug")) {
        return entry.get("Code").toString();
      }
    }

    // TODO: Remove default SCD before going to production.
    // HERE BE DRAGONS.
    // A default value is provided below PURELY for debugging purposes.
    log.warn("Using default 107373 for drug named: " + json.get("DrugName"));
    return "107373";
  }

  /**
   * Extract the ingredient codes from an order.
   *
   * @param json The JSONObject representing the order.
   * @return A search of ingredient codes.
   */
  public static List<String> extractRxNormIngredients(JSONObject json) {
    ArrayList<String> ingredients = new ArrayList<>();
    JSONArray rxnorm = (JSONArray) json.get("RxNorm");
    for (Object elem : rxnorm) {
      JSONObject entry = (JSONObject) elem;
      if (entry.get("TermType").toString().equals("1^Ingredient")) {
        ingredients.add(entry.get("Code").toString());
      }
    }
    return ingredients;
  }

  /**
   * Populate a MedicationOrder.
   *
   * @param orderJson The base JSON.
   * @param medication The medication.
   * @param encounterId The ID of the associated encounter.
   * @param orderId The id of the order.
   * @param medsSet The medsset codes and names.
   * @param clientBuilder The resource client builder.
   * @return The populated medication order.
   * @throws java.sql.SQLException When there is a problem communicating with SQL.
   */
  public static MedicationOrder populateMedicationOrder(
      JSONObject orderJson,
      Medication medication,
      String encounterId,
      String orderId,
      List<MedsSet> medsSet,
      ClientBuilder clientBuilder) throws SQLException {

    MedicationOrder medicationOrder = new MedicationOrder();
    medicationOrder.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_MEDICATION_ORDER)
        .setValue(orderId);

    // Fetch fields from the JSON.
    String dateTimeOrdered = orderJson.get("DateTimeOrdered").toString();
    String orderStatus = orderJson.get("OrderStatus").toString().split("\\^")[0];
    String orderedDose = orderJson.get("OrderedDose").toString();
    String[] orderedDoseUnitParts = orderJson.get("OrderedDoseUnit").toString().split("\\^");

    Instant instant = UcsfWebGetProcessor.epicDateToInstant(dateTimeOrdered);
    DateTimeDt orderedDateDt = new DateTimeDt(Date.from(instant));

    if (encounterId != null) {
      Encounter encounter = new Encounter();
      try {
        encounter = clientBuilder.getEncounterClient().getEncounter(encounterId);
      } catch (ResourceNotFoundException e) {
        // The encounter will remain empty, and the warning below will trigger.
      }
      if (!encounter.isEmpty()) {
        medicationOrder.setEncounter(new ResourceReferenceDt(encounter));
      } else {
        log.warn("Could not find encounter with CSN " + encounterId + ". HAPI FHIR doesn't allow "
            + "dangling references, so the linkage between the encounter and order " + orderId
            + " is lost.");
      }
    }

    if (medication != null) {
      ResourceReferenceDt medicationRef = new ResourceReferenceDt();
      medicationRef.setReference(medication.getId());
      medicationOrder.setMedication(medicationRef);
    } else {
      throw new IllegalArgumentException("Medication order " + orderId
          + " lacks a reference to a medication:\n" + orderJson.toJSONString());
    }
    medicationOrder.setDateWritten(orderedDateDt);
    try {
      medicationOrder.setStatus(UcsfMedicationUtils.webOrderStatusToFhir(orderStatus));
    } catch (IllegalArgumentException e) {
      log.error("Unknown order status " + orderStatus + ". Resulting order will"
          + "have no \"status\".");
    }

    MedicationOrder.DosageInstruction dosage = medicationOrder.addDosageInstruction();

    // If the dose contains a dash, then it's a range.
    if (orderedDose.contains("-")) {
      String[] ratioParts = orderedDose.split("-");
      RangeDt range = new RangeDt();
      SimpleQuantityDt lowSQ = new SimpleQuantityDt();
      lowSQ.setValue(new BigDecimal(ratioParts[0]));
      SimpleQuantityDt highSQ = new SimpleQuantityDt();
      highSQ.setValue(new BigDecimal(ratioParts[1]));
      range.setLow(lowSQ);
      range.setHigh(highSQ);
      dosage.setDose(range);
      if (orderedDoseUnitParts.length > 1) {
        range.getLow().setUnit(orderedDoseUnitParts[1]);
        range.getHigh().setUnit(orderedDoseUnitParts[1]);
      }
    } else if (!orderedDose.isEmpty()) {
      SimpleQuantityDt quantityDt = new SimpleQuantityDt();
      if (orderedDoseUnitParts.length > 1) {
        quantityDt.setUnit(orderedDoseUnitParts[1]);
      }
      try {
        quantityDt.setValue(new BigDecimal(orderedDose));
        dosage.setDose(quantityDt);
      } catch (NumberFormatException e) {
        log.error("Dose value of '" + orderedDose + "' for order " + orderId
            + " is not a valid number.");
      }
    }

    for (MedsSet medsSetInst : medsSet) {
      IdentifierDt groupIdent = medicationOrder.addIdentifier();
      groupIdent.setSystem(CodingSystems.UCSF_MEDICATION_GROUP_NAME);
      groupIdent.setValue(medsSetInst.getName());
    }

    return medicationOrder;
  }

  /**
   * Creates a new KieContainer, which will include a KieModule with the DRL files sent as parameter
   *
   * @param ks KieServices
   * @param drlResourcesPaths DRL files that will be included
   * @return the new KieContainer
   */
  public static KieContainer createKieContainer(KieServices ks, String... drlResourcesPaths) {
    // Create the in-memory File System and add the resources files to it
    KieFileSystem kfs = ks.newKieFileSystem();
    for (String path : drlResourcesPaths) {
      kfs.write(ResourceFactory.newClassPathResource(path));
    }
    // Create the builder for the resources of the File System
    KieBuilder kbuilder = ks.newKieBuilder(kfs);
    // Build the Kie Bases
    kbuilder.buildAll();
    // Check for errors
    if (kbuilder.getResults().
        hasMessages(Message.Level.ERROR)) {
      throw new IllegalArgumentException(kbuilder.getResults().
          toString());
    }
    // Get the Release ID (mvn style: groupId, artifactId,version)
    ReleaseId relId = kbuilder.getKieModule().
        getReleaseId();
    // Create the Container, wrapping the KieModule with the given ReleaseId
    return ks.newKieContainer(relId);
  }

  /**
   * Creates a new KieSession (Stateful) that will be used for the rules. Its KieBase contains the
   * drl files sent by parameter.
   *
   * @param drlResourcesPaths Paths for drools rules.
   * @return the new KieSession
   */
  public static KieSession createKieSession(String... drlResourcesPaths) {
    KieServices ks = KieServices.Factory.get();
    KieContainer kcontainer = createKieContainer(ks, drlResourcesPaths);

    // Configure and create the KieBase
    KieBaseConfiguration kbconf = ks.newKieBaseConfiguration();
    KieBase kbase = kcontainer.newKieBase(kbconf);

    // Configure and create the KieSession
    KieSessionConfiguration ksconf = ks.newKieSessionConfiguration();
    return kbase.newKieSession(ksconf, null);
  }
}
