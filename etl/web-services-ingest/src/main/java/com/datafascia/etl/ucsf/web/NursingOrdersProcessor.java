// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web;

import ca.uhn.fhir.model.dstu2.composite.AnnotationDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.common.nifi.DependencyInjectingProcessor;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.google.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ProcessorLog;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Processor;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.InputStreamCallback;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kohsuke.MetaInfServices;

/**
 * Processor for handling nursing orders at UCSF.
 */
@MetaInfServices(Processor.class)
@Slf4j
@Tags({"ingest", "datafascia", "ucsf", "json"})
public class NursingOrdersProcessor extends DependencyInjectingProcessor {
  private Set<Relationship> relationships;
  private List<PropertyDescriptor> properties;

  @Inject
  private ClientBuilder clientBuilder = null;

  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("SUCCESS")
      .description("Success relationship")
      .build();
  public static final Relationship FAILURE = new Relationship.Builder()
      .name("FAILURE")
      .description("Failure relationship")
      .build();

  @Override
  public Set<Relationship> getRelationships() {
    return relationships;
  }

  @Override
  public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return properties;
  }

  @Override
  public void init(final ProcessorInitializationContext context) {
    List<PropertyDescriptor> initProperties = new ArrayList<>();
    this.properties = Collections.unmodifiableList(initProperties);

    Set<Relationship> initRelationships = new HashSet<>();
    initRelationships.add(SUCCESS);
    initRelationships.add(FAILURE);
    this.relationships = Collections.unmodifiableSet(initRelationships);
  }

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    final ProcessorLog plog = this.getLogger();
    final FlowFile flowfile = session.get();

    session.read(flowfile, new InputStreamCallback() {

      @Override
      public void process(InputStream in) throws IOException {
        try {
          String jsonString = IOUtils.toString(in);
          JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonString);

          Object errorObj = jsonObject.get("Error");
          if (errorObj != null && !errorObj.toString().equals("")) {
            log.error("Nursing order web service error: " + errorObj.toString());
          }

          JSONArray patients = (JSONArray) jsonObject.get("Patient");
          for (Object obj : patients) {
            if (obj instanceof JSONObject) {
              String encounterId = ((JSONObject) obj).get("CSN").toString();
              JSONArray orders = (JSONArray) ((JSONObject) obj).get("Orders");
              for (Object obj2 : orders) {
                if (obj2 instanceof JSONObject) {
                  JSONObject jsonNursing = (JSONObject) obj2;
                  String status = jsonNursing.get("OrderStatus").toString();
                  ProcedureRequest procedure = populateProcedureRequest(jsonNursing, encounterId);

                  ProcedureRequest existingRequest = clientBuilder.getProcedureRequestClient()
                      .getProcedureRequest(procedure.getIdentifierFirstRep().getValue(),
                          encounterId);

                  if (existingRequest == null) {
                    clientBuilder.getProcedureRequestClient().saveProcedureRequest(procedure,
                        encounterId);
                  } else {
                    clientBuilder.getProcedureRequestClient().updateProcedureRequest(procedure,
                        encounterId);
                  }
                }
              }
            }
          }
        } catch (ParseException e) {
          log.error("Error reading json.", e);
          plog.error("Failed to read json string: " + e.getMessage());
          throw new ProcessException(e);
        }
      }
    });
    session.transfer(flowfile, SUCCESS);
  }

  private ProcedureRequest populateProcedureRequest(JSONObject jsonNursing, String encounterId) {
    String status = jsonNursing.get("OrderStatus").toString();
    String orderId = jsonNursing.get("OrderID").toString();
    String procID = jsonNursing.get("ProcID").toString();
    String orderDesc = jsonNursing.get("OrderDesc").toString();
    String startDate = jsonNursing.get("StartDate").toString();
    String discontinuedDate = jsonNursing.get("DiscontinuedDate").toString();

    ProcedureRequest procedureRequest = new ProcedureRequest();
    procedureRequest.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PROCEDURE_REQUEST)
        .setValue(orderId);

    if (status.equalsIgnoreCase("2^SENT")) {
      procedureRequest.setStatus(ProcedureRequestStatusEnum.PROPOSED);
    } else if (status.equalsIgnoreCase("6^HOLDING FOR REFERRAL")) {
      procedureRequest.setStatus(ProcedureRequestStatusEnum.PROPOSED);
    } else if (status.equalsIgnoreCase("7^DENIED APPROVAL")) {
      procedureRequest.setStatus(ProcedureRequestStatusEnum.REJECTED);
    } else if (status.equalsIgnoreCase("4^CANCELED")) {
      procedureRequest.setStatus(ProcedureRequestStatusEnum.REJECTED);
    } else if (status.equalsIgnoreCase("5^COMPLETED")) {
      procedureRequest.setStatus(ProcedureRequestStatusEnum.COMPLETED);
    }

    TimingDt timing = new TimingDt();
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
    TimingDt.Repeat repeat = new TimingDt.Repeat();
    repeat.setBounds(period);
    timing.setRepeat(repeat);
    procedureRequest.setScheduled(timing);

    procedureRequest.setCode(
        new CodeableConceptDt(CodingSystems.PROCEDURE_REQUEST, procID)
            .setText(orderDesc));

    Encounter encounter = clientBuilder.getEncounterClient().getEncounter(encounterId);
    if (encounter != null) {
      procedureRequest.setEncounter(new ResourceReferenceDt(encounter.getId()));
    } else {
      log.warn("Could not find encounter with CSN " + encounterId + ". HAPI FHIR doesn't allow "
          + "dangling references, so the linkage between the encounter and nursing order " + orderId
          + " is lost.");
    }

    List<AnnotationDt> notes = new ArrayList<>();
    Object questions = jsonNursing.get("Questions");
    if (questions != null) {
      for (Object obj : ((JSONArray) questions)) {
        String answer = ((JSONObject) obj).get("Answer").toString();
        notes.add(new AnnotationDt().setText(answer));
      }
    }
    procedureRequest.setNotes(notes);

    return procedureRequest;
  }
}
