// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * Daily sedation interruption.
 */
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
    Optional<Observation> sedationWakeUpActionFromPast25Hours =
        ObservationUtils.getFreshestByCodeAfterTime(
            apiClient, encounterId, ObservationCodeEnum.SEDATION_WAKE_UP.getCode(),
            effectiveLowerBound);
    if (sedationWakeUpActionFromPast25Hours.isPresent()
        && sedationWakeUpActionFromPast25Hours.get().getValue().toString().equals("Yes")) {
      return true;
    }

    // Get a list of administrations and associate them with their respective orders.
    List<MedicationAdministration> admins = apiClient.getMedicationAdministrationClient()
        .search(encounterId);
    List<MedicationAdministration> filteredAdmins = new ArrayList<>();
    Map<String, MedicationOrder> orders = new HashMap<>();
    Map<MedicationAdministration, MedicationOrder> orderAdmins = new HashMap<>();

    if (admins.isEmpty()) {
      return false;
    }

    // Filter only admins for the meds we care about from the last 25 hours.
    for (MedicationAdministration admin : admins) {
      List<IdentifierDt> identifiers = MedicationAdministrationUtils.findIdentifiers(admin,
          CodingSystems.UCSF_MEDICATION_GROUP_NAME);
      for (IdentifierDt ident : identifiers) {
        String adminId = ident.getValue();
        String orderId = admin.getPrescription().getReference().getValue();
        if (adminId.equals(MedsSetEnum.CONTINUOUS_INFUSION_DEXMEDETOMIDINE_IV.getCode()) ||
            adminId.equals(MedsSetEnum.CONTINUOUS_INFUSION_PROPOFOL_IV.getCode()) ||
            adminId.equals(MedsSetEnum.CONTINUOUS_INFUSION_LORAZEPAM_IV.getCode()) ||
            adminId.equals(MedsSetEnum.CONTINUOUS_INFUSION_MIDAZOLAM_IV.getCode())) {
          if (((DateTimeDt) admin.getEffectiveTime()).getValue().after(effectiveLowerBound)) {
            filteredAdmins.add(admin);
            MedicationOrder order;
            if (!orders.containsKey(orderId)) {
              order = apiClient.getMedicationOrderClient().read(orderId, encounterId);
            } else {
              order = orders.get(orderId);
            }

            orders.put(orderId, order);
            orderAdmins.put(admin, order);
          }
        }
      }
    }

    if (orders.isEmpty() || orderAdmins.isEmpty()) {
      return false;
    }

    // Gather start/stop periods for medications.
    List<TimePeriod> periods = new ArrayList<>();
    for (MedicationOrder order : orders.values()) {
      Date start = new Date(Long.MAX_VALUE);
      Date end = new Date(Long.MIN_VALUE);

      for (MedicationAdministration admin : orderAdmins.keySet()) {
        if (orderAdmins.containsValue(order)) {
          Date effectiveTime = MedicationAdministrationUtils.getEffectiveDate(admin);
          if (admin.getStatus().equalsIgnoreCase("Stopped")) {
            end = effectiveTime;
          } else if (effectiveTime.before(start)) {
            start = effectiveTime;
          }
        }
      }

      periods.add(new TimePeriod(start, end));
    }

    // Sort periods by their start.
    periods = periods.stream()
        .sorted(Comparator.comparing(TimePeriod::getStart))
        .collect(Collectors.toList());

    // Make sure all periods overlap or at least meetup with every other period.
    Date lastEnd = null;
    for (TimePeriod period : periods) {
      if (lastEnd == null) {
        lastEnd = period.getEnd();
      }

      // If the next period starts after the last one ended, then we have a gap.
      if (period.getStart().after(lastEnd)) {
        return true;
      }

      // If it's the default or after the last end, track it.
      if (period.getEnd().after(lastEnd)) {
        lastEnd = period.getEnd();
      }
    }

    // Finally, check if anything is actively administering.
    for (MedicationAdministration admin : filteredAdmins) {
      for (IdentifierDt ident : MedicationAdministrationUtils.findIdentifiers(admin,
          CodingSystems.UCSF_MEDICATION_GROUP_NAME)) {
        String medsSet = ident.getValue();
        if (MedicationAdministrationUtils.activelyInfusing(admins, medsSet)) {
          return false;
        }
      }
    }

    // Nothing is being administered now.
    return true;
  }

  /**
   * Simple container to track time periods.
   */
  private static class TimePeriod {
    private final Date start;
    private final Date end;

    /**
     * Creates a TimePeriod.
     *
     * @param start The start of the period.
     * @param end The end of the period.
     */
    public TimePeriod(Date start, Date end) {
      this.start = start;
      this.end = end;
    }

    /**
     * Gets the start of the period.
     *
     * @return The start of the period.
     */
    public Date getStart() {
      return start;
    }

    /**
     * Gets the end of the period.
     *
     * @return The end of the period.
     */
    public Date getEnd() {
      return end;
    }
  }
}
