// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationPrescription;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import com.datafascia.common.inject.Injectors;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.etl.ucsf.web.MedAdminDiffListener.ElementType;
import com.datafascia.etl.ucsf.web.persist.JsonPersistUtils;
import com.datafascia.etl.ucsf.web.rules.model.RxNorm;
import com.google.common.base.Strings;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.commons.io.IOUtils;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ProcessorLog;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.InputStreamCallback;
import org.apache.nifi.processor.util.StandardValidators;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kie.api.runtime.KieSession;

/**
 * Processor for detecting and handling diffs in UCSF medication administration.
 */
@Slf4j
public class MedAdminDiffProcessor extends AbstractProcessor {
  private Set<Relationship> relationships;
  private List<PropertyDescriptor> properties;
  private MedAdminDiffListener diffListener;
  private RxNormLookup rxNormDb;
  private String tableName;
  private KieSession kieSession;
  private IGenericClient client = null;
  private final Authorizations authorizations = new Authorizations("System");
  private final FhirContext ctx = FhirContext.forDstu2();
  @Inject
  private volatile Connector connector;
  private final HashMap<String, Medication> orderMedicationCache = new HashMap<>();

  // Statuses to ignore in MedAdmin diffs.
  private final static List<String> IGNORE_STATUSES = new ArrayList<>(Arrays.asList(
      "Due", "Rate Verify", "Canceled Entry", "Stopped"));

  // Track keys we expect to change.
  private final static String[] MED_DIFF_KEYS = new String[]{"OrderStatus", "OrderedDose",
    "OrderedDoseUnit", "Frequency"};
  private final static String[] ADMIN_DIFF_KEYS = new String[]{"AdminAction", "AdministrationTime",
    "Dose", "DoseUnit", "Duration", "Rate", "RateUnit", "TimeTaken", "User"};

  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("SUCCESS")
      .description("Success relationship")
      .build();
  public static final Relationship FAILURE = new Relationship.Builder()
      .name("FAILURE")
      .description("Failure relationship")
      .build();
  public static final PropertyDescriptor ACCUMULOTABLE = new PropertyDescriptor.Builder()
      .name("Accumulo table to persist med orders and admins.")
      .defaultValue("UcsfMedOrder")
      .required(true)
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .build();
  public static final PropertyDescriptor RXNORMDB = new PropertyDescriptor.Builder()
      .name("RxNorm database JDBC url")
      .defaultValue("RxNorm.RXNCONSO")
      .required(true)
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .build();
  public static final PropertyDescriptor RXNORMTABLE = new PropertyDescriptor.Builder()
      .name("RxNorm database table to fetch from.")
      .defaultValue("RxNorm.RXNCONSO")
      .required(true)
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .build();
  public static final PropertyDescriptor RXNORMDBUSERNAME = new PropertyDescriptor.Builder()
      .name("RxNorm database username.")
      .required(true)
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .build();
  public static final PropertyDescriptor RXNORMDBPASSWORD = new PropertyDescriptor.Builder()
      .name("RxNorm database password.")
      .required(true)
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .build();
  public static final PropertyDescriptor FHIR_SERVER = new PropertyDescriptor.Builder()
      .name("Fhir server connection.")
      .required(true)
      .addValidator(StandardValidators.URL_VALIDATOR)
      .build();
  public static final PropertyDescriptor FHIR_USERNAME = new PropertyDescriptor.Builder()
      .name("Fhir server username.")
      .required(false)
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .build();
  public static final PropertyDescriptor FHIR_PASSWORD = new PropertyDescriptor.Builder()
      .name("Fhir server password.")
      .required(false)
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .build();

  /**
   * Sets the listener to be called when a diff is found.
   *
   * @param newListener The listener to be called when a diff is found.
   */
  public void setDiffListener(MedAdminDiffListener newListener) {
    diffListener = newListener;
  }

  /**
   * Sets the connector to use.
   *
   * @param newConnector The connector to use.
   */
  public void setConnector(Connector newConnector) {
    connector = newConnector;
  }

  @Override
  public Set<Relationship> getRelationships() {
    return relationships;
  }

  @Override
  public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return properties;
  }

  @Override
  public void init(final ProcessorInitializationContext context) {
    List<PropertyDescriptor> initProperties = new ArrayList<>();
    initProperties.add(ACCUMULOTABLE);
    initProperties.add(RXNORMDB);
    initProperties.add(RXNORMDBUSERNAME);
    initProperties.add(RXNORMDBPASSWORD);
    initProperties.add(RXNORMTABLE);
    initProperties.add(FHIR_SERVER);
    initProperties.add(FHIR_USERNAME);
    initProperties.add(FHIR_PASSWORD);
    this.properties = Collections.unmodifiableList(initProperties);

    Set<Relationship> initRelationships = new HashSet<>();
    initRelationships.add(SUCCESS);
    initRelationships.add(FAILURE);
    this.relationships = Collections.unmodifiableSet(initRelationships);

    kieSession = UcsfMedicationUtils
        .createKieSession("com/datafascia/etl/ucsf/web/rules/rxnorm.drl");
    kieSession.setGlobal("log", log);
  }

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    if (connector == null) {
      connector = Injectors.getInjector().getInstance(Connector.class);
    }

    if (tableName == null) {
      tableName = context.getProperty(ACCUMULOTABLE).getValue();
      if (!connector.tableOperations().exists(tableName)) {
        try {
          connector.tableOperations().create(tableName);
        } catch (AccumuloException | AccumuloSecurityException | TableExistsException e) {
          throw new IllegalStateException("Cannot create table " + tableName, e);
        }
      }
    }

    if (client == null) {
      client = ctx.newRestfulGenericClient(context.getProperty(FHIR_SERVER).getValue());
      String fhirUsername = context.getProperty(FHIR_USERNAME).getValue();
      String fhirPassword = context.getProperty(FHIR_PASSWORD).getValue();
      if (!Strings.isNullOrEmpty(fhirUsername) && !Strings.isNullOrEmpty(fhirPassword)) {
        client.registerInterceptor(new BasicAuthInterceptor(fhirUsername, fhirPassword));
      }
    }

    final ProcessorLog plog = this.getLogger();
    final FlowFile flowfile = session.get();

    // Build the connection to the RxNorm database.
    String jdbcUrl = context.getProperty(RXNORMDB).getValue();
    String username = context.getProperty(RXNORMDBUSERNAME).getValue();
    String password = context.getProperty(RXNORMDBPASSWORD).getValue();
    if (rxNormDb == null) {
      try {
        String rxnormDb = context.getProperty(RXNORMTABLE).getValue();
        rxNormDb = new RxNormLookup(jdbcUrl, rxnormDb, username, password);
      } catch (SQLException e) {
        log.error("SQL error fetching rxnorm name.", e);
        plog.error("SQL error fetching rxnorm name: " + e.getMessage());
        session.transfer(flowfile, FAILURE);
      }
    }

    session.read(flowfile, new InputStreamCallback() {

      @Override
      public void process(InputStream in) throws IOException {
        try {
          String jsonString = IOUtils.toString(in);
          JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonString);

          Object errorObj = jsonObject.get("Error");
          if (errorObj != null) {
            log.error("Medication web service error: " + errorObj.toString());
          }

          // Find medication diffs in each medication in each patient.
          JSONArray patients = (JSONArray) jsonObject.get("Patient");
          for (Object obj : patients) {
            if (obj instanceof JSONObject) {
              String encounterId = ((JSONObject) obj).get("CSN").toString();
              JSONArray medications = (JSONArray) ((JSONObject) obj).get("Medications");
              for (Object obj2 : medications) {
                if (obj2 instanceof JSONObject) {
                  JSONObject jsonMed = (JSONObject) obj2;
                  String scd = UcsfMedicationUtils.extractSCD(jsonMed);
                  try {
                    findPrescriptionDiffs(jsonMed, encounterId, scd);
                  } catch (SQLException ex) {
                    log.error("Error fetching RxNorm name with SCD " + scd + ": " + ex.getMessage(),
                        ex);
                    plog.error("Error fetching RxNorm name with SCD " + scd + ": " + ex.getMessage
                        ());
                    throw new ProcessException(ex);
                  } catch (MutationsRejectedException | TableNotFoundException ex) {
                    log.error("Error finding medication diffs: " + ex.getMessage(), ex);
                    plog.error("Error finding medication diffs: " + ex.getMessage());
                    throw new ProcessException(ex);
                  }
                }
              }
            }
          }
        } catch (ParseException e) {
          log.error("Error reading json.", e);
          plog.error("Failed to read json string: " + e.getMessage());
          throw new ProcessException(e);
        }
      }
    });
    session.transfer(flowfile, SUCCESS);
  }

  /**
   * Detects diffs in medication JSON object.
   *
   * @param orderJson   A JSONObject of a medication order.
   * @param encounterId The ID of the encounter.
   * @param scd         The SCD of the drug this refers to.
   * @throws MutationsRejectedException If the mutation fails.
   * @throws ParseException             If the persisted data in accumulo is corrupt.
   * @throws TableNotFoundException     If the table doesn't exist.
   * @throws SQLException               If there is a problem interacting with SQL.
   */
  private void findPrescriptionDiffs(JSONObject orderJson, String encounterId, String scd)
      throws ParseException, MutationsRejectedException, TableNotFoundException, SQLException {
    String prescriptionId = orderJson.get("OrderID").toString();
    String[] routeParts = orderJson.get("Route").toString().split("\\^");
    String[] frequencyParts = orderJson.get("Frequency").toString().split("\\^");
    List<String> rxNormIngredients = UcsfMedicationUtils.extractRxNormIngredients(orderJson);

    RxNorm droolNorm = new RxNorm() {
      {
        setRxcuiSCD(scd);
        setAhfs(orderJson.get("AHFS").toString());
        setPca(orderJson.get("PCA").toString());
        setDrugId(orderJson.get("DrugID").toString());
        setRxcuiIn(rxNormIngredients);
      }
    };

    if (routeParts.length > 1) {
      droolNorm.setRoute(routeParts[1]);
    }

    if (frequencyParts.length > 1) {
      droolNorm.setFrequency(frequencyParts[1]);
    }

    // If we don't have an SCD, then we can't populate a medication.
    Medication medication = null;
    if (Strings.isNullOrEmpty(scd)) {
      medication = populateMedication(droolNorm);
    }

    String medDataNew = orderJson.toJSONString();

    String medDataOld = JsonPersistUtils.fetchJson(ElementType.ORDER, prescriptionId, connector,
        tableName, authorizations);
    JsonPersistUtils.persistJson(ElementType.ORDER, prescriptionId, medDataNew, connector,
        tableName);

    // If medDataOld isn't null, then it's a change. Otherwise, it's a new order.
    if (medDataOld != null) {
      JSONObject oldDataJson = (JSONObject) new JSONParser().parse(medDataOld);

      for (String key : MED_DIFF_KEYS) {
        Object medelem = oldDataJson.get(key);
        if (medelem != null) {
          String oldData = medelem.toString();
          String newData = orderJson.get(key).toString();

          if (!oldData.equals(newData)) {
            if (diffListener != null) {
              diffListener.diff(ElementType.ORDER, key, prescriptionId, oldData, newData);
            }
          }
        }
      }

      // Recreate the prescription based on the new data.
      MedicationPrescription medpresc = UcsfMedicationUtils.populatePrescription(orderJson,
          medication, encounterId, prescriptionId, droolNorm.getMedsSets(), client);
      MedicationPrescription existingPrescription = UcsfMedicationUtils.getMedicationPrescription(
          prescriptionId, encounterId, client);
      if (existingPrescription != null) {
        medpresc.setId(existingPrescription.getId());
        client.update().resource(medpresc).execute();
      } else {
        log.warn(
            "Could not find prescription for prescriptionId [{}] and encounterId [{}]",
            prescriptionId,
            encounterId);
      }
    } else {
      MedicationPrescription prescription = UcsfMedicationUtils.populatePrescription
          (orderJson, medication, encounterId, prescriptionId, droolNorm.getMedsSets(), client);
      prescription = UcsfMedicationUtils.savePrescription(prescription, client);
      diffListener.newOrder(prescription);
    }

    JSONArray meds = (JSONArray) orderJson.get("MedAdmin");
    if (meds != null) {
      for (Object obj : meds) {
        if (obj instanceof JSONObject) {
          JSONObject admin = (JSONObject) obj;
          findAdministrationDiffs(admin, encounterId, prescriptionId, droolNorm);
        }
      }
    }
  }

  /**
   * Detects diffs in medication administration JSON object.
   *
   * @param admin       The admin to inspect for diffs.
   * @param encounterId the Id of the encounter.
   * @param prescriptionId     The Id of the order.
   * @param droolNorm   The populated RxNorm pojo.
   * @throws MutationsRejectedException If the mutation fails.
   * @throws ParseException             If the JSON found is unparsable.
   * @throws TableNotFoundException     If the table doesn't exist.
   * @throws SQLException               If there is a problem interacting with SQL.
   */
  private void findAdministrationDiffs(JSONObject admin, String encounterId, String prescriptionId,
      RxNorm droolNorm) throws ParseException,
      MutationsRejectedException, TableNotFoundException, SQLException {
    /*
     * Only recording actual administrations, not scheduled ones or verifications.
     * Might need to invert this to a whitelist instead containing "Given" and "New Bag".
     */

    String[] adminAction = admin.get("AdminAction").toString().split("\\^");
    if (IGNORE_STATUSES.contains(adminAction[1])) {
      return;
    }

    String adminId = admin.get("AdminID").toString();
    String adminDataNew = admin.toJSONString();

    String adminDataOld = JsonPersistUtils.fetchJson(ElementType.ADMIN, prescriptionId + "-"
        + adminId, connector, tableName, authorizations);
    JsonPersistUtils.persistJson(ElementType.ADMIN, prescriptionId + "-" + adminId, adminDataNew,
        connector, tableName);

    // If adminDataOld isn't null, then it's a change. Otherwise, it's a new admin.
    if (adminDataOld != null) {
      JSONObject oldDataJson = (JSONObject) new JSONParser().parse(adminDataOld);

      for (String key : ADMIN_DIFF_KEYS) {
        Object oldAdmin = oldDataJson.get(key);
        if (oldAdmin != null) {
          String oldData = oldAdmin.toString();
          String newData = admin.get(key).toString();

          if (!oldData.equals(newData)) {
            if (diffListener != null) {
              diffListener.diff(ElementType.ADMIN, key, adminId, oldData, newData);
            }
          }
        }
      }

      // Recreate the administration based on the new data.
      MedicationAdministration medAdmin = UcsfMedicationUtils.populateAdministration
          (admin, adminId, prescriptionId, encounterId, populateMedication(droolNorm),
              droolNorm.getMedsSets(), client);
      if (!medAdmin.isEmpty()) {
        try {
          medAdmin.setId(UcsfMedicationUtils.getMedicationAdministration(adminId, encounterId,
              prescriptionId, client).getId());
        } catch (NullPointerException e) {
          log.error("No admin for order " + prescriptionId + " and admin " + adminId);
          throw e;
        }
        client.update().resource(medAdmin).execute();
      }
    } else {
      // We have a new administration. Populate a MedicationAdministration and save it.
      MedicationAdministration medAdmin = UcsfMedicationUtils
          .populateAdministration(admin, adminId, prescriptionId, encounterId,
              populateMedication(droolNorm), droolNorm.getMedsSets(), client);
      medAdmin = UcsfMedicationUtils.saveAdministration(medAdmin, client);
      diffListener.newAdmin(medAdmin);
    }
  }

  private Medication populateMedication(RxNorm droolNorm) throws SQLException {
    Medication medication = orderMedicationCache.get(droolNorm.getRxcuiSCD());
    if (medication == null) {
      if (droolNorm.getRxcuiSCD() == null) {
        return null;
      }

      // Fetch the normalized medication name.
      String drugName = rxNormDb.getRxString(Integer.parseInt(droolNorm.getRxcuiSCD()));
      kieSession.insert(droolNorm);

      // Fetch the medication object.
      Bundle response = client.search()
          .forResource(Medication.class)
          .where(Medication.CODE.exactly().code(droolNorm.getRxcuiSCD()))
          .execute();
      List<BundleEntry> entries = response.getEntries();

      if (entries.size() > 0) {
        BundleEntry entry = entries.get(0);
        medication = (Medication) entry.getResource();
      } else {
        medication = new Medication();
        medication.setName(drugName);
        medication.setCode(new CodeableConceptDt(CodingSystems.SEMANTIC_CLINICAL_DRUG,
            droolNorm.getRxcuiSCD()));

        MethodOutcome outcome = client.create().resource(medication).execute();
        medication.setId(outcome.getId());
      }

      if (!orderMedicationCache.keySet().contains(droolNorm.getRxcuiSCD())) {
        orderMedicationCache.put(droolNorm.getRxcuiSCD(), medication);
      }
    }
    return medication;
  }

  public void setClient(IGenericClient client) {
    this.client = client;
  }
}
