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

  /**
   * Compares effective date of observations. Orders empty optionals first.
   *
   * @param left
   *     observation in question
   * @param right
   *     observation we're comparing it to
   * @return true if left is older than right
   */
  public static boolean isEffectiveBefore(Optional<Observation> left, Optional<Observation> right) {
    return EFFECTIVE_COMPARATOR.compare(left.orElse(null), right.orElse(null)) < 0;
  }

  /**
   * Compares effective date of observations. Orders empty optionals first.
   *
   * @param left
   *     observation in question
   * @param right
   *     observation we're comparing it to
   * @return true if left is newer than right
   */
  public static boolean isEffectiveAfter(Optional<Observation> left, Optional<Observation> right) {
    return EFFECTIVE_COMPARATOR.compare(left.orElse(null), right.orElse(null)) > 0;
  }

  /**
   * Streams observations
   *
   * @return stream
   */
  public Stream<Observation> stream() {
    return observations.stream();
  }

  private static String getCode(Observation observation) {
    return observation.getCode().getCodingFirstRep().getCode();
  }

  private Stream<Observation> filter(Set<String> codes) {
    if (codes.size() == 1) {
      return codeToObservationsMap.get(codes.iterator().next())
          .stream();
    }

    return stream()
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
  public Stream<Observation> filter(
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
  public Stream<Observation> filter(
      String code, Instant effectiveLower, Instant effectiveUpper) {

    return filter(Collections.singleton(code), effectiveLower, effectiveUpper);
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
  public List<Observation> list(
      Set<String> codes, Instant effectiveLower, Instant effectiveUpper) {

    return filter(codes, effectiveLower, effectiveUpper)
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
  public List<Observation> list(String code, Instant effectiveLower, Instant effectiveUpper) {
    return list(Collections.singleton(code), effectiveLower, effectiveUpper);
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

    return filter(codes, effectiveLower, effectiveUpper)
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
    return filter(Collections.singleton(code), null, null)
        .filter(observation -> value.equals(observation.getValue().toString()))
        .max(EFFECTIVE_COMPARATOR);
  }

  /**
   * Finds freshest observation for given observation code and value, within a time window.
   *
   * @param code
   *     observation code to match
   * @param value
   *     observation value to match
   * @param effectiveLower
   *     effective date time lower bound (inclusive), null for no lower bound
   * @param effectiveUpper
   *     effective date time upper bound (exclusive), null for no upper bound
   * @return found observation, or empty if no match is found
   */
  public Optional<Observation> findFreshest(String code, String value, Instant effectiveLower,
      Instant effectiveUpper) {
    return filter(Collections.singleton(code), effectiveLower, effectiveUpper)
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

    return filter(Collections.singleton(code), effectiveLower, effectiveUpper)
        .findAny();
  }
}
