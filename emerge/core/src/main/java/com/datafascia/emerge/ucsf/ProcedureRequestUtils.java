// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import java.util.ArrayList;
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
   * Finds freshest ProcedureRequest.
   *
   * @param procedureRequests
   *     ProcedureRequests to search
   * @return freshest procedureRequest, or {@code null} if input procedureRequests is empty
   */
  public static ProcedureRequest findFreshestProcedureRequest(
      List<ProcedureRequest> procedureRequests) {
    return procedureRequests.stream()
        .max(new ProcedureRequestScheduledComparator())
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
        .sorted().collect(Collectors.toList());
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
        .searchProcedureRequest(encounterId, procedureCode, null).stream()
        .max(new ProcedureRequestScheduledComparator())
        .filter(request -> request.getStatusElement()
            .getValueAsEnum().equals(ProcedureRequestStatusEnum.IN_PROGRESS)).isPresent();
  }

  /**
   * Given an encounter and a Procedure type code, find the freshest match that is scheduled before
   * now. If the freshest is in progress, return true.
   *
   * @param encounterId
   *    encounter to search for procedure requests
   * @param client
   *    API client
   * @param procedureCode
   *    Code field in ProcedureRequest, identifies the type of procedure for this search.
   * @return
   *    True if the freshest procedure request for this encounter and code and has a status of
   *    in progress.
   */
  public static boolean currentNonMedicationOrder(ClientBuilder client, String encounterId,
      String procedureCode) {
    return client.getProcedureRequestClient()
        .searchProcedureRequest(encounterId, procedureCode, null).stream()
        .max(new ProcedureRequestScheduledComparator())
        .filter(request -> isCurrent(request))
        .isPresent();
  }

  /**
   * Given a procedure request return true if it is dated before now and is in progress.
   *
   * @param request
   *    The request in question.
   * @return
   *    True if the request is before now and in progress. Otherwise false.
   */
  public static boolean isCurrent(ProcedureRequest request) {
    return (request != null && beforeNow(request) && request.getStatusElement()
        .getValueAsEnum().equals(ProcedureRequestStatusEnum.IN_PROGRESS));
  }

  /**
   * Given an encounter and a Procedure type code, find the freshest match that is also
   * scheduled after now. If the freshest is in progress, return true.
   * and a dosage over zero.
   *
   * @param encounterId
   *    encounter to search for procedure requests
   * @param client
   *    API client
   * @param procedureCode
   *    Code field in ProcedureRequest, identifies the type of procedure for this search.
   * @return
   *    True if the freshest procedure request scheduled in the future for this encounter and code
   *    and has a status of in progress.
   */
  public static boolean futureNonMedicationOrder(ClientBuilder client, String encounterId,
      String procedureCode) {
    return client.getProcedureRequestClient()
        .searchProcedureRequest(encounterId, procedureCode, null).stream()
        .filter(request -> afterNow(request))
        .max(new ProcedureRequestScheduledComparator())
        .filter(request -> request.getStatusElement()
            .getValueAsEnum().equals(ProcedureRequestStatusEnum.IN_PROGRESS)).isPresent();
  }

  /**
   * Returns true if a specified procedure request is scheduled before now.
   *
   * @param request
   *     Procedure request resource.
   * @return
   *     True if the supplied request is scheduled before now.
   */
  public static boolean beforeNow(ProcedureRequest request) {
    Date now = new Date();
    IDatatype scheduled = request.getScheduled();
    if (scheduled instanceof TimingDt) {
      return ((TimingDt) scheduled).getEventFirstRep().getValue().before(now);
    } else if (scheduled instanceof PeriodDt) {
      return ((PeriodDt) scheduled).getStart().before(now);
    } else if (scheduled instanceof DateTimeDt) {
      return ((DateTimeDt) scheduled).getValue().before(now);
    } else {
      throw new RuntimeException("Unexpected type: " + scheduled.getClass().getCanonicalName());
    }
  }

  /**
   * Returns true if a specified procedure request is scheduled after now.
   *
   * @param request
   *     Procedure request resource.
   * @return
   *     True if the supplied request is scheduled after now.
   */
  public static boolean afterNow(ProcedureRequest request) {
    Date now = new Date();
    IDatatype scheduled = request.getScheduled();
    if (scheduled instanceof TimingDt) {
      return ((TimingDt) scheduled).getEventFirstRep().getValue().after(now);
    } else if (scheduled instanceof PeriodDt) {
      return ((PeriodDt) scheduled).getStart().after(now);
    } else if (scheduled instanceof DateTimeDt) {
      return ((DateTimeDt) scheduled).getValue().after(now);
    } else {
      throw new RuntimeException("Unexpected type: " + scheduled.getClass().getCanonicalName());
    }
  }

  /**
   * Returns the procedure requests for a given encounter with the given code and after the given
   * time.
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
        = client.getProcedureRequestClient().searchProcedureRequest(encounterId, code, null);
    List<ProcedureRequest> returnList = new ArrayList<>();
    for (ProcedureRequest req : requests) {
      if (req.getOrderedOn().after(date)) {
        returnList.add(req);
      }
    }
    return returnList;
  }
}
