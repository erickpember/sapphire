// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Observation helper methods
 */
public class ObservationUtils {

  // Private constructor disallows creating instances of this class.
  private ObservationUtils() {
  }

  /**
   * Finds freshest observation.
   *
   * @param observations
   *     observations to search
   * @return freshest observation, or {@code null} if input observations is empty
   */
  public static Observation findFreshestObservation(List<Observation> observations) {
    return observations.stream()
        .max(new ObservationEffectiveComparator())
        .orElse(null);
  }

  /**
   * A date comparator for Observation Objects that handles nulls.
   *
   * @param left
   *     The observation in question.
   * @param right
   *     The observation we're comparing it to.
   * @return
   *     True if left is newer than right, or right is null. Otherwise false.
   */
  public static boolean firstIsFresher(Observation left, Observation right) {
    if (left == null) {
      return false;
    }
    if (right == null) {
      return true;
    }
    ObservationEffectiveComparator comparator = new ObservationEffectiveComparator();
    return comparator.compare(left, right) > 0;
  }

  /**
   * Finds freshest observation for a given Encounter and Code.
   *
   * @param client
   *     API client.
   * @param encounterId
   *     Relevant encounter ID.
   * @param code
   *     Observation code to search for.
   * @return freshest observation for the given code, or {@code null} if no match is found
   */
  public static Observation findFreshestObservationForCode(ClientBuilder client,
      String encounterId, String code) {
    return client.getObservationClient().searchObservation(encounterId, code, null).stream()
        .max(new ObservationEffectiveComparator())
        .orElse(null);
  }

  /**
   * Returns the freshest observation for an encounter with the given code and after the given time.
   *
   * @param client
   *     The client to use.
   * @param encounterId
   *     The encounter to search by.
   * @param code
   *     The code to search by.
   * @param date
   *     The lower time bound.
   * @return An observation.
   */
  public static Observation getFreshestByCodeAfterTime(ClientBuilder client,
      String encounterId, String code, Date date) {
    List<Observation> observations = client.getObservationClient()
        .searchObservation(encounterId, code, null);

    Observation freshtestObservation = ObservationUtils.findFreshestObservation(observations);
    return getEffectiveDate(freshtestObservation).after(date) ? freshtestObservation : null;
  }

  /**
   * Returns the observations for a given encounter with the given code and after the given time.
   *
   * @param client
   *     The client to use.
   * @param encounterId
   *     The encounter to search by.
   * @param code
   *     The code to search by.
   * @param date
   *     The lower time bound.
   * @return A list of observations.
   */
  public static List<Observation> getObservationByCodeAfterTime(ClientBuilder client,
      String encounterId, String code, Date date) {
    List<Observation> observations
        = client.getObservationClient().searchObservation(encounterId, code, null);
    List<Observation> returnList = new ArrayList<>();
    for (Observation obv : observations) {
      if (getEffectiveDate(obv).after(date)) {
        returnList.add(obv);
      }
    }
    return returnList;
  }

  /**
   * Returns the effective date of an observation.
   *
   * @param ob
   *     The observation to pull from.
   * @return The effective date.
   */
  public static Date getEffectiveDate(Observation ob) {
    return ((DateTimeDt) ob.getEffective()).getValue();
  }

  /**
   * Finds freshest observation for a given Encounter and Code.
   *
   * @param client
   *     API client.
   * @param encounterId
   *     Relevant encounter ID.
   * @param code
   *     Observation code to search for.
   * @param value
   *     Filter condition for Observation's value member.
   * @return freshest observation for the given code, or {@code null} if no match is found
   */
  public static Observation findFreshestObservationForCodeAndValue(ClientBuilder client,
      String encounterId, String code, String value) {
    return client.getObservationClient().searchObservation(encounterId, code, null).stream()
        .filter(observation -> observation.getValue().toString().equals(value))
        .max(new ObservationEffectiveComparator())
        .orElse(null);
  }
}
