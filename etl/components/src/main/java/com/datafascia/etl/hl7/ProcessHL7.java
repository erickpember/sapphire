// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import com.datafascia.common.nifi.DependencyInjectingProcessor;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;
import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.SupportsBatching;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;

/**
 * Updates application state in response to HL7 message.
 */
@CapabilityDescription("Updates application state in response to HL7 message.")
@EventDriven
@SupportsBatching
@Tags({"HL7", "health level 7", "healthcare"})
public class ProcessHL7 extends DependencyInjectingProcessor {

  public static final Relationship FAILURE = new Relationship.Builder()
      .name("failure")
      .description("Input FlowFiles that could not be processed")
      .build();
  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("success")
      .description("Input FlowFiles that were successfully processed")
      .build();

  private Set<Relationship> relationships = ImmutableSet.of(FAILURE, SUCCESS);

  @Inject
  private volatile HL7MessageProcessor hl7MessageProcessor;

  @Override
  public Set<Relationship> getRelationships() {
    return relationships;
  }

  @Override
  protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return Collections.emptyList();
  }

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    FlowFile messageFlowFile = session.get();
    if (messageFlowFile == null) {
      return;
    }

    try {
      AtomicReference<byte[]> messageReference = new AtomicReference<>();
      session.read(messageFlowFile, input -> messageReference.set(ByteStreams.toByteArray(input)));
      byte[] message = messageReference.get();

      hl7MessageProcessor.accept(message);

      session.transfer(messageFlowFile, SUCCESS);
    } catch (IllegalStateException | NullPointerException e) {
      log.error("Cannot process {}", new Object[] { messageFlowFile }, e);
      messageFlowFile = session.penalize(messageFlowFile);
      session.transfer(messageFlowFile, FAILURE);
    }
  }
}
