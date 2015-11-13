// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web;

import com.datafascia.common.nifi.DependencyInjectingProcessor;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
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
import org.kohsuke.MetaInfServices;

/**
 * Processor for handling nursing orders at UCSF.
 */
@CapabilityDescription("Transforms UCSF nursing orders to FHIR procedure requests.")
@EventDriven
@MetaInfServices(Processor.class)
@SupportsBatching
@Tags({"ingest", "datafascia", "ucsf", "json"})
public class NursingOrdersProcessor extends DependencyInjectingProcessor {

  public static final Relationship FAILURE = new Relationship.Builder()
      .name("FAILURE")
      .description("Failed nursing orders")
      .build();
  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("SUCCESS")
      .description("Successfully processed nursing orders")
      .build();

  private static final Set<Relationship> RELATIONSHIPS = ImmutableSet.of(FAILURE, SUCCESS);

  @Inject
  private NursingOrdersTransformer nursingOrdersTransformer;

  @Override
  public Set<Relationship> getRelationships() {
    return RELATIONSHIPS;
  }

  @Override
  public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return Collections.emptyList();
  }

  private static String readString(InputStream input) throws IOException {
    return CharStreams.toString(new InputStreamReader(input, StandardCharsets.UTF_8));
  }

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    FlowFile flowFile = session.get();
    if (flowFile == null) {
      return;
    }

    try {
      AtomicReference<String> orderReference = new AtomicReference<>();
      session.read(flowFile, input -> orderReference.set(readString(input)));
      String order = orderReference.get();

      nursingOrdersTransformer.accept(order);

      session.transfer(flowFile, SUCCESS);
    } catch (RuntimeException e) {
      log.error("Cannot process {}", new Object[] { flowFile }, e);
      flowFile = session.putAttribute(flowFile, "stackTrace", Throwables.getStackTraceAsString(e));
      flowFile = session.penalize(flowFile);
      session.transfer(flowFile, FAILURE);
    }
  }
}
