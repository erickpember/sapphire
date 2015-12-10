// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.api.client.ClientBuilder;
import com.google.common.base.Strings;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
  public static Observation findFreshest(List<Observation> observations) {
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
  public static Observation findFreshestForCode(ClientBuilder client,
      String encounterId, String code) {
    return client.getObservationClient().searchObservation(encounterId, code, null).stream()
        .max(new ObservationEffectiveComparator())
        .orElse(null);
  }

  /**
   * Finds freshest observation for a given list of observations and code.
   *
   * @param observations
   *     A list of observations for a specific encounter.
   * @param code
   *     Observation code to search for.
   * @return freshest observation for the given code, or {@code null} if no match is found
   */
  public static Observation findFreshestForCode(List<Observation> observations,
      String code) {
    return observations.stream()
        .filter(observation -> !Strings.isNullOrEmpty(code) && code.equals(observation.getCode()
                .getCodingFirstRep().getCode()))
        .max(new ObservationEffectiveComparator())
        .orElse(null);
  }

  /**
   * Filters observations by a list of multiple codes.
   *
   * @param observations
   *     A list of observations for a specific encounter.
   * @param codes
   *     One or many observation codes to search for.
   * @return A list of observations for the given codes, or {@code null} if no match is found.
   */
  public static List<Observation> filterByCodes(List<Observation> observations,
      Set<String> codes) {
    return observations.stream()
        .filter(observation -> codes != null && codes.contains(observation.getCode()
                .getCodingFirstRep().getCode()))
        .collect(Collectors.toList());
  }

  /**
   * Retrieves observations for a given encounter of multiple codes.
   *
   * @param client
   *     client to use
   * @param encounterId
   *     encounter to search
   * @param codes
   *     One or many observation codes to search for.
   * @return A list of observations for the given codes, or {@code null} if no match is found.
   */
  public static List<Observation> getByCodes(ClientBuilder client, String encounterId,
      Set<String> codes) {
    return filterByCodes(client.getObservationClient().searchObservation(encounterId, null, null),
        codes);
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

    Observation freshestObservation = findFreshest(observations);
    if (ObservationUtils.isAfter(freshestObservation, effectiveLowerBound)) {
      return Optional.of(freshestObservation);
    }

    return Optional.empty();
  }

  /**
   * Finds the freshest observation from a list with the given code and after the given time.
   *
   * @param observations
   *     A list of observations for a specific encounter.
   * @param code
   *     observation code to match
   * @param effectiveLowerBound
   *     observation effective time lower bound
   * @return optional observation, empty if not found
   */
  public static Optional<Observation> getFreshestByCodeAfterTime(
      List<Observation> observations, String code, Date effectiveLowerBound) {
    if (observations.isEmpty()) {
      return Optional.empty();
    }

    Observation freshestObservation = ObservationUtils.findFreshestForCode(observations, code);
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
  public static List<Observation> getByCodeAfterTime(ClientBuilder client,
      String encounterId, String code, Date date) {
    return client.getObservationClient().searchObservation(encounterId, code, null).stream().filter(
        observation -> isAfter(observation, date)).collect(Collectors.toList());
  }

  /**
   * Returns the observations from a list with the given code and after the given time.
   *
   * @param observations
   *     A list of observations for a specific encounter.
   * @param code
   *     The code to search by.
   * @param date
   *     The lower time bound.
   * @return A list of observations.
   */
  public static List<Observation> getByCodeAfterTime(List<Observation> observations,
      String code, Date date) {
    return observations.stream()
        .filter(observation -> !Strings.isNullOrEmpty(code)
            && code.equals(observation.getCode()
                .getCodingFirstRep().getCode()))
        .filter(
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
   * Returns the observations from a list with the given code inside a specified time
   * frame.
   *
   * @param observations
   *     A list of observations for a specific encounter.
   * @param code
   *     The code to search by.
   * @param timeFrame
   *     Time window constraint for search.
   * @return
   *     A list of observations.
   */
  public static List<Observation> searchByCodeInTimeFrame(List<Observation> observations,
      String code, PeriodDt timeFrame) {
    return observations.stream()
        .filter(observation -> !Strings.isNullOrEmpty(code)
            && code.equals(observation.getCode()
                .getCodingFirstRep().getCode()))
        .filter(observation -> insideTimeFrame(observation, timeFrame))
                    .collect(Collectors.toList());
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
   * Returns the observations from a list inside a specified time frame.
   *
   * @param observations
   *     A list of observations for a specific encounter.
   * @param timeFrame
   *     Time window constraint for search.
   * @return
   *     A list of observations.
   */
  public static List<Observation> filterByTimeFrame(List<Observation> observations,
      PeriodDt timeFrame) {
    return observations.stream().filter(
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
    return findFreshest(searchByCodeInTimeFrame(client, encounterId, code, timeFrame));
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
  public static Observation findFreshestForCodeAndValue(ClientBuilder client,
      String encounterId, String code, String value) {
    return client.getObservationClient().searchObservation(encounterId, code, null).stream()
        .filter(observation -> observation.getValue().toString().equals(value))
        .max(new ObservationEffectiveComparator())
        .orElse(null);
  }

  /**
   * Finds freshest observation from a list with a given code and value.
   *
   * @param observations
   *     A list of observations for a specific encounter.
   * @param code
   *     Observation code to search for.
   * @param value
   *     Filter condition for Observation's value member.
   * @return freshest observation for the given code, or {@code null} if no match is found
   */
  public static Observation findFreshestForCodeAndValue(List<Observation> observations, String code,
      String value) {
    return observations.stream()
        .filter(observation -> !Strings.isNullOrEmpty(code)
            && code.equals(observation.getCode()
                .getCodingFirstRep().getCode()))
        .filter(observation -> observation.getValue().toString().equals(value))
        .max(new ObservationEffectiveComparator())
        .orElse(null);
  }

  /**
   * Returns true if a specified observation is inside a specified time window.
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
      return ((TimingDt) effectiveTime).getEventFirstRep().getValue().after(timeFrame.getStart())
          && ((TimingDt) effectiveTime).getEventFirstRep().getValue().before(timeFrame.getEnd());
    } else if (effectiveTime instanceof PeriodDt) {
      return ((PeriodDt) effectiveTime).getStart().after(timeFrame.getStart())
          && ((PeriodDt) effectiveTime).getEnd().before(timeFrame.getEnd());
    } else if (effectiveTime instanceof DateTimeDt) {
      return ((DateTimeDt) effectiveTime).getValue().after(timeFrame.getStart())
          && ((DateTimeDt) effectiveTime).getValue().before(timeFrame.getEnd());
    } else {
      throw new RuntimeException("Unexpected type: " + effectiveTime.getClass().getCanonicalName());
    }
  }

  /**
   * Returns true if a specified observation  is after a specified time.
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
      return ((TimingDt) effectiveTime).getEventFirstRep().getValue().after(startTime);
    } else if (effectiveTime instanceof PeriodDt) {
      return ((PeriodDt) effectiveTime).getStart().after(startTime);
    } else if (effectiveTime instanceof DateTimeDt) {
      return ((DateTimeDt) effectiveTime).getValue().after(startTime);
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
