// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.ProcedureRequestUtils;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * Pharmacologic VTE Prophylaxis Contraindicated Implementation
 */
public class ProphylaxisContraindicated {
  private static final String NULL_RESULT = "No EHR mechanical VTE prophylaxis contraindication";

  /**
   * Pharmacologic VTE Prophylaxis Contraindicated Implementation
   *
   * @param client ClientBuilder to use.
   * @param encounterId Encounter to search.
   * @return A string.
   **/
  public static String prophylaxisContraindicated(ClientBuilder client, String encounterId) {
    String type = "VTE Ppx Contraindications";
    List<ProcedureRequest> inProgressPpxRequests = client.getProcedureRequestClient()
        .searchProcedureRequest(encounterId, type,
            ProcedureRequestStatusEnum.IN_PROGRESS.getCode());
    ProcedureRequest freshestInProgressPpxRequest = ProcedureRequestUtils.
        findFreshestProcedureRequest(inProgressPpxRequests);

    // if the request happens after now, throw it out
    if (toDate(freshestInProgressPpxRequest.getTiming()).compareTo(new Date()) > 0) {
      return NULL_RESULT;
    }

    List<StringDt> notes = freshestInProgressPpxRequest.getNotes();
    for (StringDt note : notes) {
      switch (note.getValue()) {
        case "High risk or recurrent CNS procedure/drain":
          return "High risk or recurrent CNS procedure/drain";
        case "Allergy to heparin or HIT":
          return "Allergy to heparin or HIT";
      }
    }

    if (plateletCountLessThan50000(client, encounterId)) {
      return "Platelet count <50,000";
    }

    if (inrOver1point5(client, encounterId)) {
      return "INR >1.5";
    }

    if (aPttRatioOver1point3(client, encounterId)) {
      return "aPTT Ratio >1.3";
    }

    return NULL_RESULT;
  }

  /**
   * Platelet count <50,000 Implementation
   *
   * @param client
   *  API client.
   * @param encounterId
   *  Relevant encounter ID.
   * @return true if conditions are met
   */
  private static boolean plateletCountLessThan50000(ClientBuilder client, String encounterId) {
    String code = "PLT";
    List<Observation> pltObservations = client.getObservationClient().searchObservation(encounterId,
        code, null);
    Observation freshestPltObservation = ObservationUtils.findFreshestObservation(pltObservations);
    IDatatype quantity = freshestPltObservation.getValue();
    if (quantity instanceof QuantityDt) {
      return ((QuantityDt) quantity).getValue().compareTo(new BigDecimal(50000)) < 0;
    } else {
      throw new NumberFormatException("Observation value is not of type QuantityDt.");
    }
  }

  /**
   * INR >1.5 Implementation
   *
   * @param client
   *  API client.
   * @param encounterId
   *  Relevant encounter ID.
   * @return true if conditions are met
   */
  private static boolean inrOver1point5(ClientBuilder client, String encounterId) {
    String code = "INR";
    List<Observation> pltObservations = client.getObservationClient().searchObservation(encounterId,
        code, null);
    Observation freshestPltObservation = ObservationUtils.findFreshestObservation(pltObservations);
    IDatatype quantity = freshestPltObservation.getValue();
    if (quantity instanceof QuantityDt) {
      return ((QuantityDt) quantity).getValue().compareTo(new BigDecimal(1.5)) > 0;
    } else {
      throw new NumberFormatException("Observation value is not of type QuantityDt.");
    }
  }

  /**
   * aPTT Ratio >1.3 Implementation
   *
   * @param client
   *  API client.
   * @param encounterId
   *  Relevant encounter ID.
   * @return true if conditions are met
   */
  private static boolean aPttRatioOver1point3(ClientBuilder client, String encounterId) {
    String code = "PTT";
    List<Observation> pltObservations = client.getObservationClient().searchObservation(encounterId,
        code, null);
    Observation freshestPltObservation = ObservationUtils.findFreshestObservation(pltObservations);
    IDatatype quantity = freshestPltObservation.getValue();
    if (quantity instanceof QuantityDt) {
      return ((QuantityDt) quantity).getValue().compareTo(new BigDecimal(1.3)) > 0;
    } else {
      throw new NumberFormatException("Observation value is not of type QuantityDt.");
    }
  }

  private static Date toDate(IDatatype value) {
    return ((DateTimeDt) value).getValue();
  }
}
