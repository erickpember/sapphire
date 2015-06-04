// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import com.datafascia.common.avro.Serializer;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.nifi.DependencyInjectingProcessor;
import com.datafascia.domain.event.Event;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
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
  private static final URI UNKNOWN = URI.create("UNKNOWN");

  public static final Relationship ORIGINAL = new Relationship.Builder()
      .name("original")
      .description("Original FlowFile containing HL7 message")
      .build();

  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("success")
      .description("Successfully processed FlowFiles")
      .build();

  private Set<Relationship> relationships;

  @Inject
  private volatile Parser parser;

  @Inject
  private volatile AvroSchemaRegistry schemaRegistry;

  @Inject
  private volatile Serializer serializer;

  private volatile Map<Class<? extends Message>, MessageToEventTransformer>
      messageTypeToTransformerMap;

  @Override
  protected void init(ProcessorInitializationContext context) {
    relationships = ImmutableSet.of(ORIGINAL, SUCCESS);
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
    messageTypeToTransformerMap = new HashMap<>();
    ServiceLoader<MessageToEventTransformer> serviceLoader =
        ServiceLoader.load(MessageToEventTransformer.class);
    for (MessageToEventTransformer transformer : serviceLoader) {
      messageTypeToTransformerMap.put(transformer.getApplicableMessageType(), transformer);
      log.debug(
          "loaded transformer of {}",
          new Object[] { transformer.getApplicableMessageType().getName() });
    }

    Schema schema = ReflectData.get().getSchema(Event.class);
    schemaRegistry.putSchema(SUBJECT, schema);
  }

  private Message parseHL7(byte[] bytes) {
    String hl7 = new String(bytes, StandardCharsets.UTF_8);
    try {
      return parser.parse(hl7);
    } catch (HL7Exception e) {
      throw new IllegalStateException("Cannot parse HL7 " + hl7, e);
    }
  }

  private List<Event> toEvents(Message message) {
    MessageToEventTransformer transformer = messageTypeToTransformerMap.get(message.getClass());
    if (transformer == null) {
      log.debug(
          "Do not know how to transform from message type {}",
          new Object[] { message.getClass().getName() });
      return Collections.emptyList();
    }

    List<Event> events = transformer.transform(UNKNOWN, UNKNOWN, message);
    log.debug("Transformed to events {}", new Object[] { events });
    return events;
  }

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    FlowFile messageFlowFile = session.get();
    if (messageFlowFile == null) {
      return;
    }

    AtomicReference<Message> messageReference = new AtomicReference<>();
    session.read(messageFlowFile, input -> {
        byte[] content = ByteStreams.toByteArray(input);
        messageReference.set(parseHL7(content));
      });
    Message message = messageReference.get();

    List<Event> events = toEvents(message);

    List<FlowFile> eventFlowFiles = new ArrayList<>();
    for (Event event : events) {
      byte[] content = serializer.encodeReflect(SUBJECT, event);
      FlowFile eventFlowFile = session.create(messageFlowFile);
      eventFlowFile = session.write(eventFlowFile, output -> {
          output.write(content);
        });
      eventFlowFiles.add(eventFlowFile);
    }

    session.transfer(eventFlowFiles, SUCCESS);
    session.transfer(messageFlowFile, ORIGINAL);
  }
}
