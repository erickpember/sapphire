// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.domain.fhir.Dates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Convenience methods on collection of observations.
 */
public class Observations {

  public static final Comparator<Observation> EFFECTIVE_COMPARATOR = Comparator.nullsFirst(
      Comparator.comparing(
          observation -> Dates.toDate(observation.getEffective()),
          Dates.getDateComparator()));

  private List<Observation> observations;
  private Multimap<String, Observation> codeToObservationsMap;

  /**
   * Constructor
   *
   * @param observations
   *     observations
   */
  public Observations(List<Observation> observations) {
    this.observations = observations;

    int estimatedSize = observations.size() / 2 + 1;
    codeToObservationsMap = ArrayListMultimap.create(estimatedSize, estimatedSize);
    for (Observation observation : observations) {
      codeToObservationsMap.put(getCode(observation), observation);
    }
  }

  private static String getCode(Observation observation) {
    return observation.getCode().getCodingFirstRep().getCode();
  }

  private Stream<Observation> filter(Set<String> codes) {
    if (codes.size() == 1) {
      return codeToObservationsMap.get(codes.iterator().next())
          .stream();
    }

    return observations.stream()
        .filter(observation -> codes.contains(getCode(observation)));
  }

  /**
   * Filters observations.
   *
   * @param codes
   *     observation codes to match
   * @param effectiveLower
   *     effective date time lower bound (inclusive), null for no lower bound
   * @param effectiveUpper
   *     effective date time upper bound (exclusive), null for no upper bound
   * @return filtered observations
   */
  public Stream<Observation> filterToStream(
      Set<String> codes, Instant effectiveLower, Instant effectiveUpper) {

    Stream<Observation> stream = filter(codes);

    if (effectiveLower != null) {
      Date effectiveLowerDate = Date.from(effectiveLower);
      stream = stream.filter(
          observation -> !Dates.toDate(observation.getEffective()).before(effectiveLowerDate));
    }

    if (effectiveUpper != null) {
      Date effectiveUpperDate = Date.from(effectiveUpper);
      stream = stream.filter(
          observation -> Dates.toDate(observation.getEffective()).before(effectiveUpperDate));
    }

    return stream;
  }

  /**
   * Filters observations.
   *
   * @param code
   *     observation code to match
   * @param effectiveLower
   *     effective date time lower bound (inclusive), null for no lower bound
   * @param effectiveUpper
   *     effective date time upper bound (exclusive), null for no upper bound
   * @return filtered observations
   */
  public Stream<Observation> filterToStream(
      String code, Instant effectiveLower, Instant effectiveUpper) {

    return filterToStream(Collections.singleton(code), effectiveLower, effectiveUpper);
  }

  /**
   * Filters observations.
   *
   * @param codes
   *     observation codes to match
   * @param effectiveLower
   *     effective date time lower bound (inclusive), null for no lower bound
   * @param effectiveUpper
   *     effective date time upper bound (exclusive), null for no upper bound
   * @return filtered observations
   */
  public List<Observation> filter(
      Set<String> codes, Instant effectiveLower, Instant effectiveUpper) {

    return filterToStream(codes, effectiveLower, effectiveUpper)
        .collect(Collectors.toList());
  }

  /**
   * Filters observations.
   *
   * @param code
   *     observation code to match
   * @param effectiveLower
   *     effective date time lower bound (inclusive), null for no lower bound
   * @param effectiveUpper
   *     effective date time upper bound (exclusive), null for no upper bound
   * @return filtered observations
   */
  public List<Observation> filter(String code, Instant effectiveLower, Instant effectiveUpper) {
    return filter(Collections.singleton(code), effectiveLower, effectiveUpper);
  }

  /**
   * Finds freshest observation for given observation codes.
   *
   * @param codes
   *     observation codes to match
   * @param effectiveLower
   *     effective date time lower bound (inclusive), null for no lower bound
   * @param effectiveUpper
   *     effective date time upper bound (exclusive), null for no upper bound
   * @return freshest observation for the given code, or empty if no match is found
   */
  public Optional<Observation> findFreshest(
      Set<String> codes, Instant effectiveLower, Instant effectiveUpper) {

    return filterToStream(codes, effectiveLower, effectiveUpper)
        .max(EFFECTIVE_COMPARATOR);
  }

  /**
   * Finds freshest observation for given observation code.
   *
   * @param code
   *     observation code to match
   * @param effectiveLower
   *     effective date time lower bound (inclusive), null for no lower bound
   * @param effectiveUpper
   *     effective date time upper bound (exclusive), null for no upper bound
   * @return freshest observation for the given code, or empty if no match is found
   */
  public Optional<Observation> findFreshest(
      String code, Instant effectiveLower, Instant effectiveUpper) {

    return findFreshest(Collections.singleton(code), effectiveLower, effectiveUpper);
  }

  /**
   * Finds freshest observation for given observation code.
   *
   * @param code
   *     observation code to match
   * @return freshest observation for the given code, or empty if no match is found
   */
  public Optional<Observation> findFreshest(String code) {
    return findFreshest(Collections.singleton(code), null, null);
  }

  /**
   * Finds freshest observation for given observation code and value.
   *
   * @param code
   *     observation code to match
   * @param value
   *     observation value to match
   * @return found observation, or empty if no match is found
   */
  public Optional<Observation> findFreshest(String code, String value) {
    return filterToStream(Collections.singleton(code), null, null)
        .filter(observation -> value.equals(observation.getValue().toString()))
        .max(EFFECTIVE_COMPARATOR);
  }

  /**
   * Finds any observation for given observation code.
   *
   * @param code
   *     observation code to match
   * @param effectiveLower
   *     effective date time lower bound (inclusive), null for no lower bound
   * @param effectiveUpper
   *     effective date time upper bound (exclusive), null for no upper bound
   * @return filtered observations
   */
  public Optional<Observation> findAny(
      String code, Instant effectiveLower, Instant effectiveUpper) {

    return filterToStream(Collections.singleton(code), effectiveLower, effectiveUpper)
        .findAny();
  }
}
