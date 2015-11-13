// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.AnnotationDt;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
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

  private static Date toDate(IDatatype value) {
    return ((DateTimeDt) value).getValue();
  }

  /**
   * Gets pharmacologic VTE prophylaxis contraindicated reason
   *
   * @param encounterId
   *     encounter to search
   * @return pharmacologic VTE prophylaxis contraindicated reason, or {@code null} if not found
   */
  public String getProphylaxisContraindicatedReason(String encounterId) {
    List<ProcedureRequest> inProgressPpxRequests = apiClient.getProcedureRequestClient()
        .searchProcedureRequest(encounterId,
            ProcedureRequestCodeEnum.VTE_PPX_CONTRAINDICATIONS.getCode(),
            ProcedureRequestStatusEnum.IN_PROGRESS.getCode());
    ProcedureRequest freshestInProgressPpxRequest = ProcedureRequestUtils.
        findFreshestProcedureRequest(inProgressPpxRequests);
    if (freshestInProgressPpxRequest == null) {
      return null;
    }

    // if the request happens after now, throw it out
    Date now = Date.from(Instant.now(clock));
    if (toDate(freshestInProgressPpxRequest.getScheduled()).after(now)) {
      return null;
    }

    List<AnnotationDt> notes = freshestInProgressPpxRequest.getNotes();
    for (AnnotationDt note : notes) {
      switch (note.getText()) {
        case "High risk or recurrent CNS procedure/drain":
          return "High risk or recurrent CNS procedure/drain";
        case "Allergy to heparin or HIT":
          return "Allergy to heparin or HIT";
      }
    }

    if (HarmsLookups.plateletCountLessThan50000(apiClient, encounterId)) {
      return "Platelet Count <50,000";
    }

    if (HarmsLookups.inrOver1point5(apiClient, encounterId)) {
      return "INR >1.5";
    }

    if (HarmsLookups.aPttRatioOver1point5(apiClient, encounterId)) {
      return "aPTT Ratio >1.5";
    }

    return null;
  }
}
