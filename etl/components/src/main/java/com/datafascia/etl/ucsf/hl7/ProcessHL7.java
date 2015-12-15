// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.hl7;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.nifi.DependencyInjectingProcessor;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceUpdater;
import com.datafascia.etl.event.ReplayMessages;
import com.datafascia.etl.hl7.HL7MessageProcessor;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.SupportsBatching;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Processor;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.stream.io.StreamUtils;
import org.kohsuke.MetaInfServices;

/**
 * Updates application state in response to HL7 message.
 */
@CapabilityDescription("Updates application state in response to HL7 message.")
@EventDriven
@MetaInfServices(Processor.class)
@SupportsBatching
@Tags({"datafascia", "HL7", "health level 7", "healthcare", "input"})
public class ProcessHL7 extends DependencyInjectingProcessor {

  public static final Relationship FAILURE = new Relationship.Builder()
      .name("failure")
      .description("HL7 message that could not be processed")
      .build();
  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("success")
      .description("HL7 message that was successfully processed")
      .build();

  public static final PropertyDescriptor REPLAY_MESSAGES = new PropertyDescriptor.Builder()
      .name("Replay messages on admit or transfer")
      .description(
          "If true, will replay messages for the encounter when a patient is admited or " +
          "transferred")
      .defaultValue("true")
      .allowableValues("true", "false")
      .build();

  private static final Set<Relationship> RELATIONSHIPS = ImmutableSet.of(FAILURE, SUCCESS);
  private static final List<PropertyDescriptor> PROPERTY_DESCRIPTORS =
      Arrays.asList(REPLAY_MESSAGES);

  @Inject
  private volatile ReplayMessages replayMessages;

  @Inject
  private volatile HL7MessageProcessor hl7MessageProcessor;

  @Inject
  private volatile HarmEvidenceUpdater harmEvidenceUpdater;

  @Override
  public Set<Relationship> getRelationships() {
    return RELATIONSHIPS;
  }

  @Override
  protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return PROPERTY_DESCRIPTORS;
  }

  @Override
  protected void onInjected(ProcessContext context) {
    replayMessages.setEnabled(context.getProperty(REPLAY_MESSAGES).asBoolean());
  }

  private static String readString(ProcessSession session, FlowFile flowFile) {
    byte[] bytes = new byte[(int) flowFile.getSize()];
    session.read(flowFile, input -> StreamUtils.fillBuffer(input, bytes));
    return new String(bytes, StandardCharsets.UTF_8);
  }

  private static Encounter getEncounter(String encounterIdentifier) {
    Encounter encounter = new Encounter();
    encounter.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue(encounterIdentifier);
    encounter.setId(new IdDt(EncounterRepository.generateId(encounter).toString()));
    return encounter;
  }

  private void processTimer(String encounterIdentifier) {
    if (Strings.isNullOrEmpty(encounterIdentifier)) {
      throw new IllegalStateException("Missing encounterIdentifier");
    }

    log.info(
        "Updating harm evidence for encounter ID {}", new Object[] { encounterIdentifier });

    Encounter encounter = getEncounter(encounterIdentifier);
    harmEvidenceUpdater.processTimer(encounter);
  }

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    FlowFile flowFile = session.get();
    if (flowFile == null) {
      return;
    }

    try {
      String message = readString(session, flowFile);
      if (message.isEmpty()) {
        // Empty input message indicates request for timer-based processing of an encounter.
        String encounterIdentifier = flowFile.getAttribute("encounterIdentifier");
        processTimer(encounterIdentifier);
      } else {
        hl7MessageProcessor.accept(message);
      }

      session.transfer(flowFile, SUCCESS);
    } catch (RuntimeException e) {
      log.error("Cannot process {}", new Object[] { flowFile }, e);
      flowFile = session.putAttribute(flowFile, "stackTrace", Throwables.getStackTraceAsString(e));
      flowFile = session.penalize(flowFile);
      session.transfer(flowFile, FAILURE);
    }
  }
}
