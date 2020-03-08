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
package com.datafascia.emerge.harms.aog;

import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.CodeStatus;
import com.datafascia.emerge.ucsf.ProcedureRequestUtils;
import com.datafascia.emerge.ucsf.codes.CodeStatusCodeMap;
import com.google.common.collect.Lists;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

/**
 * Code Status Implementation
 */
public class CodeStatusImpl {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
  * Returns true if a procedure request has a code that indicates code status.
  *
  * @param request
  *     Resource to check.
  * @return
  *     True if this request is relevant to code status. Otherwise false.
  */
  public static boolean isRelevant(ProcedureRequest request) {
    String code = request.getCode().getCodingFirstRep().getCode();
    if (code != null && (code.equals("82935") || code.equals("82934") || code.equals("521") || code
        .equals("519") || code.equals("517"))) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Gets most recent code status.
   *
   * @param encounterId
   *     encounter to search
   * @return
   *     code status
   **/
  public CodeStatus.Value apply(String encounterId) {
    List<ProcedureRequest> codeStatusRequests = apiClient.getProcedureRequestClient()
        .search(encounterId, null, null);

    List<ProcedureRequest> sortedRequests = Lists.reverse(ProcedureRequestUtils
        .sortProcedureRequests(codeStatusRequests));

    for (ProcedureRequest request : sortedRequests) {
      if (ProcedureRequestUtils.isCurrent(request, Date.from(Instant.now(clock))) &&
          isRelevant(request)) {
        return CodeStatus.Value.fromValue(CodeStatusCodeMap.getName(request.getCode()
            .getCodingFirstRep().getCode()));
      }
    }

    return CodeStatus.Value.NO_CURRENT_CODE_STATUS;
  }
}
