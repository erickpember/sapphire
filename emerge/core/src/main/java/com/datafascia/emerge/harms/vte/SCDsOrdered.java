// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.ProcedureRequests;
import com.datafascia.emerge.ucsf.codes.ProcedureRequestCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * VTE SCDs Ordered Implementation
 */
public class SCDsOrdered {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if procedure request is relevant to SCDs Ordered.
   *
   * @param request
   *     the procedureRequest to check
   * @return true if procedureRequest is relevant to SCDs Ordered.
   */
  public static boolean isRelevant(ProcedureRequest request) {
    return (ProcedureRequestCodeEnum.PLACE_SCDS.isCodeEquals(request.getCode()) ||
            ProcedureRequestCodeEnum.MAINTAIN_SCDS.isCodeEquals(request.getCode()) ||
            ProcedureRequestCodeEnum.REMOVE_SCDS.isCodeEquals(request.getCode()));
  }

  /**
   * SCDs Ordered Implementation
   *
   * @param encounterId
   *     encounter to search
   * @return true if SCDs have been ordered
   */
  public boolean isSCDsOrdered(String encounterId) {
    Instant now = Instant.now(clock);
    List<ProcedureRequest> requests = apiClient.getProcedureRequestClient()
        .search(
            encounterId, null, ProcedureRequestStatusEnum.IN_PROGRESS.getCode());

    Encounter encounter = apiClient.getEncounterClient().getEncounter(encounterId);

    Instant encounterStartTime = encounter.getPeriod().getStart().toInstant();

    return isSCDsOrdered(requests, encounterStartTime, now);
  }

  /**
   * SCDs Ordered Implementation
   *
   * @param procedureRequests
   *     procedure requests for the encounter
   * @param effectiveLowerBound
   *     start time of the encounter
   * @param effectiveUpperBound
   *     current time
   * @return true if SCDs have been ordered
   */
  public boolean isSCDsOrdered(List<ProcedureRequest> procedureRequests,
      Instant effectiveLowerBound, Instant effectiveUpperBound) {
    ProcedureRequests requests = new ProcedureRequests(procedureRequests);

    boolean ordered = false;

    Optional<ProcedureRequest> placeStart = requests.findFreshest(
        ProcedureRequestCodeEnum.PLACE_SCDS.getCode(),
        effectiveLowerBound, effectiveUpperBound);
    Optional<ProcedureRequest> maintainStart = requests.findFreshest(
        ProcedureRequestCodeEnum.MAINTAIN_SCDS.getCode(),
        effectiveLowerBound, effectiveUpperBound);
    Optional<ProcedureRequest> removeStart = requests.findFreshest(
        ProcedureRequestCodeEnum.REMOVE_SCDS.getCode(),
        effectiveLowerBound, effectiveUpperBound);

    if (!removeStart.isPresent()) {
      if (placeStart.isPresent() || maintainStart.isPresent()) {
        ordered = true;
      }
    } else {
      // there is at least one RemoveSCD
      if (ProcedureRequests.isScheduledBefore(removeStart, placeStart) ||
          ProcedureRequests.isScheduledBefore(removeStart, maintainStart)) {
        ordered = true;
      }
    }
    return ordered;
  }
}
