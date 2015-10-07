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
import java.util.Optional;


/**
 * Code Status Implementation
 */
public class CodeStatusImpl {
  private static final List<String> qualifyingCodes = Arrays.asList(
      ProcedureRequestCodeEnum.ATTENDING_PARTIAL.getCode(),
      ProcedureRequestCodeEnum.ATTENDING_DNR_DNI.getCode(),
      ProcedureRequestCodeEnum.RESIDENT_DNR_DNI.getCode(),
      ProcedureRequestCodeEnum.RESIDENT_PARTIAL.getCode(),
      ProcedureRequestCodeEnum.FULL.getCode()
  );


  /**
   * Code Status Implementation
   *
   * @param client
   *     API client
   * @param encounterId
   *     encounter to search
   * @return
   *     Most recent code status, or empty if absent.
   **/
  public static Optional<CodeStatus.Value> codeStatusValue(ClientBuilder client,
      String encounterId) {
    List<ProcedureRequest> codeStatusRequests = client.getProcedureRequestClient()
        .searchProcedureRequest(encounterId, null, null);

    List<ProcedureRequest> sortedRequests = Lists.reverse(ProcedureRequestUtils
        .sortProcedureRequests(codeStatusRequests));

    for (ProcedureRequest request : sortedRequests) {
      if (qualifyingCodes.contains(request.getIdentifierFirstRep().getValue())) {
        return Optional.of(CodeStatus.Value.fromValue(request.getIdentifierFirstRep().getValue()));
      }
    }

    return Optional.empty();
  }
}
