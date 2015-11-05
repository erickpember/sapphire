// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.aog;

import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.CodeStatus;
import com.datafascia.emerge.ucsf.ProcedureRequestUtils;
import com.datafascia.emerge.ucsf.codes.ProcedureRequestCodeEnum;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

/**
 * Code Status Implementation
 */
public class CodeStatusImpl {

  private static final List<String> QUALIFYING_CODES = Arrays.asList(
      ProcedureRequestCodeEnum.ATTENDING_PARTIAL.getCode(),
      ProcedureRequestCodeEnum.ATTENDING_DNR_DNI.getCode(),
      ProcedureRequestCodeEnum.RESIDENT_DNR_DNI.getCode(),
      ProcedureRequestCodeEnum.RESIDENT_PARTIAL.getCode(),
      ProcedureRequestCodeEnum.FULL.getCode()
  );

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
      if (QUALIFYING_CODES.contains(request.getIdentifierFirstRep().getValue())) {
        return CodeStatus.Value.fromValue(request.getIdentifierFirstRep().getValue());
      }
    }

    return CodeStatus.Value.NO_CURRENT_CODE_STATUS;
  }
}
