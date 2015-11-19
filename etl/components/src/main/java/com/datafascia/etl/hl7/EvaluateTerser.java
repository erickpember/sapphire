// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.Terser;
import com.datafascia.common.nifi.DependencyInjectingProcessor;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.apache.nifi.annotation.behavior.DynamicProperty;
import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.behavior.SupportsBatching;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.Validator;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Processor;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.stream.io.StreamUtils;
import org.kohsuke.MetaInfServices;

/**
 * Evaluates HAPI terser on HL7 message.
 */
@CapabilityDescription(
    "Evaluates HAPI terser on HL7 message. Assigns the results to FlowFile attributes. " +
    "Configure terser expressions by adding user-defined properties. " +
    "The property name maps to the attribute name into which the result will be placed. " +
    "The property value must be a valid terser expression.")
@DynamicProperty(
    name = "FlowFile attribute",
    value = "terser expression",
    description = "The FlowFile attribute will be set to the result of the terser expression.")
@EventDriven
@MetaInfServices(Processor.class)
@SideEffectFree
@SupportsBatching
@Tags({"datafascia", "evaluate", "HL7", "health level 7", "healthcare" })
public class EvaluateTerser extends DependencyInjectingProcessor {

  public static final Relationship FAILURE = new Relationship.Builder()
      .name("failure")
      .description(
          "HL7 message routed to this relationship when the terser " +
          "cannot be evaluated against the HL7 message; " +
          "for example, if the HL7 message is malformed")
      .build();
  public static final Relationship MATCHED = new Relationship.Builder()
      .name("matched")
      .description(
          "HL7 message routed to this relationship when the terser " +
          "was successfully evaluated and the FlowFile was modified as a result")
      .build();
  public static final Relationship UNMATCHED = new Relationship.Builder()
      .name("unmatched")
      .description(
          "HL7 message routed to this relationship when the terser " +
          "did not match the HL7 message")
      .build();

  private static final Set<Relationship> RELATIONSHIPS =
      ImmutableSet.of(FAILURE, MATCHED, UNMATCHED);

  @Inject
  private volatile Parser parser;

  @Override
  public Set<Relationship> getRelationships() {
    return RELATIONSHIPS;
  }

  @Override
  protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return Collections.emptyList();
  }

  @Override
  protected PropertyDescriptor getSupportedDynamicPropertyDescriptor(
      String propertyDescriptorName) {

    return new PropertyDescriptor.Builder()
        .name(propertyDescriptorName)
        .expressionLanguageSupported(false)
        .addValidator(Validator.VALID)
        .required(false)
        .dynamic(true)
        .build();
  }

  private static String readString(ProcessSession session, FlowFile flowFile) {
    byte[] bytes = new byte[(int) flowFile.getSize()];
    session.read(flowFile, input -> StreamUtils.fillBuffer(input, bytes));
    return new String(bytes, StandardCharsets.UTF_8);
  }

  private static Map<String, String> getAttributeNameToExpressionMap(ProcessContext context) {
    Map<String, String> attributeNameToExpressionMap = new HashMap<>();
    context.getProperties().entrySet().stream()
        .filter(entry -> entry.getKey().isDynamic())
        .forEach(entry ->
            attributeNameToExpressionMap.put(entry.getKey().getName(), entry.getValue()));
    return attributeNameToExpressionMap;
  }

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    FlowFile flowFile = session.get();
    if (flowFile == null) {
      return;
    }

    try {
      String hl7 = readString(session, flowFile);
      Message message = parser.parse(hl7);
      Terser terser = new Terser(message);

      Map<String, String> attributeNameToValueMap = new HashMap<>();
      for (Map.Entry<String, String> entry : getAttributeNameToExpressionMap(context).entrySet()) {
        String value = terser.get(entry.getValue());
        if (value != null) {
          attributeNameToValueMap.put(entry.getKey(), value);
        }
      }

      Relationship destination;
      if (attributeNameToValueMap.isEmpty()) {
        destination = UNMATCHED;
      } else {
        flowFile = session.putAllAttributes(flowFile, attributeNameToValueMap);
        session.getProvenanceReporter().modifyAttributes(flowFile);
        destination = MATCHED;
      }
      session.transfer(flowFile, destination);
    } catch (HL7Exception | RuntimeException e) {
      log.error("Cannot process {}", new Object[] { flowFile }, e);
      flowFile = session.putAttribute(flowFile, "stackTrace", Throwables.getStackTraceAsString(e));
      flowFile = session.penalize(flowFile);
      session.transfer(flowFile, FAILURE);
    }
  }
}
