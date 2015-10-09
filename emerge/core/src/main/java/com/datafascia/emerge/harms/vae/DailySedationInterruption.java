// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
import com.datafascia.emerge.ucsf.ObservationUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Daily sedation interruption.
 */
public class DailySedationInterruption {

  /**
   * Returns whether a person's sedation was interrupted in the last 25 hours.
   *
   * @param client The client to use.
   * @param encounterId The encounter to check.
   * @return Whether a person's sedation was interrupted.
   */
  public static boolean dailySedationInterrupted(ClientBuilder client, String encounterId) {
    // We only care about the last 25 hours.
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.HOUR, -25);
    Date twentyFiveHoursAgo = cal.getTime();

    // Check the latest observation if it shows a wakeup action.
    List<Observation> sedationWakeUpActionFromPast25Hours = ObservationUtils
        .getObservationByCodeAfterTime(client, encounterId, "304890033", twentyFiveHoursAgo);
    Observation freshest = ObservationUtils
        .findFreshestObservation(sedationWakeUpActionFromPast25Hours);

    if (freshest.getValue().toString().equals("Yes")) {
      return true;
    }

    // Get a list of administrations and associate them with their respective orders.
    List<MedicationAdministration> admins = client.getMedicationAdministrationClient()
        .search(encounterId);
    List<MedicationAdministration> filteredAdmins = new ArrayList<>();
    Map<String, MedicationOrder> orders = new HashMap<>();
    Map<MedicationAdministration, MedicationOrder> orderAdmins = new HashMap<>();

    // Filter only admins for the meds we care about from the last 25 hours.
    for (MedicationAdministration admin : admins) {
      String adminId = admin.getIdentifierFirstRep().getValue();
      String orderId = admin.getPrescription().getReference().getValue();
      if (adminId.equals("Continuous Infusion Dexmedetomidine IV")
          || adminId.equals("Continuous Infusion Propofol IV")
          || adminId.equals("Continuous Infusion Lorazepam IV")
          || adminId.equals("Continuous Infusion Midazolam IV")) {
        if (((DateTimeDt) admin.getEffectiveTime()).getValue().after(twentyFiveHoursAgo)) {
          filteredAdmins.add(admin);
          MedicationOrder order;
          if (!orders.containsKey(orderId)) {
            order = client.getMedicationOrderClient().read(orderId, encounterId);
          } else {
            order = orders.get(orderId);
          }

          orders.put(orderId, order);
          orderAdmins.put(admin, order);
        }
      }
    }

    // Gather start/stop periods for medications.
    List<TimePeriod> periods = new ArrayList<>();
    for (MedicationOrder order : orders.values()) {
      Date start = Date.from(Instant.MAX);
      Date end = Date.from(Instant.MIN);

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
      String medset = admin.getIdentifierFirstRep().getValue();
      if (MedicationAdministrationUtils.activelyInfusing(client, encounterId, medset)) {
        return false;
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
