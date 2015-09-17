// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ingest;

import com.datafascia.common.nifi.DependencyInjectingProcessor;
import com.datafascia.domain.model.IngestMessage;
import com.datafascia.domain.persist.IngestMessageRepository;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Instant;
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
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.kohsuke.MetaInfServices;

/**
 * NiFi processor that archives FlowFiles to the IngestMessage table in
 * Accumulo.
 */
@CapabilityDescription("Archives FlowFiles to the IngestMessage table in Accumulo.")
@EventDriven
@MetaInfServices(Processor.class)
@SupportsBatching
@Tags({"archive"})
public class ArchiveIngestMessage extends DependencyInjectingProcessor {

  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("success")
      .description("Successfully processed FlowFiles")
      .build();

  public static final PropertyDescriptor INSTITUTION_ID = new PropertyDescriptor.Builder()
      .name("Institution ID")
      .description("Identifies institution where data came from")
      .addValidator(StandardValidators.URI_VALIDATOR)
      .build();

  public static final PropertyDescriptor FACILITY_ID = new PropertyDescriptor.Builder()
      .name("Facility ID")
      .description("Identifies facility where data came from")
      .addValidator(StandardValidators.URI_VALIDATOR)
      .build();

  public static final PropertyDescriptor DEPARTMENT_ID = new PropertyDescriptor.Builder()
      .name("Department ID")
      .description("Identifies department where data came from")
      .addValidator(StandardValidators.URI_VALIDATOR)
      .build();

  public static final PropertyDescriptor SOURCE_ID = new PropertyDescriptor.Builder()
      .name("Source ID")
      .description("Identifies source where data came from")
      .addValidator(StandardValidators.URI_VALIDATOR)
      .build();

  public static final PropertyDescriptor PAYLOAD_TYPE = new PropertyDescriptor.Builder()
      .name("Payload Type")
      .description("Payload type")
      .addValidator(StandardValidators.URI_VALIDATOR)
      .required(true)
      .build();

  private Set<Relationship> relationships;
  private List<PropertyDescriptor> supportedPropertyDescriptors;

  private volatile URI institutionId;
  private volatile URI facilityId;
  private volatile URI departmentId;
  private volatile URI sourceId;
  private volatile URI payloadType;

  @Inject
  private volatile IngestMessageRepository ingestMessageRepository;

  @Override
  protected void init(ProcessorInitializationContext context) {
    relationships = ImmutableSet.of(SUCCESS);
    supportedPropertyDescriptors = Arrays.asList(
        INSTITUTION_ID, FACILITY_ID, DEPARTMENT_ID, SOURCE_ID, PAYLOAD_TYPE);
  }

  @Override
  public Set<Relationship> getRelationships() {
    return relationships;
  }

  @Override
  protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return supportedPropertyDescriptors;
  }

  private static URI toURI(String value) {
    return (value == null) ? null : URI.create(value);
  }

  @Override
  protected void onInjected(ProcessContext processContext) {
    institutionId = toURI(processContext.getProperty(INSTITUTION_ID).getValue());
    facilityId = toURI(processContext.getProperty(FACILITY_ID).getValue());
    departmentId = toURI(processContext.getProperty(DEPARTMENT_ID).getValue());
    sourceId = toURI(processContext.getProperty(SOURCE_ID).getValue());
    payloadType = toURI(processContext.getProperty(PAYLOAD_TYPE).getValue());
  }

  private void saveIngestMessage(ByteBuffer payload) {
    IngestMessage ingestMessage = IngestMessage.builder()
        .timestamp(Instant.now())
        .institution(institutionId)
        .facility(facilityId)
        .department(departmentId)
        .source(sourceId)
        .payloadType(payloadType)
        .payload(payload)
        .build();
    ingestMessageRepository.save(ingestMessage);
  }

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    FlowFile flowFile = session.get();
    if (flowFile == null) {
      return;
    }

    session.read(flowFile, input -> {
        byte[] payload = ByteStreams.toByteArray(input);
        saveIngestMessage(ByteBuffer.wrap(payload));
      });

    session.transfer(flowFile, SUCCESS);
  }
}
