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
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.codes.ProcedureRequestCodeEnum;
import java.util.List;
import javax.inject.Inject;

/**
 * Utilities for Lower Extremity SCD
 */
public class LowerExtremitySCDsContraindicatedImpl {

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if procedure request is relevant to lower extremity SCDs contraindicated.
   *
   * @param request
   *     the procedure request to check
   * @return true if procedure request is relevant to lower extremity SCDs contraindicated.
   */
  public static boolean isRelevant(ProcedureRequest request) {
    return ProcedureRequestCodeEnum.VTE_PPX_CONTRAINDICATIONS.isCodeEquals(request.getCode());
  }

  /**
   * Gets Lower Extremity SCDs Contraindicated.
   *
   * @param encounterId
   *     Encounter to search.
   * @return A reason for lower extremity SCDs contraindicated, or null if none.
   */
  public String getLowerExtremitySCDsContraindicated(String encounterId) {
    List<ProcedureRequest> requests = apiClient.getProcedureRequestClient()
        .search(encounterId);

    for (ProcedureRequest request : requests) {
      if (ProcedureRequestCodeEnum.VTE_PPX_CONTRAINDICATIONS.isCodeEquals(request.getCode()) &&
          request.getStatusElement().getValueAsEnum() == ProcedureRequestStatusEnum.ACCEPTED) {
        List<AnnotationDt> notes = request.getNotes();
        for (AnnotationDt note : notes) {
          String text = note.getText();
          if (text.equals("High risk of skin breakdown or arterial insufficiency")) {
            return text;
          } else if (text.equals("Bilateral amputee")) {
            return "Amputations";
          }
        }
      }
    }
    return null;
  }
}
