// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.AnnotationDt;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import java.util.List;

/**
 * Utilities for Lower Extremity SCD
 */
public class LowerExtremitySCD {

  /**
   * Lower Extremity SCDs Contraindicated Implementation
   *
   * @param client ClientBuilder to use.
   * @param encounterId Encounter to search.
   * @return A reason for lower extremity SCDs contraindicated, or null if none.
   */
  public static String lowerExtremitySCDsContraindicated(ClientBuilder client, String encounterId) {
    List<ProcedureRequest> requests = client.getProcedureRequestClient()
        .getProcedureRequest(encounterId);

    for (ProcedureRequest request : requests) {
      if (request.getCode().getText().equals("VTE Ppx Contraindications") &&
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
