// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web;

import ca.uhn.fhir.model.dstu2.composite.AnnotationDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceUpdater;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Transforms UCSF nursing orders to procedure requests.
 */
@Slf4j
public class NursingOrdersTransformer {
  public static JSONObjectOrderedDateComparator jsonDateTimeJsonCompare
      = new JSONObjectOrderedDateComparator();

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private HarmEvidenceUpdater harmEvidenceUpdater;

  /**
   * A comparator class for medication order JSON blobs.
   */
  public static class JSONObjectOrderedDateComparator implements Comparator<JSONObject> {
    /**
     * Compares two nursing order JSON blob dates.
     *
     * @param o1 The first blob.
     * @param o2 The second blob.
     * @return The comparator result.
     */
    @Override
    public int compare(JSONObject o1, JSONObject o2) {
      String ucsfTime1 = o1.get("OrderedDate").toString();
      Instant dateTimeOrdered1 = UcsfWebGetProcessor.epicDateToInstant(ucsfTime1);
      String ucsfTime2 = o2.get("OrderedDate").toString();
      Instant dateTimeOrdered2 = UcsfWebGetProcessor.epicDateToInstant(ucsfTime2);

      return dateTimeOrdered1.compareTo(dateTimeOrdered2);
    }
  }

  private ProcedureRequest toProcedureRequest(JSONObject order, Encounter encounter) {
    String status = order.get("OrderStatus").toString();
    String orderId = order.get("OrderID").toString();
    String procID = order.get("ProcID").toString();
    String orderDesc = order.get("OrderDesc").toString();
    String orderedDate = order.get("OrderedDate").toString();
    String startDate = order.get("StartDate").toString();
    String discontinuedDate = order.get("DiscontinuedDate").toString();

    ProcedureRequest procedureRequest = new ProcedureRequest()
        .setEncounter(new ResourceReferenceDt(encounter));
    procedureRequest.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PROCEDURE_REQUEST)
        .setValue(orderId);

    if (status.equalsIgnoreCase("2^SENT")) {
      procedureRequest.setStatus(ProcedureRequestStatusEnum.IN_PROGRESS);
    } else if (status.equalsIgnoreCase("6^HOLDING FOR REFERRAL")) {
      procedureRequest.setStatus(ProcedureRequestStatusEnum.PROPOSED);
    } else if (status.equalsIgnoreCase("7^DENIED APPROVAL")) {
      procedureRequest.setStatus(ProcedureRequestStatusEnum.REJECTED);
    } else if (status.equalsIgnoreCase("4^CANCELED")) {
      procedureRequest.setStatus(ProcedureRequestStatusEnum.REJECTED);
    } else if (status.equalsIgnoreCase("5^COMPLETED")) {
      procedureRequest.setStatus(ProcedureRequestStatusEnum.COMPLETED);
    }

    Instant orderedInstant = UcsfWebGetProcessor.epicDateToInstant(orderedDate);
    procedureRequest.setOrderedOn(new DateTimeDt(Date.from(orderedInstant)));

    PeriodDt period = new PeriodDt();
    /* Null or otherwise 0 dates (they manifest differently from system to system) may throw
     * exceptions which can be safely ignored. We only care about real data. */
    try {
      Instant startInstant = UcsfWebGetProcessor.epicDateToInstant(startDate);
      period.setStart(new DateTimeDt(Date.from(startInstant)));
    } catch (DateTimeException e) {
    }

    // The string here is a null date, which we don't want to use.
    if (!discontinuedDate.equals("/Date(-62135568000000-0800)/")) {
      try {
        Instant endInstant = UcsfWebGetProcessor.epicDateToInstant(discontinuedDate);
        period.setEnd(new DateTimeDt(Date.from(endInstant)));
      } catch (DateTimeException e) {
      }
    }

    procedureRequest.setScheduled(period);

    procedureRequest.setCode(
        new CodeableConceptDt(CodingSystems.PROCEDURE_REQUEST, procID)
        .setText(orderDesc));

    List<AnnotationDt> notes = new ArrayList<>();
    Object questions = order.get("Questions");
    if (questions != null) {
      for (Object obj : ((JSONArray) questions)) {
        String answer = ((JSONObject) obj).get("Answer").toString();
        notes.add(new AnnotationDt().setText(answer));
      }
    }
    procedureRequest.setNotes(notes);

    return procedureRequest;
  }

  private void save(ProcedureRequest request) {
    String requestId = request.getIdentifierFirstRep().getValue();
    String encounterId = request.getEncounter().getReference().getIdPart();
    Optional<ProcedureRequest> existingRequest = apiClient.getProcedureRequestClient()
        .read(requestId, encounterId);

    if (existingRequest.isPresent()) {
      request.setId(existingRequest.get().getId());
      apiClient.getProcedureRequestClient().update(request);
      apiClient.invalidateProcedureRequests(encounterId);
    } else {
      apiClient.getProcedureRequestClient().create(request);
      apiClient.invalidateProcedureRequests(encounterId);
    }
  }

  private void process(JSONObject order, String encounterId) {
    Encounter encounter;
    try {
      encounter = apiClient.getEncounterClient().getEncounter(encounterId);
    } catch (ResourceNotFoundException e) {
      log.warn(
          "Encounter ID [{}] not found. Discarded nursing order ID {}",
          encounterId,
          order.get("OrderID"));
      return;
    }

    ProcedureRequest request = toProcedureRequest(order, encounter);
    save(request);
    harmEvidenceUpdater.updateProcedureRequest(request, encounter);
  }

  /**
   * Transforms UCSF nursing order to procedure request.
   *
   * @param jsonString
   *     nursing order in JSON format
   */
  public void accept(String jsonString) {
    JSONObject jsonObject;
    try {
      jsonObject = (JSONObject) new JSONParser().parse(jsonString);
    } catch (ParseException e) {
      throw new IllegalStateException("Cannot parse JSON " + jsonString, e);
    }

    Object errorObj = jsonObject.get("Error");
    if (errorObj != null && !errorObj.toString().equals("")) {
      log.error("Nursing order web service error: " + errorObj.toString());
    }

    JSONArray patients = (JSONArray) jsonObject.get("Patient");
    for (Object patient : patients) {
      if (patient instanceof JSONObject) {
        String encounterId = ((JSONObject) patient).get("CSN").toString();
        JSONArray orders = (JSONArray) ((JSONObject) patient).get("Orders");

        Collections.sort(orders, jsonDateTimeJsonCompare);

        for (Object order : orders) {
          if (order instanceof JSONObject) {
            process((JSONObject) order, encounterId);
          }
        }
      }
    }
  }
}
