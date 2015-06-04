// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import com.datafascia.common.avro.Deserializer;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.nifi.DependencyInjectingProcessor;
import com.datafascia.domain.event.Event;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;
import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.apache.nifi.annotation.behavior.EventDriven;
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
 * NiFi processor that processes event.
 */
@CapabilityDescription("Processes event.")
@EventDriven @SupportsBatching @Tags({"event"})
public class ProcessEvent extends DependencyInjectingProcessor {

  private static final String SUBJECT = "event";

  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("success")
      .description("Successfully processed FlowFiles")
      .build();

  private Set<Relationship> relationships;

  @Inject
  private volatile AvroSchemaRegistry schemaRegistry;

  @Inject
  private volatile Deserializer deserializer;

  @Inject
  private volatile AdmitPatient admitPatient;

  @Inject
  private volatile DischargePatient dischargePatient;

  @Inject
  private volatile AddObservations addObservations;

  @Override
  protected void init(ProcessorInitializationContext context) {
    relationships = ImmutableSet.of(SUCCESS);
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
    FlowFile flowFile = session.get();
    if (flowFile == null) {
      return;
    }

    AtomicReference<Event> eventReference = new AtomicReference<>();
    session.read(flowFile, input -> {
        byte[] content = ByteStreams.toByteArray(input);
        eventReference.set(deserializer.decodeReflect(SUBJECT, content, Event.class));
      });
    Event event = eventReference.get();

    switch (event.getType()) {
      case PATIENT_ADMIT:
        admitPatient.accept(event);
        break;
      case PATIENT_DISCHARGE:
        dischargePatient.accept(event);
        break;
      case OBSERVATIONS_ADD:
        addObservations.accept(event);
        break;
      default:
        getLogger().debug("Ignored event type {}", new Object[] { event.getType() });
    }

    session.transfer(flowFile, SUCCESS);
  }
}
