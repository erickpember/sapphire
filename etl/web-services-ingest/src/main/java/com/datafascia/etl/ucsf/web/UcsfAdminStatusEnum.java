// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import com.datafascia.domain.fhir.CodingSystems;
import java.util.Arrays;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;

/**
 * Enumeration of know statuses for medication administrations.
 */
@Slf4j
public enum UcsfAdminStatusEnum {
  GIVEN,
  MISSED,
  REFUSED,
  CANCELED,
  CANCELED_ENTRY,
  HELD,
  NEW,
  NEW_BAG,
  RESTARTED,
  STOPPED,
  RATE_CHANGE,
  MAR_HOLD,
  MAR_UNHOLD,
  BOLUS,
  PUSH,
  RATE_VERIFY,
  SEE_ALTERNATIVE,
  PAUSE,
  PAUSED,
  PEND,
  PENDING,
  AUTOMATICALLY_HELD,
  DUE,
  RETURN_TO_PYXIS,
  NOT_GIVEN,
  PATCH_REMOVED,
  NEW_SYRINGE,
  IV_STOP,
  IV_PAUSE,
  IV_RESUME,
  ANESTHESIA_HOLD,
  ANESTHESIA_BAR_CODE,
  HANDOFF,
  ANESTHESIA_VOLUME_ADJUSTMENT,
  DOWNTIME,
  GIVEN_DURING_DOWNTIME,
  STARTED_DURING_DOWNTIME,
  PATCH_APPLIED,
  BOLUS_FROM_INFUSION_PUMP,
  GIVEN_TO_PROVIDER,
  CHEMO_VERIFY,
  CNTRLD_SUBSTANCE_BOLUS,
  PATCH_INTACT,
  STARTED_ON_ANOTHER_UNIT,
  INSULIN_ZERO_DOSE,
  PASTE_APPLIED,
  PASTE_REMOVED,
  PREVIOUSLY_STARTED,
  INFUSION_PUMP_BOLUS,
  CODE_SEDATION_RECORD,
  FINISHED,
  ENDED,
  RX_ENTRY_VERIFY,
  GIVEN_TO_CIRCUIT,
  ANESTHESIA_BOLUS,
  DOSE_RATE_VERIFY,
  PREVIOUSLY_GIVEN,
  INSULIN_PUMP_BOLUSl,
  INSULIN_PUMP_RATE_DOSE_VERIFY,
  INSULIN_PUMP_0_DOSE,
  SINGLE_VERIFY;

  /**
   * Sets the admin status and the reason it was given or not given.
   *
   * @param admin The admin to populate.
   * @param status The status string.
   */
  public static void populateAdminStatus(MedicationAdministration admin, String status) {
    UcsfAdminStatusEnum adminStatusEnum = UcsfAdminStatusEnum.valueOf(status.replace(" ",
        "_").replace("/", "_").toUpperCase(Locale.ENGLISH));

    switch (adminStatusEnum) {
      case GIVEN:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case MISSED:
        admin.setStatus(MedicationAdministrationStatusEnum.ON_HOLD);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_NOT_GIVEN,
            UcsfMedicationUtils.NotGivenReason.NOT_GIVEN.toString())));
        break;
      case REFUSED:
        admin.setStatus(MedicationAdministrationStatusEnum.ON_HOLD);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_NOT_GIVEN,
            UcsfMedicationUtils.NotGivenReason.NOT_GIVEN.toString())));
        break;
      case CANCELED:
        admin.setStatus(MedicationAdministrationStatusEnum.ENTERED_IN_ERROR);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_NOT_GIVEN,
            UcsfMedicationUtils.NotGivenReason.NOT_GIVEN.toString())));
        break;
      case CANCELED_ENTRY:
        admin.setStatus(MedicationAdministrationStatusEnum.ENTERED_IN_ERROR);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_NOT_GIVEN,
            UcsfMedicationUtils.NotGivenReason.NOT_GIVEN.toString())));
        break;
      case HELD:
        admin.setStatus(MedicationAdministrationStatusEnum.ON_HOLD);
        break;
      case NEW:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case NEW_BAG:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case RESTARTED:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case STOPPED:
        admin.setStatus(MedicationAdministrationStatusEnum.STOPPED);
        break;
      case RATE_CHANGE:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case MAR_HOLD:
        admin.setStatus(MedicationAdministrationStatusEnum.ENTERED_IN_ERROR);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_GIVEN,
            UcsfMedicationUtils.GivenReason.SYSTEM_ONLY.toString())));
        break;
      case MAR_UNHOLD:
        admin.setStatus(MedicationAdministrationStatusEnum.ENTERED_IN_ERROR);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_GIVEN,
            UcsfMedicationUtils.GivenReason.SYSTEM_ONLY.toString())));
        break;
      case BOLUS:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case PUSH:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case RATE_VERIFY:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case SEE_ALTERNATIVE:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_NOT_GIVEN,
            UcsfMedicationUtils.NotGivenReason.NOT_GIVEN.toString())));
        break;
      case PAUSE:
        admin.setStatus(MedicationAdministrationStatusEnum.ON_HOLD);
        break;
      case PAUSED:
        admin.setStatus(MedicationAdministrationStatusEnum.ON_HOLD);
        break;
      case PEND:
        admin.setStatus(MedicationAdministrationStatusEnum.ON_HOLD);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_NOT_GIVEN,
            UcsfMedicationUtils.NotGivenReason.NOT_GIVEN.toString())));
        break;
      case PENDING:
        admin.setStatus(MedicationAdministrationStatusEnum.ON_HOLD);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_NOT_GIVEN,
            UcsfMedicationUtils.NotGivenReason.NOT_GIVEN.toString())));
        break;
      case AUTOMATICALLY_HELD:
        admin.setStatus(MedicationAdministrationStatusEnum.ENTERED_IN_ERROR);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_GIVEN,
            UcsfMedicationUtils.GivenReason.SYSTEM_ONLY.toString())));
        break;
      case DUE:
        admin.setStatus(MedicationAdministrationStatusEnum.ENTERED_IN_ERROR);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_NOT_GIVEN,
            UcsfMedicationUtils.NotGivenReason.DUE.toString())));
        break;
      case RETURN_TO_PYXIS:
        admin.setStatus(MedicationAdministrationStatusEnum.ENTERED_IN_ERROR);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_GIVEN,
            UcsfMedicationUtils.GivenReason.SYSTEM_ONLY.toString())));
        break;
      case NOT_GIVEN:
        admin.setStatus(MedicationAdministrationStatusEnum.ON_HOLD);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_NOT_GIVEN,
            UcsfMedicationUtils.NotGivenReason.NOT_GIVEN.toString())));
        break;
      case PATCH_REMOVED:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case NEW_SYRINGE:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case IV_STOP:
        admin.setStatus(MedicationAdministrationStatusEnum.STOPPED);
        break;
      case IV_PAUSE:
        admin.setStatus(MedicationAdministrationStatusEnum.ON_HOLD);
        break;
      case IV_RESUME:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case ANESTHESIA_HOLD:
        admin.setStatus(MedicationAdministrationStatusEnum.ON_HOLD);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_NOT_GIVEN,
            UcsfMedicationUtils.NotGivenReason.NOT_GIVEN.toString())));
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_GIVEN,
            UcsfMedicationUtils.GivenReason.ANESTHESIA.toString())));
        break;
      case ANESTHESIA_BAR_CODE:
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_GIVEN,
            UcsfMedicationUtils.GivenReason.ANESTHESIA.toString())));
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case HANDOFF:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case ANESTHESIA_VOLUME_ADJUSTMENT:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_GIVEN,
            UcsfMedicationUtils.GivenReason.ANESTHESIA.toString())));
        break;
      case DOWNTIME:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case GIVEN_DURING_DOWNTIME:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case STARTED_DURING_DOWNTIME:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case PATCH_APPLIED:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case BOLUS_FROM_INFUSION_PUMP:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case GIVEN_TO_PROVIDER:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_GIVEN,
            UcsfMedicationUtils.GivenReason.PROCEDURE.toString())));
        break;
      case CHEMO_VERIFY:
        admin.setStatus(MedicationAdministrationStatusEnum.ENTERED_IN_ERROR);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_GIVEN,
            UcsfMedicationUtils.GivenReason.SYSTEM_ONLY.toString())));
        break;
      case CNTRLD_SUBSTANCE_BOLUS:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case PATCH_INTACT:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case STARTED_ON_ANOTHER_UNIT:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case INSULIN_ZERO_DOSE:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_NOT_GIVEN,
            UcsfMedicationUtils.NotGivenReason.NOT_GIVEN.toString())));
        break;
      case PASTE_APPLIED:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case PASTE_REMOVED:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case PREVIOUSLY_STARTED:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case INFUSION_PUMP_BOLUS:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case CODE_SEDATION_RECORD:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case FINISHED:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case ENDED:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case RX_ENTRY_VERIFY:
        admin.setStatus(MedicationAdministrationStatusEnum.ENTERED_IN_ERROR);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_GIVEN,
            UcsfMedicationUtils.GivenReason.SYSTEM_ONLY.toString())));
        break;
      case GIVEN_TO_CIRCUIT:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_GIVEN,
            UcsfMedicationUtils.GivenReason.CIRCUIT.toString())));
        break;
      case ANESTHESIA_BOLUS:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_GIVEN,
            UcsfMedicationUtils.GivenReason.ANESTHESIA.toString())));
        break;
      case DOSE_RATE_VERIFY:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case PREVIOUSLY_GIVEN:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case INSULIN_PUMP_BOLUSl:
        admin.setStatus(MedicationAdministrationStatusEnum.COMPLETED);
        break;
      case INSULIN_PUMP_RATE_DOSE_VERIFY:
        admin.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
        break;
      case INSULIN_PUMP_0_DOSE:
        admin.setStatus(MedicationAdministrationStatusEnum.ON_HOLD);
        admin.setReasonNotGiven(Arrays.asList(new CodeableConceptDt(
            CodingSystems.UCSF_REASON_NOT_GIVEN,
            UcsfMedicationUtils.NotGivenReason.NOT_GIVEN.toString())));
        break;
      default:
        log.error("Unhandled admin status " + adminStatusEnum + ". Resulting administration will"
            + "have no \"status\" or \"reason not given\".");
    }
  }
}
