// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ingest;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import ca.uhn.hl7v2.protocol.ReceivingApplicationExceptionHandler;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import com.datafascia.common.time.InstantFormatter;
import com.google.common.collect.ImmutableSet;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.annotation.lifecycle.OnUnscheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.CoreAttributes;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Processor;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.kohsuke.MetaInfServices;

/**
 * NiFi processor that starts an HL7 MLLP server to receive HL7 messages.
 */
@CapabilityDescription("Starts an HL7 MLLP server to receive HL7 messages.")
@MetaInfServices(Processor.class)
@Tags({"HL7", "health level 7", "healthcare", "ingest", "listen", "MLLP"})
public class ListenHL7 extends AbstractProcessor {

  private static final BaseEncoding ENCODING = BaseEncoding.base64Url().omitPadding();

  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("success")
      .description("Relationship for successfully received FlowFiles")
      .build();

  public static final PropertyDescriptor PORT = new PropertyDescriptor.Builder()
      .name("Listening Port")
      .description("Port to listen on for incoming connections")
      .required(true)
      .addValidator(StandardValidators.POSITIVE_INTEGER_VALIDATOR)
      .build();

  public static final PropertyDescriptor TLS = new PropertyDescriptor.Builder()
      .name("TLS")
      .description("Whether or not to use TLS")
      .required(true)
      .addValidator(StandardValidators.BOOLEAN_VALIDATOR)
      .build();

  private Set<Relationship> relationships = ImmutableSet.of(SUCCESS);
  private List<PropertyDescriptor> supportedPropertyDescriptors = Arrays.asList(PORT, TLS);

  private volatile HL7Service server;
  private final BlockingQueue<String> receivedMessages = new LinkedBlockingQueue<>();

  @Override
  public Set<Relationship> getRelationships() {
    return relationships;
  }

  @Override
  protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return supportedPropertyDescriptors;
  }

  private class MyReceivingApplication implements ReceivingApplication {
    @Override
    public Message processMessage(Message message, Map<String, Object> metadata)
        throws ReceivingApplicationException, HL7Exception {

      receivedMessages.add(message.encode());
      try {
        return message.generateACK();
      } catch (IOException e) {
        throw new HL7Exception("Error generating ACK", e);
      }
    }

    @Override
    public boolean canProcess(Message message) {
      return true;
    }
  }

  private class MyExceptionHandler implements ReceivingApplicationExceptionHandler {
    @Override
    public String processException(
        String incomingMessage,
        Map<String, Object> incomingMetadata,
        String outgoingMessage,
        Exception e) throws HL7Exception {

      String header = incomingMessage.split("\r")[0];
      getLogger().error(
          "Error processing incoming message. MSH segment [{}]", new Object[] { header }, e);
      return outgoingMessage;
    }
  }

  /**
   * When the processor is scheduled to run, starts the HL7 MLLP server.
   *
   * @param processContext
   *     process context
   */
  @OnScheduled
  public void startServer(ProcessContext processContext) {
    int port = processContext.getProperty(PORT).asInteger();
    boolean tls = processContext.getProperty(TLS).asBoolean();
    getLogger().info("Starting MLLP server, port {}, useTLS {}", new Object[] { port, tls });

    HapiContext hapiContext = new DefaultHapiContext(new NoValidation());

    server = hapiContext.newServer(port, tls);
    server.registerApplication(new MyReceivingApplication());
    server.setExceptionHandler(new MyExceptionHandler());
    server.start();
  }

  /**
   * When the processor is no longer scheduled to run, stops the HL7 MLLP server.
   */
  @OnUnscheduled
  public void stopServer() {
    server.stop();
  }

  private String generateFilename(byte[] content) {
    return
        InstantFormatter.ISO_INSTANT_MILLI.format(Instant.now()) + '|' +
        ENCODING.encode(Hashing.sha1().hashBytes(content).asBytes());
  }

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    String message = receivedMessages.poll();
    if (message == null) {
      context.yield();
      return;
    }

    byte[] content = message.getBytes(StandardCharsets.UTF_8);

    FlowFile flowFile = session.create();
    flowFile = session.write(flowFile, output -> output.write(content));
    flowFile = session.putAttribute(
        flowFile, CoreAttributes.FILENAME.key(), generateFilename(content));
    session.getProvenanceReporter().receive(flowFile, "Unknown");
    session.transfer(flowFile, SUCCESS);
  }
}
