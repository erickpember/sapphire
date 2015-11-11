// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.aog;

import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.CodeStatus;
import com.datafascia.emerge.ucsf.ProcedureRequestUtils;
import com.datafascia.emerge.ucsf.codes.CodeStatusCodeMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.inject.Inject;

/**
 * Code Status Implementation
 */
public class CodeStatusImpl {

  @Inject
  private ClientBuilder apiClient;

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
        .searchProcedureRequest(encounterId, null, null);

    List<ProcedureRequest> sortedRequests = Lists.reverse(ProcedureRequestUtils
        .sortProcedureRequests(codeStatusRequests));

    for (ProcedureRequest request : sortedRequests) {
      String code = request.getCode().getCodingFirstRep().getCode();
      if (code != null &&
          (code.equals("82935") || code.equals("82934") ||
           code.equals("521") || code.equals("519") || code.equals("517"))) {
        return CodeStatus.Value.fromValue(CodeStatusCodeMap.getName(code));
      }
    }

    return CodeStatus.Value.NO_CURRENT_CODE_STATUS;
  }
}
