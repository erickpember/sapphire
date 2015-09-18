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
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * VTE SCDs Ordered Implementation
 */
public class SCDsOrdered {
  private static Optional<DateTimeDt> getStartTime(ProcedureRequest request) {
    if (request != null) {
      IDatatype timing = request.getTiming();
      if (timing instanceof DateTimeDt) {
        return Optional.of((DateTimeDt) timing);
      } else if  (timing instanceof PeriodDt) {
        return Optional.of(((PeriodDt) timing).getStartElement());
      }
    }
    return Optional.empty();
  }


  /**
   * SCDs Ordered Implementation
   *
   * @param client
   *     the FHIR client used to query Topaz
   * @param encounterId
   *     the Encounter to search
   * @return ordered
   *     indicates whether SCDs have been ordered
   */
  public static boolean scdsOrdered(ClientBuilder client, String encounterId) {
    boolean ordered = false;
    Date now = new Date();

    List<ProcedureRequest> requests = client.getProcedureRequestClient()
        .searchProcedureRequest
            (encounterId, null, ProcedureRequestStatusEnum.IN_PROGRESS.getCode());

    Optional<DateTimeDt> placeStart =
        getStartTime(ProcedureUtils.findFreshestPlaceSCDs(requests));
    Optional<DateTimeDt> maintainStart =
        getStartTime(ProcedureUtils.findFreshestMaintainSCDs(requests));
    Optional<DateTimeDt> removeStart =
        getStartTime(ProcedureUtils.findFreshestRemoveSCDs(requests));

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
