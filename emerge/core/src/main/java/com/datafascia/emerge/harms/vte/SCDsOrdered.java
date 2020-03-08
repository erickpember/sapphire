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

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.ProcedureRequests;
import com.datafascia.emerge.ucsf.codes.ProcedureRequestCodeEnum;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * VTE SCDs Ordered Implementation
 */
public class SCDsOrdered {

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
   * @param encounter
   *     encounter to search
   * @return true if SCDs have been ordered
   */
  public boolean isSCDsOrdered(Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();
    List<ProcedureRequest> requests = apiClient.getProcedureRequestClient().search(
        encounterId, null, null);

    return isSCDsOrdered(requests);
  }

  /**
   * SCDs Ordered Implementation
   *
   * @param procedureRequests
   *     procedure requests for the encounter
   * @return true if SCDs have been ordered
   */
  @VisibleForTesting
  boolean isSCDsOrdered(List<ProcedureRequest> procedureRequests) {

    // in this rare case we treat null statuses as in progress
    procedureRequests = procedureRequests.stream()
        .filter(procedurerequest -> procedurerequest.getStatus() == null
            || ProcedureRequestStatusEnum.IN_PROGRESS.getCode().equals(procedurerequest
                .getStatus()))
        .collect(Collectors.toList());
    ProcedureRequests requests = new ProcedureRequests(procedureRequests);

    boolean ordered = false;

    Optional<ProcedureRequest> placeStart = requests.findFreshest(
        ProcedureRequestCodeEnum.PLACE_SCDS.getCode(), null, null);
    Optional<ProcedureRequest> maintainStart = requests.findFreshest(
        ProcedureRequestCodeEnum.MAINTAIN_SCDS.getCode(), null, null);
    Optional<ProcedureRequest> removeStart = requests.findFreshest(
        ProcedureRequestCodeEnum.REMOVE_SCDS.getCode(), null, null);

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
