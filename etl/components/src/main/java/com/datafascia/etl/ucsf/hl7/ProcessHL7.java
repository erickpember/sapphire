// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.hl7;

import com.datafascia.common.nifi.DependencyInjectingProcessor;
import com.datafascia.etl.event.PlayMessages;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
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

  private static final String ENCOUNTER_IDENTIFIER = "encounterIdentifier";

  public static final Relationship FAILURE = new Relationship.Builder()
      .name("failure")
      .description("HL7 message that could not be processed")
      .build();
  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("success")
      .description("HL7 message that was successfully processed")
      .build();

  private static final Set<Relationship> RELATIONSHIPS = ImmutableSet.of(FAILURE, SUCCESS);

  @Inject
  private volatile PlayMessages playMessages;

  @Override
  public Set<Relationship> getRelationships() {
    return RELATIONSHIPS;
  }

  @Override
  protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return Collections.emptyList();
  }

  @Override
  protected void onInjected(ProcessContext processContext) {
    playMessages.initializeLastProcessedMessageIds();
  }

  private static String readString(ProcessSession session, FlowFile flowFile) {
    byte[] bytes = new byte[(int) flowFile.getSize()];
    session.read(flowFile, input -> StreamUtils.fillBuffer(input, bytes));
    return new String(bytes, StandardCharsets.UTF_8);
  }

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    FlowFile flowFile = session.get();
    if (flowFile == null) {
      return;
    }

    try {
      readString(session, flowFile);

      String encounterIdentifier = flowFile.getAttribute(ENCOUNTER_IDENTIFIER);
      if (Strings.isNullOrEmpty(encounterIdentifier)) {
        throw new IllegalStateException("FlowFile does not have attribute " + ENCOUNTER_IDENTIFIER);
      }

      playMessages.accept(encounterIdentifier);

      session.transfer(flowFile, SUCCESS);
    } catch (RuntimeException e) {
      log.error("Cannot process {}", new Object[] { flowFile }, e);
      flowFile = session.putAttribute(flowFile, "stackTrace", Throwables.getStackTraceAsString(e));
      flowFile = session.penalize(flowFile);
      session.transfer(flowFile, FAILURE);
    }
  }
}
