// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
   * Gets Lower Extremity SCDs Contraindicated.
   *
   * @param encounterId
   *     Encounter to search.
   * @return A reason for lower extremity SCDs contraindicated, or null if none.
   */
  public String getLowerExtremitySCDsContraindicated(String encounterId) {
    List<ProcedureRequest> requests = apiClient.getProcedureRequestClient()
        .list(encounterId);

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
