// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.api.client.ClientBuilder;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
   * Finds the freshest observation for an encounter with the given code and after the given time.
   *
   * @param client
   *     client to use
   * @param encounterId
   *     encounter to search
   * @param code
   *     observation code to match
   * @param effectiveLowerBound
   *     observation effective time lower bound
   * @return optional observation, empty if not found
   */
  public static Optional<Observation> getFreshestByCodeAfterTime(
      ClientBuilder client, String encounterId, String code, Date effectiveLowerBound) {

    List<Observation> observations = client.getObservationClient()
        .searchObservation(encounterId, code, null);
    if (observations.isEmpty()) {
      return Optional.empty();
    }

    Observation freshestObservation = findFreshestObservation(observations);
    if (ObservationUtils.isAfter(freshestObservation, effectiveLowerBound)) {
      return Optional.of(freshestObservation);
    }

    return Optional.empty();
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
    return client.getObservationClient().searchObservation(encounterId, code, null).stream().filter(
        observation -> isAfter(observation, date)).collect(Collectors.toList());
  }

  /**
   * Returns the observations for a given encounter with the given code inside a specified time
   * frame.
   *
   * @param client
   *     The client to use.
   * @param encounterId
   *     The encounter to search by.
   * @param code
   *     The code to search by.
   * @param timeFrame
   *     Time window constraint for search.
   * @return
   *     A list of observations.
   */
  public static List<Observation> searchByCodeInTimeFrame(ClientBuilder client,
      String encounterId, String code, PeriodDt timeFrame) {
    return client.getObservationClient().searchObservation(encounterId, code, null).stream().filter(
        observation -> insideTimeFrame(observation, timeFrame)).collect(Collectors.toList());
  }

  /**
   * Returns the observations for a given encounter inside a specified time frame.
   *
   * @param client
   *     The client to use.
   * @param encounterId
   *     The encounter to search by.
   * @param timeFrame
   *     Time window constraint for search.
   * @return
   *     A list of observations.
   */
  public static List<Observation> searchByTimeFrame(ClientBuilder client,
      String encounterId, PeriodDt timeFrame) {
    return client.getObservationClient().searchObservation(encounterId, null, null).stream().filter(
        observation -> insideTimeFrame(observation, timeFrame)).collect(Collectors.toList());
  }

  /**
   * Returns the most recent observation for a given encounter with the given code inside
   * a specified time frame.
   *
   * @param client
   *     The client to use.
   * @param encounterId
   *     The encounter to search by.
   * @param code
   *     The code to search by.
   * @param timeFrame
   *     Time window constraint for search.
   * @return
   *     The most recent observation that fits the conditions.
   */
  public static Observation getFreshestByCodeInTimeFrame(ClientBuilder client,
      String encounterId, String code, PeriodDt timeFrame) {
    return findFreshestObservation(searchByCodeInTimeFrame(client, encounterId, code, timeFrame));
  }

  /**
   * Returns the effective date of an observation.
   *
   * @param observation
   *     observation to pull from
   * @return effective date
   */
  public static Date getEffectiveDate(Observation observation) {
    IDatatype effective = observation.getEffective();
    if (effective instanceof DateTimeDt) {
      return ((DateTimeDt) effective).getValue();
    } else if (effective instanceof PeriodDt) {
      return ((PeriodDt) effective).getStart();
    }

    return null;
  }

  /**
   * Gets observation value as a string.
   *
   * @param observation
   *     observation to pull from
   * @return observation value, or {@code null} if observation is null
   */
  public static String getValueAsString(Observation observation) {
    if (observation == null || observation.getValue() == null) {
      return null;
    }

    return ((StringDt) observation.getValue()).getValue();
  }

  /**
   * Returns true if a specified observation is inside a specified time window,
   * time bounds are beginning-inclusive and end-exclusive.
   *
   * @param observation
   *     Observation  resource.
   * @param timeFrame
   *     Time window constraint for search.
   * @return
   *     True if the supplied  is inside the specified time window.
   */
  public static boolean insideTimeFrame(Observation observation, PeriodDt timeFrame) {
    if (observation == null || observation.getEffective() == null) {
      return false;
    }

    IDatatype effectiveTime = observation.getEffective();
    if (effectiveTime instanceof TimingDt) {
      return (((TimingDt) effectiveTime).getEventFirstRep().getValue()
          .compareTo(timeFrame.getStart()) >= 0)
          && (((TimingDt) effectiveTime).getEventFirstRep().getValue()
          .compareTo(timeFrame.getEnd()) < 0);
    } else if (effectiveTime instanceof PeriodDt) {
      return (((PeriodDt) effectiveTime).getStart()
          .compareTo(timeFrame.getStart()) >= 0)
          && (((PeriodDt) effectiveTime).getEnd()
          .compareTo(timeFrame.getEnd()) < 0);
    } else if (effectiveTime instanceof DateTimeDt) {
      return (((DateTimeDt) effectiveTime).getValue()
          .compareTo(timeFrame.getStart()) >= 0)
          && (((DateTimeDt) effectiveTime).getValue()
          .compareTo(timeFrame.getEnd()) < 0);
    } else {
      throw new RuntimeException("Unexpected type: " + effectiveTime.getClass().getCanonicalName());
    }
  }

  /**
   * Returns true if a specified observation is at or after a specified time.
   *
   * @param observation
   *     Observation  resource.
   * @param startTime
   *     Start time for search.
   * @return
   *     True if the supplied 's effective time is after the specified start time.
   */
  public static boolean isAfter(Observation observation, Date startTime) {
    IDatatype effectiveTime = observation.getEffective();
    if (effectiveTime instanceof TimingDt) {
      return ((TimingDt) effectiveTime).getEventFirstRep().getValue().compareTo(startTime) >= 0;
    } else if (effectiveTime instanceof PeriodDt) {
      return ((PeriodDt) effectiveTime).getStart().compareTo(startTime) >= 0;
    } else if (effectiveTime instanceof DateTimeDt) {
      return ((DateTimeDt) effectiveTime).getValue().compareTo(startTime) >= 0;
    } else {
      throw new RuntimeException("Unexpected type: " + effectiveTime.getClass().getCanonicalName());
    }
  }

  /**
   * Gets the airway name for an observation.
   *
   * @param observation
   *     observation to pull from
   * @return airway name, or {@code null} if not present
   */
  public static String airwayName(Observation observation) {
    String[] propertyParts = observation.getCode().getText().split("-", 2);
    return (propertyParts.length > 1) ? propertyParts[1] : null;
  }
}
