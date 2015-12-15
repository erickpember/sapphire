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
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.ProcedureRequestUtils;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.datafascia.emerge.ucsf.codes.ProcedureRequestCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.Getter;

/**
 * Daily Sedation Interruption Candidate
 */
public class DailySedationInterruptionCandidate {

  /**
   * Result container for daily sedation interruption candidate.
   */
  @Getter
  public enum CandidateResult {
    OFF_SEDATION(false, "Off Sedation"),
    RECEIVING_NMBA(false, "Receiving NMBA"),
    STATUS_EPILEPTICUS(false, "Status Epilepticus"),
    RESPIRATORY_INSTABILITY(false, "Respiratory Instability"),
    THERAPEUTIC_HYPOTHERMIA(false, "Therapeutic Hypothermia"),
    RASS_2_OR_GREATER(false, "RASS 2+ or Greater"),
    WITHDRAWAL_SEIZURE_RISK(false, "Withdrawal Seizure Risk"),
    HEMODYNAMIC_INSTABILITY(false, "Hemodynamic Instability"),
    OTHER(false, "Other"),
    YES(true, null),
    INVALID(false, "No specified conditions were met");

    CandidateResult(boolean candidate, String notCandidateReason) {
      this.candidate = candidate;
      this.notCandidateReason = notCandidateReason;
    }

    private boolean candidate;
    private String notCandidateReason;
  }

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
    boolean activelyInfusingSedative = false;
    if (MedicationAdministrationUtils.activelyInfusing(medicationAdministrations,
        MedsSetEnum.ANY_SEDATIVE_INFUSION.getCode())) {
      // Save this for returning YES later on.
      activelyInfusingSedative = true;
    } else {
      return CandidateResult.OFF_SEDATION;
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
    if (MedicationAdministrationUtils.beenAdministered(medicationAdministrations, twoHourPeriod,
        MedsSetEnum.INTERMITTENT_CISATRACURIUM_IV.getCode())
        || MedicationAdministrationUtils.beenAdministered(medicationAdministrations, twoHourPeriod,
            MedsSetEnum.INTERMITTENT_VECURONIUM_IV.getCode())
        || MedicationAdministrationUtils.beenAdministered(medicationAdministrations, twoHourPeriod,
            MedsSetEnum.INTERMITTENT_ROCURONIUM_IV.getCode())
        || MedicationAdministrationUtils.beenAdministered(medicationAdministrations, twoHourPeriod,
            MedsSetEnum.INTERMITTENT_PANCURONIUM_IV.getCode())) {
      return CandidateResult.RECEIVING_NMBA;
    }

    /* if there are any MedicationAdministration resources where
     * MedicationAdministration.identifier.value == (“Continuous Infusion Cisatracurium IV”,
     * “Continuous Infusion Vecuronium IV”) and activelyInfusing(MedicationAdministrations) then
     * Daily Sedation Interruption Candidate = “No: Receiving NMBA”
     */
    for (MedicationAdministration admin : medicationAdministrations) {
      for (IdentifierDt id : admin.getIdentifier()) {
        if (MedicationAdministrationUtils
            .activelyInfusing(medicationAdministrations, id.getValue())) {
          switch (admin.getIdentifierFirstRep().getValue()) {
            case "Continuous Infusion Cisatracurium IV":
            case "Continuous Infusion Vecuronium IV":
              return CandidateResult.RECEIVING_NMBA;
          }
        }
      }
    }

    // Gather observations needed.
    String freshestTrainOfFour = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient, encounterId, ObservationCodeEnum.TRAIN_OF_FOUR.getCode(), twoHoursAgo)
        .map(observation -> observation.getValue().toString())
        .orElse("");

    String freshestWakeUpAction = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient, encounterId, ObservationCodeEnum.SEDATION_WAKE_UP.getCode(), twentyFiveHoursAgo)
        .map(observation -> observation.getValue().toString())
        .orElse("");

    List<ProcedureRequest> hypothermiaBlanketOrders = ProcedureRequestUtils.getByCodeAfterTime(
        apiClient, encounterId, ProcedureRequestCodeEnum.HYPOTHERMIA_BLANKET_ORDER_1.getCode(),
        twentyFiveHoursAgo);
    hypothermiaBlanketOrders.addAll(ProcedureRequestUtils.getByCodeAfterTime(
        apiClient, encounterId, ProcedureRequestCodeEnum.HYPOTHERMIA_BLANKET_ORDER_2.getCode(),
        twentyFiveHoursAgo));

    Optional<Observation> freshestCoolingPadStatusFromPast25Hours = ObservationUtils
        .getFreshestByCodeAfterTime(apiClient, encounterId,
            ObservationCodeEnum.COOLING_PAD_STATUS.getCode(), twentyFiveHoursAgo);

    List<Observation> temperatureFromPast48Hours = ObservationUtils.getObservationByCodeAfterTime(
        apiClient, encounterId, ObservationCodeEnum.TEMPERATURE.getCode(), fourtyEightHoursAgo);

    List<Observation> coolingPadPatientTemperatureFromPast48Hours = ObservationUtils
        .getObservationByCodeAfterTime(apiClient, encounterId,
            ObservationCodeEnum.COOLING_PAD_PATIENT_TEMPERATURE.getCode(), fourtyEightHoursAgo);

    List<Observation> coolingPadWaterTemperatureFromPast48Hours = ObservationUtils
        .getObservationByCodeAfterTime(apiClient, encounterId,
            ObservationCodeEnum.COOLING_PAD_WATER_TEMPERATURE.getCode(), fourtyEightHoursAgo);

    Optional<Observation> freshestCoolingPadWaterTemperatureFromPast2Hours = ObservationUtils
        .getFreshestByCodeAfterTime(apiClient, encounterId,
            ObservationCodeEnum.COOLING_PAD_WATER_TEMPERATURE.getCode(), twoHoursAgo);

    Optional<Observation> freshestActualRASSFromPast7Hours = ObservationUtils
        .getFreshestByCodeAfterTime(apiClient, encounterId, ObservationCodeEnum.ACTUAL_RASS
            .getCode(), sevenHoursAgo);

    /* if freshestTrainOfFour.getValue() == (“0”, “1”, “2”, “3”) then Daily Sedation Interruption
     * Candidate = “No: Receiving NMBA”
     */
    switch (freshestTrainOfFour) {
      case "0":
      case "1":
      case "2":
      case "3":
        return CandidateResult.RECEIVING_NMBA;
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
        return CandidateResult.RECEIVING_NMBA;
      case "No - Status epilepticus":
        return CandidateResult.STATUS_EPILEPTICUS;
      case "No - Deep sedation for ventilator tolerance":
        return CandidateResult.RESPIRATORY_INSTABILITY;
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
      return CandidateResult.THERAPEUTIC_HYPOTHERMIA;
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
      return CandidateResult.THERAPEUTIC_HYPOTHERMIA;
    }

    /* if freshestCoolingPadStatus.getValue() == “on” within the past 2 hours then Daily Sedation
     * Interruption Candidate = “No: Theraputic Hypothermia”
     */
    if (freshestCoolingPadStatusFromPast25Hours.isPresent()
        && freshestCoolingPadStatusFromPast25Hours.get().getValue().toString().equals("on")
        && ObservationUtils.getEffectiveDate(freshestCoolingPadStatusFromPast25Hours.get())
            .after(twoHoursAgo)) {
      return CandidateResult.THERAPEUTIC_HYPOTHERMIA;
    }

    /* if any coolingPadWaterTemperature.getQuantity().getValue() != null then Daily Sedation
     * Interruption Candidate = “No: Theraputic Hypothermia”
     */
    for (Observation obv : coolingPadWaterTemperatureFromPast48Hours) {
      if (obv.getValue() != null) {
        return CandidateResult.THERAPEUTIC_HYPOTHERMIA;
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
        return CandidateResult.THERAPEUTIC_HYPOTHERMIA;
      }

      /* if freshestCoolingPadStatus.getValue() == “off” and, since the time of this value, all
       * temperatureFromPast48Hours.getQuantity().getValue() <= 36.5 then Daily Sedation
       * Interruption Candidate = “No: Theraputic Hypothermia”
       */
      allBelow36p5 = false;
      for (Observation obv : temperatureFromPast48Hours) {
        if (freshestCoolingPadStatusFromPast25Hours.isPresent()
            && ObservationUtils.getEffectiveDate(obv).after(ObservationUtils
                .getEffectiveDate(freshestCoolingPadStatusFromPast25Hours.get()))
            && obv.getValue() instanceof QuantityDt
            && ((QuantityDt) obv.getValue()).getValue().doubleValue() <= 36.5) {
          allBelow36p5 = false;
        }
      }

      if (allBelow36p5) {
        return CandidateResult.THERAPEUTIC_HYPOTHERMIA;
      }
    }

    /*
     * if all coolingPadPatientTemperature.getQuantity().getValue <= 36.5c since
     * freshestCoolingPadWaterTemperature.getQuantity().getValue() within the past 2 hours is not
     * null then Daily Sedation Interruption Candidate = “No: Theraputic Hypothermia”
     */
    if (freshestCoolingPadWaterTemperatureFromPast2Hours.isPresent()) {
      for (Observation obv : coolingPadPatientTemperatureFromPast48Hours) {
        if (ObservationUtils.getEffectiveDate(obv).after(ObservationUtils
                .getEffectiveDate(freshestCoolingPadWaterTemperatureFromPast2Hours.get()))
            && obv.getValue() instanceof QuantityDt
            && ((QuantityDt) obv.getValue()).getValue().doubleValue() <= 36.5) {
          return CandidateResult.THERAPEUTIC_HYPOTHERMIA;
        }
      }
    }

    /* if freshestSedationWakeUpAction.getValue() == “No – Theraputic Hypothermia” then Daily
     * Sedation Interruption Candidate = “No: Theraputic Hypothermia”
     */
    if (freshestWakeUpAction.equals("No - Theraputic Hypothermia")) {
      return CandidateResult.THERAPEUTIC_HYPOTHERMIA;
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
    if (freshestActualRASSFromPast7Hours.isPresent()) {
      switch (freshestActualRASSFromPast7Hours.get().getValue().toString()) {
        case "+2":
        case "+3":
        case "+4":
          return CandidateResult.RASS_2_OR_GREATER;
      }
    }

    switch (freshestWakeUpAction) {
      case "No - RASS 2+ or Greater":
        return CandidateResult.RASS_2_OR_GREATER;
      case "No - Benzodiazepine infusion for alcohol withdrawal":
        return CandidateResult.WITHDRAWAL_SEIZURE_RISK;
      case "No - Hemodynamic instability":
        return CandidateResult.HEMODYNAMIC_INSTABILITY;
      case "No - Other reason (Comment)":
        return CandidateResult.OTHER;
    }

    /* if there are one or more MedicationAdministration.identifier.value == (“Continuous Infusion
     * Dexmedetomidine IV”, “Continuous Infusion Propofol IV”, “Continuous Infusion Lorazepam IV” or
     * “Continuous Infusion Midazolam IV”) and activelyInfusing(MedicationAdministrations) then
     * Daily Sedation Interruption Candidate = “Yes”
     */
    if (activelyInfusingSedative) {
      return CandidateResult.YES;
    }

    // None of the specified circumstances were met.
    return CandidateResult.INVALID;
  }
}
