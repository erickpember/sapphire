// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.ProcedureRequestUtils;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import com.datafascia.emerge.ucsf.codes.ProcedureRequestCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Daily Sedation Interruption Candidate
 */
public class DailySedationInterruptionCandidate {

  /**
   * Result container for daily sedation interruption candidate.
   */
  @AllArgsConstructor
  @Data
  public static class CandidateResult {
    private boolean candidate;
    private String notCandidateReason;
  }

  // Potential results
  private static final CandidateResult OFF_SEDATION =
      new CandidateResult(false, "Off Sedation");
  private static final CandidateResult RECEIVING_NMBA =
      new CandidateResult(false, "Receiving NMBA");
  private static final CandidateResult STATUS_EPILEPTICUS =
      new CandidateResult(false, "Status Epilepticus");
  private static final CandidateResult RESPIRATORY_INSTABILITY =
      new CandidateResult(false, "Respiratory Instability");
  private static final CandidateResult THERAPEUTIC_HYPOTHERMIA =
      new CandidateResult(false, "Theraputic Hypothermia");
  private static final CandidateResult RASS_2_OR_GREATER =
      new CandidateResult(false, "RASS 2+ or Greater");
  private static final CandidateResult WITHDRAWAL_SEIZURE_RISK =
      new CandidateResult(false, "Withdrawal Seizure Risk");
  private static final CandidateResult HEMODYNAMIC_INSTABILITY =
      new CandidateResult(false, "Hemodynamic Instability");
  private static final CandidateResult OTHER =
      new CandidateResult(false, "Other");
  private static final CandidateResult YES =
      new CandidateResult(true, null);
  private static final CandidateResult INVALID =
      new CandidateResult(false, "No specified conditions were met");

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Determines if the patient is a candidate for daily sedation interruption.
   *
   * @param encounterId
   *     encounter to search
   * @return object encapsulating whether they are a candidate, and a reason for why not.
   */
  public CandidateResult getDailySedationInterruptionCandidate(String encounterId) {
    Instant nowInstance = Instant.now(clock);
    Date now = Date.from(nowInstance);
    Date twoHoursAgo = Date.from(nowInstance.minus(2, ChronoUnit.HOURS));
    Date sevenHoursAgo = Date.from(nowInstance.minus(7, ChronoUnit.HOURS));
    Date twentyFiveHoursAgo = Date.from(nowInstance.minus(25, ChronoUnit.HOURS));
    Date fourtyEightHoursAgo = Date.from(nowInstance.minus(48, ChronoUnit.HOURS));

    // Gather the list of medication administrations we will be using.
    List<MedicationAdministration> medicationAdministrations =
        apiClient.getMedicationAdministrationClient().search(encounterId);

    /* If there are no MedicationAdministration resources where
     * MedicationAdministration.identifier.value == (“Continuous Infusion Dexmedetomidine IV”,
     * “Continuous Infusion Propofol IV”, “Continuous Infusion Lorazepam IV” or “Continuous Infusion
     * Midazolam IV”) then Daily Sedation Interruption Candidate = “No: Off Sedation”
     */
    boolean sedativeInUse = false;
    for (MedicationAdministration admin : medicationAdministrations) {
      if (MedicationAdministrationUtils.hasMedsSet(admin,
          MedsSetEnum.CONTINUOUS_INFUSION_DEXMEDETOMIDINE_IV.getCode())
          || MedicationAdministrationUtils.hasMedsSet(admin,
              MedsSetEnum.CONTINUOUS_INFUSION_PROPOFOL_IV.getCode())
          || MedicationAdministrationUtils.hasMedsSet(admin,
              MedsSetEnum.CONTINUOUS_INFUSION_LORAZEPAM_IV.getCode())
          || MedicationAdministrationUtils.hasMedsSet(admin,
              MedsSetEnum.CONTINUOUS_INFUSION_MIDAZOLAM_IV.getCode())) {
        sedativeInUse = true;
      }
    }
    if (!sedativeInUse) {
      return OFF_SEDATION;
    }

    /* if notInfusing(MedicationAdministrations) then Daily Sedation Interruption Candidate = “No:
     * Off Sedation”
     */
    boolean allAdminOffSedation = true;
    for (MedicationAdministration admin : medicationAdministrations) {
      for (IdentifierDt id : admin.getIdentifier()) {
        if (MedicationAdministrationUtils.notInfusing(apiClient, encounterId, id.getValue())) {
          allAdminOffSedation = false;
        }
      }
    }
    if (allAdminOffSedation) {
      return OFF_SEDATION;
    }

    PeriodDt twoHourPeriod = new PeriodDt();
    twoHourPeriod.setStart(twoHoursAgo, TemporalPrecisionEnum.SECOND);
    twoHourPeriod.setEnd(now, TemporalPrecisionEnum.SECOND);

    /* if there are any MedicationAdministration resources where
     * MedicationAdministration.identifier.value == (“Intermittent Cisatracurium IV”, “Intermittent
     * Vecuronium IV”, “Intermittent Rocuronium IV”, “Intermittent Pancuronium IV”) and
     * beenAdministered(MedicationAdministration) within the last 2 hours then Daily Sedation
     * Interruption Candidate = “No: Receiving NMBA”
     */
    for (MedicationAdministration admin : medicationAdministrations) {
      for (IdentifierDt id : admin.getIdentifier()) {
        if (MedicationAdministrationUtils
            .beenAdministered(apiClient, encounterId, twoHourPeriod, id.getValue())) {
          if (MedicationAdministrationUtils.hasMedsSet(admin,
              MedsSetEnum.INTERMITTENT_CISATRACURIUM_IV.getCode())
              || MedicationAdministrationUtils.hasMedsSet(admin,
                  MedsSetEnum.INTERMITTENT_VECURONIUM_IV.getCode())
              || MedicationAdministrationUtils.hasMedsSet(admin,
                  MedsSetEnum.INTERMITTENT_ROCURONIUM_IV.getCode())
              || MedicationAdministrationUtils.hasMedsSet(admin,
                  MedsSetEnum.INTERMITTENT_PANCURONIUM_IV.getCode())) {
            return RECEIVING_NMBA;
          }
        }
      }
    }

    /* if there are any MedicationAdministration resources where
     * MedicationAdministration.identifier.value == (“Continuous Infusion Cisatracurium IV”,
     * “Continuous Infusion Vecuronium IV”) and activelyInfusing(MedicationAdministrations) then
     * Daily Sedation Interruption Candidate = “No: Receiving NMBA”
     */
    for (MedicationAdministration admin : medicationAdministrations) {
      for (IdentifierDt id : admin.getIdentifier()) {
        if (MedicationAdministrationUtils
            .activelyInfusing(apiClient, encounterId, id.getValue())) {
          switch (admin.getIdentifierFirstRep().getValue()) {
            case "Continuous Infusion Cisatracurium IV":
            case "Continuous Infusion Vecuronium IV":
              return RECEIVING_NMBA;
          }
        }
      }
    }

    // Gather observations needed.
    String freshestTrainOfFour = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient, encounterId, "304500964", twoHoursAgo)
        .map(observation -> observation.getValue().toString())
        .orElse("");

    String freshestWakeUpAction = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient, encounterId, "304890033", twentyFiveHoursAgo)
        .map(observation -> observation.getValue().toString())
        .orElse("");

    List<ProcedureRequest> hypothermiaBlanketOrders = ProcedureRequestUtils.getByCodeAfterTime(
        apiClient, encounterId, ProcedureRequestCodeEnum.HYPOTHERMIA_BLANKET_ORDER_1.getCode(),
        twentyFiveHoursAgo);
    hypothermiaBlanketOrders.addAll(ProcedureRequestUtils.getByCodeAfterTime(
        apiClient, encounterId, ProcedureRequestCodeEnum.HYPOTHERMIA_BLANKET_ORDER_2.getCode(),
        twentyFiveHoursAgo));

    Optional<Observation> freshestCoolingPadStatusFromPast25Hours = ObservationUtils
        .getFreshestByCodeAfterTime(apiClient, encounterId, "3045000709", twentyFiveHoursAgo);

    List<Observation> temperatureFromPast48Hours = ObservationUtils.getObservationByCodeAfterTime(
        apiClient, encounterId, "3045000001", fourtyEightHoursAgo);

    List<Observation> coolingPadPatientTemperatureFromPast48Hours = ObservationUtils
        .getObservationByCodeAfterTime(apiClient, encounterId, "3045000458", fourtyEightHoursAgo);

    List<Observation> coolingPadWaterTemperatureFromPast48Hours = ObservationUtils
        .getObservationByCodeAfterTime(apiClient, encounterId, "304894100", fourtyEightHoursAgo);

    Optional<Observation> freshestCoolingPadWaterTemperatureFromPast2Hours = ObservationUtils
        .getFreshestByCodeAfterTime(apiClient, encounterId, "304894100", twoHoursAgo);

    Optional<Observation> freshestActualRASSFromPast7Hours = ObservationUtils
        .getFreshestByCodeAfterTime(apiClient, encounterId, "3045000021", sevenHoursAgo);

    /* if freshestTrainOfFour.getValue() == (“0”, “1”, “2”, “3”) then Daily Sedation Interruption
     * Candidate = “No: Receiving NMBA”
     */
    switch (freshestTrainOfFour) {
      case "0":
      case "1":
      case "2":
      case "3":
        return RECEIVING_NMBA;
    }

    /* if freshestSedationWakeUpAction.getValue() == “No: Receiving NMBA” then Daily Sedation
     * Interruption Candidate = “No: Receiving NMBA” else if freshestSedationWakeUpAction.getValue()
     * == “No - Status epilepticus” then Daily Sedation Interruption Candidate = “No: Status
     * Epilepticus” else if freshestSedationWakeUpAction.getValue() == “No - Deep sedation for
     * ventilator tolerance” then Daily Sedation Interruption Candidate = “No: Respiratory
     * Instability”
     */
    switch (freshestWakeUpAction) {
      case "No: Receiving NMBA":
        return RECEIVING_NMBA;
      case "No - Status epilepticus":
        return STATUS_EPILEPTICUS;
      case "No - Deep sedation for ventilator tolerance":
        return RESPIRATORY_INSTABILITY;
    }

    /* if all coolingPadPatientTemperatureFromPast48Hours.getQuantity().getValue <= 36.5c since
     * freshestCoolingPadWaterTemperature.getQuantity().getValue() within the past 2 hours is not
     * null then Daily Sedation Interruption Candidate = “No: Theraputic Hypothermia”
     */
    Observation freshestCoolingPadStatus = null;

    boolean allOverEqualThreeSixPointFive = coolingPadPatientTemperatureFromPast48Hours.size() > 0;
    for (Observation obv : coolingPadPatientTemperatureFromPast48Hours) {
      if (freshestCoolingPadStatus == null
          || ObservationUtils.getEffectiveDate(obv).after(ObservationUtils
              .getEffectiveDate(freshestCoolingPadStatus))
          && ObservationUtils.getEffectiveDate(obv).after(twoHoursAgo)) {
        freshestCoolingPadStatus = obv;
      }

      if (obv.getValue() instanceof QuantityDt) {
        if (((QuantityDt) obv.getValue()).getValue().doubleValue() <= 36.5
            && ObservationUtils.getEffectiveDate(obv).after(now)) {
          allOverEqualThreeSixPointFive = false;
        }
      }
    }
    if (freshestCoolingPadStatus != null && allOverEqualThreeSixPointFive) {
      return THERAPEUTIC_HYPOTHERMIA;
    }

    /* if hypothermiaBlanketOrders is not empty and currentNonMedOrder(hypothermiaBlanketOrders)
     * within the past 24 hours then Daily Sedation Interruption Candidate = “No: Theraputic
     * Hypothermia”
     */
    if (!hypothermiaBlanketOrders.isEmpty()
        && (ProcedureRequestUtils.activeNonMedicationOrder(apiClient, encounterId,
            ProcedureRequestCodeEnum.HYPOTHERMIA_BLANKET_ORDER_1.getCode())
        || ProcedureRequestUtils.activeNonMedicationOrder(apiClient, encounterId,
            ProcedureRequestCodeEnum.HYPOTHERMIA_BLANKET_ORDER_2.getCode()))) {
      return THERAPEUTIC_HYPOTHERMIA;
    }

    /* if freshestCoolingPadStatus.getValue() == “on” within the past 2 hours then Daily Sedation
     * Interruption Candidate = “No: Theraputic Hypothermia”
     */
    if (freshestCoolingPadStatusFromPast25Hours.isPresent()
        && freshestCoolingPadStatusFromPast25Hours.get().getValue().toString().equals("on")
        && ObservationUtils.getEffectiveDate(freshestCoolingPadStatusFromPast25Hours.get())
            .after(twoHoursAgo)) {
      return THERAPEUTIC_HYPOTHERMIA;
    }

    /* if any coolingPadWaterTemperature.getQuantity().getValue() != null then Daily Sedation
     * Interruption Candidate = “No: Theraputic Hypothermia”
     */
    for (Observation obv : coolingPadWaterTemperatureFromPast48Hours) {
      if (obv.getValue() != null) {
        return THERAPEUTIC_HYPOTHERMIA;
      }
    }

    /* if freshestCoolingPadStatus.getValue() == “off” and, since the time of this value, all
     * coolingPadPatientTemperature.getQuantity().getValue() <= 36.5c then Daily Sedation
     * Interruption Candidate = “No: Theraputic Hypothermia”
     */
    if (freshestCoolingPadStatusFromPast25Hours.isPresent()
        && freshestCoolingPadStatusFromPast25Hours.get().getValue().toString().equals("off")) {
      boolean allBelow36p5 = true;
      for (Observation obv : coolingPadPatientTemperatureFromPast48Hours) {
        if (ObservationUtils.getEffectiveDate(obv).after(ObservationUtils
            .getEffectiveDate(freshestCoolingPadStatusFromPast25Hours.get()))
            && obv.getValue() instanceof QuantityDt
            && ((QuantityDt) obv.getValue()).getValue().doubleValue() <= 36.5) {
          allBelow36p5 = false;
        }
      }

      if (allBelow36p5) {
        return THERAPEUTIC_HYPOTHERMIA;
      }

      /* if freshestCoolingPadStatus.getValue() == “off” and, since the time of this value, all
       * temperatureFromPast48Hours.getQuantity().getValue() <= 36.5 then Daily Sedation
       * Interruption Candidate = “No: Theraputic Hypothermia”
       */
      allBelow36p5 = false;
      for (Observation obv : temperatureFromPast48Hours) {
        if (ObservationUtils.getEffectiveDate(obv).after(ObservationUtils
            .getEffectiveDate(freshestCoolingPadStatusFromPast25Hours.get()))
            && obv.getValue() instanceof QuantityDt
            && ((QuantityDt) obv.getValue()).getValue().doubleValue() <= 36.5) {
          allBelow36p5 = false;
        }
      }

      if (allBelow36p5) {
        return THERAPEUTIC_HYPOTHERMIA;
      }
    }

    /*
     * if all coolingPadPatientTemperature.getQuantity().getValue <= 36.5c since
     * freshestCoolingPadWaterTemperature.getQuantity().getValue() within the past 2 hours is not
     * null then Daily Sedation Interruption Candidate = “No: Theraputic Hypothermia”
     */
    if (freshestCoolingPadWaterTemperatureFromPast2Hours.isPresent()) {
      for (Observation obv : coolingPadPatientTemperatureFromPast48Hours) {
        if (freshestCoolingPadWaterTemperatureFromPast2Hours != null
            && ObservationUtils.getEffectiveDate(obv).after(ObservationUtils
                .getEffectiveDate(freshestCoolingPadWaterTemperatureFromPast2Hours.get()))
            && obv.getValue() instanceof QuantityDt
            && ((QuantityDt) obv.getValue()).getValue().doubleValue() <= 36.5) {
          return THERAPEUTIC_HYPOTHERMIA;
        }
      }
    }

    /* if freshestSedationWakeUpAction.getValue() == “No – Theraputic Hypothermia” then Daily
     * Sedation Interruption Candidate = “No: Theraputic Hypothermia”
     */
    if (freshestWakeUpAction.equals("No - Theraputic Hypothermia")) {
      return THERAPEUTIC_HYPOTHERMIA;
    }

    /* if freshestActualRASS.getValue() == (“+2”, “+3”, or “+4”) or if
     * freshestSedationWakeUpAction.getValue() == “No – RASS 2+ or Greater” then Daily Sedation
     * Interruption Candidate = “No: RASS 2+ or Greater” else if
     * freshestSedationWakeUpAction.getValue() == “No - Benzodiazepine infusion for alcohol
     * withdrawal” then Daily Sedation Interruption Candidate = “No: Withdrawal Seizure Risk” else
     * if freshestSedationWakeUpAction.getValue() == “No - Hemodynamic instability” then Daily
     * Sedation Interruption Candidate = “No: Hemodynamic Instability” else if
     * freshestSedationWakeUpAction.getValue() == “No - Other reason (Comment)” then Daily Sedation
     * Interruption Candidate = “No: Other”
     */
    switch (freshestActualRASSFromPast7Hours.get().getValue().toString()) {
      case "+2":
      case "+3":
      case "+4":
        return RASS_2_OR_GREATER;
    }

    switch (freshestWakeUpAction) {
      case "No - RASS 2+ or Greater":
        return RASS_2_OR_GREATER;
      case "No - Benzodiazepine infusion for alcohol withdrawal":
        return WITHDRAWAL_SEIZURE_RISK;
      case "No - Hemodynamic instability":
        return HEMODYNAMIC_INSTABILITY;
      case "No - Other reason (Comment)":
        return OTHER;
    }

    /* if there are one or more MedicationAdministration.identifier.value == (“Continuous Infusion
     * Dexmedetomidine IV”, “Continuous Infusion Propofol IV”, “Continuous Infusion Lorazepam IV” or
     * “Continuous Infusion Midazolam IV”) and activelyInfusing(MedicationAdministrations) then
     * Daily Sedation Interruption Candidate = “Yes”
     */
    List<String> activelyInfusingDrugNames = Arrays.asList(
        MedsSetEnum.CONTINUOUS_INFUSION_DEXMEDETOMIDINE_IV.getCode(),
        MedsSetEnum.CONTINUOUS_INFUSION_PROPOFOL_IV.getCode(),
        MedsSetEnum.CONTINUOUS_INFUSION_LORAZEPAM_IV.getCode(),
        MedsSetEnum.CONTINUOUS_INFUSION_MIDAZOLAM_IV.getCode());
    for (MedicationAdministration admin : medicationAdministrations) {
      for (IdentifierDt ident : MedicationAdministrationUtils.findIdentifiers(admin,
          CodingSystems.UCSF_MEDICATION_GROUP_NAME)) {
        String medsSet = ident.getValue();

        if (activelyInfusingDrugNames.contains(medsSet)) {
          for (IdentifierDt id : admin.getIdentifier()) {
            if (MedicationAdministrationUtils
                .activelyInfusing(apiClient, encounterId, id.getValue())) {
              return YES;
            }
          }
        }
      }
    }

    // None of the specified circumstances were met.
    return INVALID;
  }
}
