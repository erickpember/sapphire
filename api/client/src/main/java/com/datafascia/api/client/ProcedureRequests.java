// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
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
import java.util.stream.Stream;

/**
 * Convenience methods on collection of procedure requests.
 */
public class ProcedureRequests {

  public static final Comparator<ProcedureRequest> SCHEDULED_COMPARATOR = Comparator.nullsFirst(
        Comparator.comparing(
            request -> Dates.toDate(request.getScheduled()),
            Dates.getDateComparator()));

  private final List<ProcedureRequest> procedureRequests;
  private final Multimap<String, ProcedureRequest> codeToProcedureRequestsMap;

  /**
   * Constructor
   *
   * @param procedureRequests
   *     procedure requests
   */
  public ProcedureRequests(List<ProcedureRequest> procedureRequests) {
    this.procedureRequests = procedureRequests;

    int estimatedSize = procedureRequests.size() / 2 + 1;
    codeToProcedureRequestsMap = ArrayListMultimap.create(estimatedSize, estimatedSize);
    for (ProcedureRequest procedureRequest : procedureRequests) {
      codeToProcedureRequestsMap.put(getCode(procedureRequest), procedureRequest);
    }
  }

  private static String getCode(ProcedureRequest procedureRequest) {
    return procedureRequest.getCode().getCodingFirstRep().getCode();
  }

  private Stream<ProcedureRequest> filter(Set<String> codes) {
    if (codes.size() == 1) {
      return codeToProcedureRequestsMap.get(codes.iterator().next())
          .stream();
    }

    return procedureRequests.stream()
        .filter(procedureRequest -> codes.contains(getCode(procedureRequest)));
  }

  /**
   * Filters procedure requests.
   *
   * @param codes
   *     procedure request codes to match
   * @param scheduledLower
   *     scheduled date time lower bound (inclusive), null for no lower bound
   * @param scheduledUpper
   *     scheduled date time upper bound (exclusive), null for no upper bound
   * @return filtered procedure requests
   */
  public Stream<ProcedureRequest> filter(
      Set<String> codes, Instant scheduledLower, Instant scheduledUpper) {

    Stream<ProcedureRequest> stream = filter(codes);

    if (scheduledLower != null) {
      Date scheduledLowerDate = Date.from(scheduledLower);
      stream = stream.filter(
          request -> !Dates.toDate(request.getScheduled()).before(scheduledLowerDate));
    }

    if (scheduledUpper != null) {
      Date scheduledUpperDate = Date.from(scheduledUpper);
      stream = stream.filter(
          request -> Dates.toDate(request.getScheduled()).before(scheduledUpperDate));
    }

    return stream;
  }

  /**
   * Filters procedure requests.
   *
   * @param code
   *     procedure request code to match
   * @param scheduledLower
   *     scheduled date time lower bound (inclusive), null for no lower bound
   * @param scheduledUpper
   *     scheduled date time upper bound (exclusive), null for no upper bound
   * @return filtered procedure requests
   */
  public Stream<ProcedureRequest> filter(
      String code, Instant scheduledLower, Instant scheduledUpper) {

    return filter(Collections.singleton(code), scheduledLower, scheduledUpper);
  }

  /**
   * Finds freshest procedure request for given codes.
   *
   * @param codes
   *     procedure request codes to match
   * @param scheduledLower
   *     scheduled date time lower bound (inclusive), null for no lower bound
   * @param scheduledUpper
   *     scheduled date time upper bound (exclusive), null for no upper bound
   * @return freshest procedureRequest for the given code, or empty if no match is found
   */
  public Optional<ProcedureRequest> findFreshest(
      Set<String> codes, Instant scheduledLower, Instant scheduledUpper) {

    return filter(codes, scheduledLower, scheduledUpper)
        .max(SCHEDULED_COMPARATOR);
  }

  /**
   * Finds freshest procedure request for given code.
   *
   * @param code
   *     procedure request code to match
   * @param scheduledLower
   *     scheduled date time lower bound (inclusive), null for no lower bound
   * @param scheduledUpper
   *     scheduled date time upper bound (exclusive), null for no upper bound
   * @return freshest procedure request for the given code, or empty if no match is found
   */
  public Optional<ProcedureRequest> findFreshest(
      String code, Instant scheduledLower, Instant scheduledUpper) {

    return findFreshest(Collections.singleton(code), scheduledLower, scheduledUpper);
  }

  /**
   * Finds freshest procedure request for given code.
   *
   * @param code
   *     procedureRequest code to match
   * @return freshest procedure request for the given code, or empty if no match is found
   */
  public Optional<ProcedureRequest> findFreshest(String code) {
    return findFreshest(Collections.singleton(code), null, null);
  }

  /**
   * Compares scheduled date of procedure requests. Orders empty optionals first.
   *
   * @param left
   *     request in question
   * @param right
   *     request we're comparing it to
   * @return true if left is older than right
   */
  public static boolean isScheduledBefore(
      Optional<ProcedureRequest> left, Optional<ProcedureRequest> right) {

    return SCHEDULED_COMPARATOR.compare(left.orElse(null), right.orElse(null)) < 0;
  }
}
