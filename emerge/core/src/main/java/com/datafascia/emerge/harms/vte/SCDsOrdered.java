// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ProcedureUtils;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
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

  private static Optional<DateTimeDt> getStartTime(ProcedureRequest request) {
    if (request != null) {
      IDatatype scheduled = request.getScheduled();
      if (scheduled instanceof DateTimeDt) {
        return Optional.of((DateTimeDt) scheduled);
      } else if (scheduled instanceof PeriodDt) {
        return Optional.of(((PeriodDt) scheduled).getStartElement());
      }
    }
    return Optional.empty();
  }

  /**
   * SCDs Ordered Implementation
   *
   * @param encounterId
   *     encounter to search
   * @return true if SCDs have been ordered
   */
  public boolean isSCDsOrdered(String encounterId) {
    boolean ordered = false;

    List<ProcedureRequest> requests = apiClient.getProcedureRequestClient()
        .searchProcedureRequest(
            encounterId, null, ProcedureRequestStatusEnum.IN_PROGRESS.getCode());

    Optional<DateTimeDt> placeStart =
        getStartTime(ProcedureUtils.findFreshestPlaceSCDs(requests));
    Optional<DateTimeDt> maintainStart =
        getStartTime(ProcedureUtils.findFreshestMaintainSCDs(requests));
    Optional<DateTimeDt> removeStart =
        getStartTime(ProcedureUtils.findFreshestRemoveSCDs(requests));

    Date now = Date.from(Instant.now(clock));
    if (!removeStart.isPresent()) {
      if ((placeStart.isPresent() && !now.before(placeStart.get().getValue())) ||
          (maintainStart.isPresent() && !now.before(maintainStart.get().getValue()))) {
        ordered = true;
      }
    } else {
      // there is at least one RemoveSCD and one PlaceSCD or MaintainSCD
      if ((placeStart.isPresent() && !now.before(placeStart.get().getValue()) &&
          (removeStart.get().getValue()).before(placeStart.get().getValue())) ||
          (maintainStart.isPresent() && !now.before(maintainStart.get().getValue()) &&
          (removeStart.get().getValue()).before(maintainStart.get().getValue()))) {
        ordered = true;
      }
    }
    return ordered;
  }
}
