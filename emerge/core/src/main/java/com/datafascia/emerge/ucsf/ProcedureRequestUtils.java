// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.Dates;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ProcedureRequest helper methods
 */
public class ProcedureRequestUtils {

  // Private constructor disallows creating instances of this class.
  private ProcedureRequestUtils() {
  }

  /**
   * Compares scheduled date time property of procedure requests.
   *
   * @return comparator
   */
  public static Comparator<ProcedureRequest> getScheduledComparator() {
    return Comparator.nullsFirst(
        Comparator.comparing(
            request -> Dates.toDate(request.getScheduled()),
            Dates.getDateComparator()));
  }

  /**
   * Finds freshest ProcedureRequest.
   *
   * @param procedureRequests
   *     ProcedureRequests to search
   * @return freshest procedureRequest, or {@code null} if input procedureRequests is empty
   */
  public static ProcedureRequest findFreshestProcedureRequest(
      List<ProcedureRequest> procedureRequests) {
    return procedureRequests.stream()
        .max(getScheduledComparator())
        .orElse(null);
  }

  /**
   * Sorts procedure requests
   *
   * @param procedureRequests
   *     ProcedureRequests to search
   * @return
   *     sorted list of procedure request, by timingDateTime, ascending order
   */
  public static List<ProcedureRequest> sortProcedureRequests(
      List<ProcedureRequest> procedureRequests) {
    return procedureRequests.stream()
        .sorted(getScheduledComparator())
        .collect(Collectors.toList());
  }

  /**
   * Given an encounter and a Procedure type code, find the freshest. If the freshest
   * is in progress, return true.
   *
   * @param encounterId
   *    encounter to search for procedure requests
   * @param client
   *    API client
   * @param procedureCode
   *    Code field in ProcedureRequest, identifies the type of procedure for this search.
   * @return
   *    True if the freshest procedure request for this encounter and code has a status of
   *    in progress.
   */
  public static boolean activeNonMedicationOrder(ClientBuilder client, String encounterId,
      String procedureCode) {
    return client.getProcedureRequestClient()
        .search(encounterId, procedureCode, null).stream()
        .max(getScheduledComparator())
        .filter(request -> request.getStatusElement()
            .getValueAsEnum().equals(ProcedureRequestStatusEnum.IN_PROGRESS)).isPresent();
  }

  /**
   * Given a procedure request return true if it is dated before now and is in progress.
   *
   * @param request
   *    The request in question.
   * @param now
   *    The current time.
   * @return
   *    True if the request is before now and in progress. Otherwise false.
   */
  public static boolean isCurrent(ProcedureRequest request, Date now) {
    return (request != null && isScheduledBefore(request, now) && request.getStatusElement()
        .getValueAsEnum().equals(ProcedureRequestStatusEnum.IN_PROGRESS));
  }

  /**
   * Returns true if a specified procedure request is scheduled before a specified time.
   *
   * @param request
   *     Procedure request resource.
   * @param date
   *     Specified time.
   * @return
   *     True if the supplied request is scheduled before a specified time.
   */
  public static boolean isScheduledBefore(ProcedureRequest request, Date date) {
    return (date != null) ? Dates.toDate(request.getScheduled()).before(date) : false;
  }

  /**
   * Returns true if a specified procedure request is scheduled isAfter a specified time.
   *
   * @param request
   *     Procedure request resource.
   * @param date
   *     Specified time.
   * @return
   *     True if the supplied request is scheduled isAfter a specified time.
   */
  public static boolean isScheduledAfter(ProcedureRequest request, Date date) {
    return (date != null) ? Dates.toDate(request.getScheduled()).after(date) : false;
  }

  /**
   * Returns the procedure requests for a given encounter with the given code and isAfter the given
 time.
   *
   * @param client
   *     The client to use.
   * @param encounterId
   *     The encounter to search by.
   * @param code
   *     The code to search by.
   * @param date
   *     The lower time bound.
   * @return A list of procedure requests.
   */
  public static List<ProcedureRequest> getByCodeAfterTime(ClientBuilder client,
      String encounterId, String code, Date date) {
    List<ProcedureRequest> requests
        = client.getProcedureRequestClient().search(encounterId, code, null);
    List<ProcedureRequest> returnList = new ArrayList<>();
    for (ProcedureRequest req : requests) {
      if (isScheduledAfter(req, date)) {
        returnList.add(req);
      }
    }
    return returnList;
  }
}
