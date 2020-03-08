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
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.AnnotationDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.harms.HarmsLookups;
import com.datafascia.emerge.ucsf.ProcedureRequestUtils;
import com.datafascia.emerge.ucsf.codes.ProcedureRequestCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

/**
 * Pharmacologic VTE Prophylaxis Contraindicated Implementation
 */
public class ProphylaxisContraindicated {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Gets pharmacologic VTE prophylaxis contraindicated reason
   *
   * @param encounter
   *     encounter to search
   * @return pharmacologic VTE prophylaxis contraindicated reason, or {@code null} if not found
   */
  public String getProphylaxisContraindicatedReason(Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();
    Observations observations = apiClient.getObservationClient().list(encounterId);

    if (HarmsLookups.plateletCountLessThan50000(observations, null)) {
      return "Platelet Count <50,000";
    }

    List<ProcedureRequest> inProgressPpxRequests = apiClient.getProcedureRequestClient()
        .search(encounterId,
            ProcedureRequestCodeEnum.VTE_PPX_CONTRAINDICATIONS.getCode(),
            ProcedureRequestStatusEnum.IN_PROGRESS.getCode());
    ProcedureRequest freshestInProgressPpxRequest = ProcedureRequestUtils.
        findFreshestProcedureRequest(inProgressPpxRequests);
    if (freshestInProgressPpxRequest == null) {
      return null;
    }

    // if the request happens after now, throw it out
    Date now = Date.from(Instant.now(clock));
    if (ProcedureRequestUtils.isScheduledAfter(freshestInProgressPpxRequest, now)) {
      return null;
    }

    List<AnnotationDt> notes = freshestInProgressPpxRequest.getNotes();
    for (AnnotationDt note : notes) {
      switch (note.getText()) {
        case "High bleeding risk or recent CNS procedure/drain":
          return "High risk or recurrent CNS procedure/drain";
        case "Allergy to heparin or HIT":
          return "Allergy to heparin or HIT";
      }
    }

    return null;
  }
}
