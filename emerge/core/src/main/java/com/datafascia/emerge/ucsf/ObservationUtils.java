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
}
