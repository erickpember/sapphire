// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.common.configuration.ConfigurationNode;
import com.datafascia.common.configuration.Configure;
import com.datafascia.common.inject.Injectors;
import com.datafascia.etl.ucsf.web.persist.JsonPersistUtils;
import com.datafascia.etl.ucsf.web.rules.model.RxNorm;
import com.datafascia.etl.ucsf.web.util.JSONObjectDateTimeOrderedComparator;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.nifi.processor.exception.ProcessException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kie.api.runtime.StatelessKieSession;

/**
 * Transforms UCSF medication orders and administration to FHIR objects.
 */
@ConfigurationNode("UcsfMedAdminProcessor")
@Slf4j
public class MedAdminDiffTransformer {
  private StatelessKieSession kieSession;
  private String tableName;
  private MedAdminDiffListener diffListener;
  private RxNormLookup rxNormDb;
  private JSONObjectDateTimeOrderedComparator jsonDateTimeJsonCompare
      = new JSONObjectDateTimeOrderedComparator();
  HashFunction hashFunction = Hashing.md5();
  private final Authorizations authorizations = new Authorizations("System");

  // Statuses to ignore in MedAdmin diffs.
  private final static List<String> IGNORE_STATUSES = new ArrayList<>(Arrays.asList(
      "Due", "Rate Verify", "Canceled Entry", "MAR Hold", "MAR Unhold",
      "See Alternative"));
  // Track keys we expect to change.
  private final static String[] MED_DIFF_KEYS = new String[]{"OrderStatus", "OrderedDose",
    "OrderedDoseUnit", "Frequency"};
  private final static String[] ADMIN_DIFF_KEYS = new String[]{"AdminAction", "AdministrationTime",
    "Dose", "DoseUnit", "Duration", "Rate", "RateUnit", "TimeTaken", "User"};

  @Inject
  private volatile Connector connector;
  @Inject
  private ClientBuilder apiClient;

  @Configure
  public String accumuloTable;
  @Configure
  public String rxnormDb;
  @Configure
  public String rxnormDbTable;
  @Configure
  public String rxnormDbUsername;
  @Configure
  public String rxnormDbPassword;

  /**
   * Sets the listener to be called when a diff is found.
   *
   * @param newListener The listener to be called when a diff is found.
   */
  public void setDiffListener(MedAdminDiffListener newListener) {
    diffListener = newListener;
  }

  /**
   * Transforms UCSF med orders and admins to FHIR objects.
   *
   * @param jsonString nursing order in JSON format
   */
  public void accept(String jsonString) {
    JSONObject jsonObject;
    try {
      jsonObject = (JSONObject) new JSONParser().parse(jsonString);
    } catch (ParseException e) {
      throw new IllegalStateException("Cannot parse JSON " + jsonString, e);
    }

    Object errorObj = jsonObject.get("Error");
    if (errorObj != null) {
      log.error("Medication web service error: " + errorObj.toString());
    }

    if (connector == null) {
      connector = Injectors.getInjector().getInstance(Connector.class);
    }

    if (tableName == null) {
      tableName = accumuloTable;
      if (!connector.tableOperations().exists(tableName)) {
        try {
          connector.tableOperations().create(tableName);
        } catch (AccumuloException | AccumuloSecurityException | TableExistsException e) {
          throw new IllegalStateException("Cannot create table " + tableName, e);
        }
      }
    }

    // Build the connection to the RxNorm database.
    if (rxNormDb == null) {
      try {
        rxNormDb = new RxNormLookup(rxnormDb, rxnormDbTable, rxnormDbUsername,
            rxnormDbPassword);
      } catch (SQLException e) {
        log.error("SQL error fetching rxnorm name.", e);
        throw new ProcessException(e);
      }
    }

    // Find medication diffs in each medication in each patient.
    JSONArray patients = (JSONArray) jsonObject.get("Patient");
    for (Object obj : patients) {
      if (obj instanceof JSONObject) {
        String encounterId = ((JSONObject) obj).get("CSN").toString();
        Encounter encounter;
        try {
          encounter = apiClient.getEncounterClient().getEncounter(encounterId);
        } catch (ResourceNotFoundException e) {
          log.warn("Encounter " + encounterId + " is not known in FHIR. Associated medication "
              + "orders and admins will be dropped for now.");
          continue;
        }

        JSONArray medications = (JSONArray) ((JSONObject) obj).get("Medications");

        Collections.sort(medications, new JSONObjectDateTimeOrderedComparator());

        for (Object obj2 : medications) {
          JSONObject jsonMed = (JSONObject) obj2;
          String scd = UcsfMedicationUtils.extractSCD(jsonMed);
          try {
            findPrescriptionDiffs(jsonMed, scd, encounter);
          } catch (SQLException ex) {
            log.error("Error fetching RxNorm name with SCD " + scd + ": " + ex.getMessage(),
                ex);
            throw new ProcessException(ex);
          } catch (MutationsRejectedException | TableNotFoundException | ParseException ex) {
            log.error("Error finding medication diffs: " + ex.getMessage(), ex);
            throw new ProcessException(ex);
          }
        }
      }
    }
  }

  /**
   * Detects diffs in medication JSON object.
   *
   * @param orderJson A JSONObject of a medication order.
   * @param encounter The encounter to which the check is relevant.
   * @param scd The SCD of the drug this refers to.
   * @throws MutationsRejectedException If the mutation fails.
   * @throws ParseException If the persisted data in accumulo is corrupt.
   * @throws TableNotFoundException If the table doesn't exist.
   * @throws SQLException If there is a problem interacting with SQL.
   */
  private void findPrescriptionDiffs(JSONObject orderJson, String scd, Encounter encounter)
      throws ParseException, MutationsRejectedException,
      TableNotFoundException, SQLException {
    String prescriptionId = orderJson.get("OrderID").toString();
    String drugName = orderJson.get("DrugName").toString();
    String[] routeParts = orderJson.get("Route").toString().split("\\^");
    String[] frequencyParts = orderJson.get("Frequency").toString().split("\\^");
    List<String> rxNormIngredients = new ArrayList<>();
    if (orderJson.get("RxNorm") != null && !((JSONArray) orderJson.get("RxNorm")).isEmpty()) {
      rxNormIngredients.addAll(
          UcsfMedicationUtils.extractRxNormIngredients((JSONArray) orderJson.get("RxNorm")));
    } else {
      if (orderJson.get("Mixture") != null) {
        for (Object mixobj : (JSONArray) orderJson.get("Mixture")) {
          JSONObject mixjson = (JSONObject) mixobj;
          if (mixjson.get("rxNormMix") != null) {
            rxNormIngredients.addAll(
                UcsfMedicationUtils.extractRxNormIngredients((JSONArray) mixjson.get("rxNormMix")));
          }
        }
      }
    }

    RxNorm droolNorm = new RxNorm() {
      {
        setRxcuiSCD(scd);
        setAhfs(orderJson.get("AHFS").toString());
        setPca(orderJson.get("PCA").toString());
        setDrugId(orderJson.get("DrugID").toString());
        setRxcuiIn(rxNormIngredients);
      }
    };

    if (routeParts.length > 0) {
      droolNorm.setRoute(routeParts[0]);
    }
    if (frequencyParts.length > 0) {
      droolNorm.setFrequency(frequencyParts[0]);
    } else {
      log.warn("No frequency given for medication order " + prescriptionId);
    }

    if (kieSession == null) {
      kieSession = UcsfMedicationUtils
          .createKieSession("com/datafascia/etl/ucsf/web/rules/rxnorm.drl");
      kieSession.setGlobal("log", log);
    }

    kieSession.execute(droolNorm);

    // If we don't have an SCD, then we can't populate a medication.
    Medication medication = null;
    String customMedId = null;
    if (!Strings.isNullOrEmpty(scd)) {
      medication = UcsfMedicationUtils.populateMedication(droolNorm, null, null, rxNormDb,
          apiClient);
    } else if (orderJson.get("Mixture") != null) {
      HashCode hc = hashFunction.newHasher()
          .putString(((JSONArray) orderJson.get("Mixture")).toJSONString(), Charsets.UTF_8)
          .hash();
      customMedId = hc.toString();
      medication = UcsfMedicationUtils.populateMedication(droolNorm, customMedId, drugName,
          rxNormDb, apiClient);
    } else if (orderJson.get("RxNorm") != null) {
      // This will trigger if there is no SCD, but the RxNorm still isn't null.
      HashCode hc = hashFunction.newHasher()
          .putString(((JSONArray) orderJson.get("RxNorm")).toJSONString(), Charsets.UTF_8)
          .hash();
      customMedId = hc.toString();
      medication = UcsfMedicationUtils.populateMedication(droolNorm, customMedId, drugName,
          rxNormDb, apiClient);
    }

    String medDataNew = orderJson.toJSONString();
    String medDataOld = JsonPersistUtils.fetchJson(MedAdminDiffListener.ElementType.ORDER,
        prescriptionId, connector, tableName, authorizations);
    JsonPersistUtils.persistJson(MedAdminDiffListener.ElementType.ORDER, prescriptionId, medDataNew,
        connector, tableName);

    MedicationOrder medOrder = null;
    // If medDataOld isn't null, then it's a change. Otherwise, it's a new order.
    if (medDataOld != null) {
      JSONObject oldDataJson = (JSONObject) new JSONParser().parse(medDataOld);

      boolean foundDiff = false;
      for (String key : MED_DIFF_KEYS) {
        Object medelem = oldDataJson.get(key);
        if (medelem != null) {
          String oldData = medelem.toString();
          String newData = orderJson.get(key).toString();

          if (!oldData.equals(newData)) {
            foundDiff = true;
            if (diffListener != null) {
              diffListener.diff(MedAdminDiffListener.ElementType.ORDER, key, prescriptionId,
                  oldData, newData);
            }
          }
        }
      }

      if (!foundDiff) {
        // Recreate the prescription based on the new data.
        medOrder = UcsfMedicationUtils.populateMedicationOrder(
            orderJson,
            medication,
            prescriptionId,
            droolNorm.getMedsSets(),
            encounter);
        MedicationOrder existingOrder = apiClient.getMedicationOrderClient()
            .read(prescriptionId, encounter.getIdentifierFirstRep().getValue());
        if (existingOrder != null) {
          medOrder.setId(existingOrder.getId());
          apiClient.getMedicationOrderClient().update(medOrder);
          apiClient.invalidateMedicationOrders(encounter.getIdentifierFirstRep().getValue());
        } else {
          log.warn(
              "Could not find prescription [{}] for encounterId [{}]."
              + " Older data for this exists in the accumulo table [{}] but"
              + " is not stored in the API. Attempting to create it now.",
              prescriptionId,
              encounter.getIdentifierFirstRep().getValue(),
              tableName);
          apiClient.getMedicationOrderClient().create(medOrder);
          apiClient.invalidateMedicationOrders(encounter.getIdentifierFirstRep().getValue());
        }
      }
    } else {
      medOrder = UcsfMedicationUtils.populateMedicationOrder(
          orderJson,
          medication,
          prescriptionId,
          droolNorm.getMedsSets(),
          encounter);
      medOrder = apiClient.getMedicationOrderClient().create(medOrder);
      apiClient.invalidateMedicationOrders(encounter.getIdentifierFirstRep().getValue());
      if (diffListener != null) {
        diffListener.newOrder(medOrder);
      }
    }

    JSONArray admins = (JSONArray) orderJson.get("MedAdmin");

    if (admins != null && admins.size() > 0) {
      if (medOrder == null) {
        medOrder = apiClient.getMedicationOrderClient().read(prescriptionId,
            encounter.getIdentifierFirstRep().getValue());
      }
      for (Object obj : admins) {
        if (obj instanceof JSONObject) {
          JSONObject admin = (JSONObject) obj;
          findAdministrationDiffs(admin, prescriptionId, droolNorm, customMedId,
              drugName, encounter, medOrder);
        }
      }
    }
  }

  /**
   * Detects diffs in medication administration JSON object.
   *
   * @param admin The admin to inspect for diffs.
   * @param encounter the encounter to which the check is relevant.
   * @param prescriptionId The Id of the order.
   * @param droolNorm The populated RxNorm pojo.
   * @throws MutationsRejectedException If the mutation fails.
   * @throws ParseException If the JSON found is unparsable.
   * @throws TableNotFoundException If the table doesn't exist.
   * @throws SQLException If there is a problem interacting with SQL.
   */
  private void findAdministrationDiffs(JSONObject admin, String prescriptionId, RxNorm droolNorm,
      String customMedId, String drugName, Encounter encounter, MedicationOrder order)
      throws ParseException, MutationsRejectedException, TableNotFoundException, SQLException {
    /*
     * Only recording actual administrations, not scheduled ones or verifications. Might need to
     * invert this to a whitelist instead containing "Given" and "New Bag".
     */
    String encounterId = encounter.getIdentifierFirstRep().getValue();
    String adminAction = admin.get("AdminAction").toString();
    String adminId = admin.get("AdminID").toString();
    String[] adminActionParts = adminAction.split("\\^");

    if (adminActionParts.length <= 1) {
      log.error("Non-parsable admin action field [{}]. Cannot create med admin status for "
          + "admin ID [{}], encounter [{}], prescription id [{}]. Discarding this medication "
          + "administration.", adminAction, adminId, encounterId, prescriptionId);
      return;
    } else {
      if (IGNORE_STATUSES.contains(adminActionParts[1])) {
        return;
      }
    }

    String adminDataNew = admin.toJSONString();
    String adminDataOld = JsonPersistUtils.fetchJson(MedAdminDiffListener.ElementType.ADMIN,
        prescriptionId + "-" + adminId, connector, tableName, authorizations);
    JsonPersistUtils.persistJson(MedAdminDiffListener.ElementType.ADMIN, prescriptionId + "-"
        + adminId, adminDataNew, connector, tableName);

    // If adminDataOld isn't null, then it's a change. Otherwise, it's a new admin.
    if (adminDataOld != null) {
      if (!adminDataNew.equals(adminDataOld)) {
        JSONObject oldDataJson = (JSONObject) new JSONParser().parse(adminDataOld);

        boolean foundDiffKey = false;
        for (String key : ADMIN_DIFF_KEYS) {
          Object oldAdmin = oldDataJson.get(key);
          if (oldAdmin != null) {
            String oldData = oldAdmin.toString();
            String newData = admin.get(key).toString();

            if (!oldData.equals(newData)) {
              foundDiffKey = true;
              if (diffListener != null) {
                diffListener.diff(MedAdminDiffListener.ElementType.ADMIN, key, adminId, oldData,
                    newData);
              }
            }
          }
        }

        if (!foundDiffKey) {
          return;
        }

        // Recreate the administration based on the new data.
        MedicationAdministration medAdmin = UcsfMedicationUtils.populateAdministration(admin,
            adminId, prescriptionId, UcsfMedicationUtils.populateMedication(droolNorm, customMedId,
                drugName, rxNormDb, apiClient),
            droolNorm.getMedsSets(), encounter, order);
        MedicationAdministration existingAdministration = apiClient
            .getMedicationAdministrationClient().get(adminId, encounterId,
                prescriptionId);
        if (existingAdministration != null) {
          medAdmin.setId(existingAdministration.getId());
          apiClient.getMedicationAdministrationClient()
              .update(medAdmin);
        } else {
          log.warn(
              "Could not find administration [{}] for prescriptionId [{}] and encounterId [{}]."
              + " Older data for this admin exists in the accumulo table " + tableName + " but"
              + " is not stored in the API. Attempting to create it now.",
              adminId,
              prescriptionId,
              encounterId);
          apiClient.getMedicationAdministrationClient().save(medAdmin);
          apiClient.invalidateMedicationAdministrations(encounterId);
        }
      }
    } else {
      // We have a new administration. Populate a MedicationAdministration and save it.
      MedicationAdministration medAdmin = UcsfMedicationUtils
          .populateAdministration(admin, adminId, prescriptionId,
              UcsfMedicationUtils.populateMedication(droolNorm, customMedId, drugName, rxNormDb,
                  apiClient),
              droolNorm.getMedsSets(), encounter, order);
      medAdmin = apiClient.getMedicationAdministrationClient().save(medAdmin);
      apiClient.invalidateMedicationAdministrations(encounterId);
      if (diffListener != null) {
        diffListener.newAdmin(medAdmin);
      }
    }
  }
}
