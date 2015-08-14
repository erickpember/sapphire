// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import ca.uhn.hl7v2.model.Message;
import com.datafascia.common.avro.Serializer;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.nifi.DependencyInjectingProcessor;
import com.datafascia.domain.event.Event;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;
import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.behavior.SupportsBatching;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;

/**
 * NiFi processor that transforms HL7 message to events.
 */
@CapabilityDescription("Transforms HL7 message to events.")
@EventDriven @SideEffectFree @SupportsBatching @Tags({"HL7", "health level 7", "healthcare"})
public class TransformMessageToEvent extends DependencyInjectingProcessor {

  private static final String SUBJECT = "event";

  public static final Relationship EVENT = new Relationship.Builder()
      .name("event")
      .description("Transformed output FlowFiles")
      .build();
  public static final Relationship FAILURE = new Relationship.Builder()
      .name("failure")
      .description("Input FlowFiles that could not be transformed")
      .build();
  public static final Relationship ORIGINAL = new Relationship.Builder()
      .name("original")
      .description("Input FlowFiles that were successfully transformed")
      .build();

  private Set<Relationship> relationships;

  @Inject
  private volatile MessageToEventService messageToEventService;

  @Inject
  private volatile AvroSchemaRegistry schemaRegistry;

  @Inject
  private volatile Serializer serializer;

  private volatile Map<Class<? extends Message>, MessageToEventTransformer>
      messageTypeToTransformerMap;

  @Override
  protected void init(ProcessorInitializationContext context) {
    relationships = ImmutableSet.of(EVENT, FAILURE, ORIGINAL);
  }

  @Override
  public Set<Relationship> getRelationships() {
    return relationships;
  }

  @Override
  protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return Collections.emptyList();
  }

  @Override
  protected void onInjected(ProcessContext context) {
    Schema schema = ReflectData.get().getSchema(Event.class);
    schemaRegistry.putSchema(SUBJECT, schema);
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

      List<Event> events = messageToEventService.toEvents(message);

      List<FlowFile> eventFlowFiles = new ArrayList<>();
      for (Event event : events) {
        byte[] content = serializer.encodeReflect(SUBJECT, event);
        FlowFile eventFlowFile = session.create(messageFlowFile);
        eventFlowFile = session.write(eventFlowFile, output -> {
            output.write(content);
          });
        eventFlowFiles.add(eventFlowFile);
      }

      session.transfer(eventFlowFiles, EVENT);
      session.transfer(messageFlowFile, ORIGINAL);
    } catch (IllegalStateException | NullPointerException e) {
      log.error("Cannot transform {}", new Object[] { messageFlowFile }, e);
      messageFlowFile = session.penalize(messageFlowFile);
      session.transfer(messageFlowFile, FAILURE);
    }
  }
}
