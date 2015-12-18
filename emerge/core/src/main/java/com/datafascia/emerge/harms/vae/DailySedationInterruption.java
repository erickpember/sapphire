// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.MedicationAdministrationEffectiveTimeComparator;
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Daily sedation interruption.
 */
@Slf4j
public class DailySedationInterruption {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if patient's sedation was interrupted in the last 25 hours.
   *
   * @param encounterId
   *     encounter to search
   * @return true if patient's sedation was interrupted
   */
  public boolean test(String encounterId) {
    Instant now = Instant.now(clock);
    Date effectiveLowerBound = Date.from(now.minus(25, ChronoUnit.HOURS));

    // If the patient was woken up, then return true.
    Optional<Observation> sedationWakeUpAction = ObservationUtils
        .getFreshestByCodeAfterTime(
            apiClient,
            encounterId,
            ObservationCodeEnum.SEDATION_WAKE_UP.getCode(),
            effectiveLowerBound);

    List<MedicationAdministration> admins = apiClient.getMedicationAdministrationClient()
        .search(encounterId);

    return process(effectiveLowerBound, sedationWakeUpAction, admins);
  }

  /**
   * The API-independent logic of the daily sedation interruption test.
   *
   * @param effectiveLowerBound
   *     Time bound for this logic.
   * @param sedationWakeUpAction
   *     Freshest observation with the sedation wake up code, if present.
   * @param admins
   *     All administrations for the encounter.
   * @return true if patient's sedation was interrupted
   */
  public boolean process(Date effectiveLowerBound, Optional<Observation> sedationWakeUpAction,
      List<MedicationAdministration> admins) {
    if (sedationWakeUpAction.isPresent() && "Yes".equals(ObservationUtils.getValueAsString(
        sedationWakeUpAction.get()))) {
      return true;
    }

    // Get a list of sedative administrations in the time window without erroneous or empty status.
    List<MedicationAdministration> sortedSedativeAdmins = admins.stream()
        .filter(admin ->
            MedicationAdministrationUtils.hasMedsSet(
                admin, MedsSetEnum.ANY_SEDATIVE_INFUSION.getCode()))
        .filter(admin -> !admin.getEffectiveTime().isEmpty())
        .filter(admin -> MedicationAdministrationUtils.isAfter(admin, effectiveLowerBound))
        .filter(admin ->
            MedicationAdministrationStatusEnum.IN_PROGRESS ==
                admin.getStatusElement().getValueAsEnum() ||
            MedicationAdministrationStatusEnum.ON_HOLD ==
                admin.getStatusElement().getValueAsEnum() ||
            MedicationAdministrationStatusEnum.STOPPED ==
                admin.getStatusElement().getValueAsEnum() ||
            MedicationAdministrationStatusEnum.COMPLETED ==
                admin.getStatusElement().getValueAsEnum())
        .sorted(new MedicationAdministrationEffectiveTimeComparator())
        .collect(Collectors.toList());

    if (admins.isEmpty()) {
      return false;
    }

    boolean dexmedetomidineIsStopped = true;
    boolean propofolIsStopped = true;
    boolean lorazepamIsStopped = true;
    boolean midazolamIsStopped = true;
    boolean sedativesAreStopped = false;
    Date timeEverythingStopped = new Date(Long.MAX_VALUE);

    for (MedicationAdministration admin : sortedSedativeAdmins) {
      if (sedativesAreStopped && !timeEverythingStopped.equals(MedicationAdministrationUtils
          .getEffectiveDate(admin))) {
        // All admins are stopped AND there are no more admins with the same timestamp.
        break;
      }

      if (MedicationAdministrationUtils.hasMedsSet(admin,
          MedsSetEnum.CONTINUOUS_INFUSION_DEXMEDETOMIDINE_IV.getCode())) {
        dexmedetomidineIsStopped = isStopped(admin);
      }
      if (MedicationAdministrationUtils.hasMedsSet(admin,
          MedsSetEnum.CONTINUOUS_INFUSION_PROPOFOL_IV.getCode())) {
        propofolIsStopped = isStopped(admin);
      }
      if (MedicationAdministrationUtils.hasMedsSet(admin,
          MedsSetEnum.CONTINUOUS_INFUSION_LORAZEPAM_IV.getCode())) {
        lorazepamIsStopped = isStopped(admin);
      }
      if (MedicationAdministrationUtils.hasMedsSet(admin,
          MedsSetEnum.CONTINUOUS_INFUSION_MIDAZOLAM_IV.getCode())) {
        midazolamIsStopped = isStopped(admin);
      }
      sedativesAreStopped = dexmedetomidineIsStopped && propofolIsStopped && lorazepamIsStopped
          && midazolamIsStopped;

      if (sedativesAreStopped) {
        timeEverythingStopped = MedicationAdministrationUtils.getEffectiveDate(admin);
      }
    }

    return sedativesAreStopped;
  }

  private static boolean isStopped(MedicationAdministration admin) {
    return MedicationAdministrationStatusEnum.IN_PROGRESS !=
        admin.getStatusElement().getValueAsEnum();
  }
}
