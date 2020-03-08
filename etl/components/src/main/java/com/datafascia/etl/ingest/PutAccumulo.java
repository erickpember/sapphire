// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.etl.ingest;

import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.nifi.DependencyInjectingProcessor;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.apache.accumulo.core.data.Value;
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
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.stream.io.StreamUtils;
import org.kohsuke.MetaInfServices;

/**
 * Puts Accumulo entry.
 */
@CapabilityDescription("Puts Accumulo entry.")
@EventDriven
@MetaInfServices(Processor.class)
@SupportsBatching
@Tags({"datafascia"})
public class PutAccumulo extends DependencyInjectingProcessor {

  public static final Relationship FAILURE = new Relationship.Builder()
      .name("failure")
      .description("A FlowFile is routed to this relationship if it cannot be sent to Accumulo")
      .build();
  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("success")
      .description("A FlowFile is routed to this relationship after it was sent to Accumulo")
      .build();

  public static final PropertyDescriptor TABLE = new PropertyDescriptor.Builder()
      .name("Table")
      .description("Table to put entry in")
      .required(true)
      .expressionLanguageSupported(true)
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .build();
  public static final PropertyDescriptor ROW_ID = new PropertyDescriptor.Builder()
      .name("Row Identifier")
      .description("Row identifier of entry to put")
      .required(true)
      .expressionLanguageSupported(true)
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .build();
  public static final PropertyDescriptor COLUMN_FAMILY = new PropertyDescriptor.Builder()
      .name("Column Family")
      .description("Column family of entry to put")
      .required(true)
      .expressionLanguageSupported(true)
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .build();
  public static final PropertyDescriptor COLUMN_QUALIFIER = new PropertyDescriptor.Builder()
      .name("Column Qualifier")
      .description("Column qualifier of entry to put")
      .required(true)
      .expressionLanguageSupported(true)
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .build();

  private static final Set<Relationship> RELATIONSHIPS = ImmutableSet.of(FAILURE, SUCCESS);
  private static final List<PropertyDescriptor> SUPPORTED_PROPERTY_DESCRIPTORS = Arrays.asList(
      TABLE, ROW_ID, COLUMN_FAMILY, COLUMN_QUALIFIER);

  @Inject
  private volatile AccumuloTemplate accumuloTemplate;

  @Override
  public Set<Relationship> getRelationships() {
    return RELATIONSHIPS;
  }

  @Override
  protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return SUPPORTED_PROPERTY_DESCRIPTORS;
  }

  private void doPut(
      String table, String rowId, String columnFamily, String columnQualifier, byte[] value) {

    accumuloTemplate.save(
        table,
        rowId,
        mutationBuilder ->
            mutationBuilder.columnFamily(columnFamily)
                .put(columnQualifier, new Value(value)));
  }

  private void put(
      String table, String rowId, String columnFamily, String columnQualifier, byte[] value) {

    try {
      doPut(table, rowId, columnFamily, columnQualifier, value);
    } catch (IllegalStateException e) {
      accumuloTemplate.createTableIfNotExist(table);
      doPut(table, rowId, columnFamily, columnQualifier, value);
    }
  }

  private static byte[] readBytes(ProcessSession session, FlowFile flowFile) {
    byte[] bytes = new byte[(int) flowFile.getSize()];
    session.read(flowFile, input -> StreamUtils.fillBuffer(input, bytes));
    return bytes;
  }

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    FlowFile flowFile = session.get();
    if (flowFile == null) {
      return;
    }

    try {
      String table = context.getProperty(TABLE)
          .evaluateAttributeExpressions(flowFile)
          .getValue();
      String rowId = context.getProperty(ROW_ID)
          .evaluateAttributeExpressions(flowFile)
          .getValue();
      String columnFamily = context.getProperty(COLUMN_FAMILY)
          .evaluateAttributeExpressions(flowFile)
          .getValue();
      String columnQualifier = context.getProperty(COLUMN_QUALIFIER)
          .evaluateAttributeExpressions(flowFile)
          .getValue();

      byte[] value = readBytes(session, flowFile);
      put(table, rowId, columnFamily, columnQualifier, value);

      session.getProvenanceReporter()
          .send(
              flowFile,
              "accumulo://" + table + "/" + rowId + "/" + columnFamily + ":" + columnQualifier);
      session.transfer(flowFile, SUCCESS);
    } catch (RuntimeException e) {
      log.error("Cannot process {}", new Object[] { flowFile }, e);
      flowFile = session.putAttribute(flowFile, "stackTrace", Throwables.getStackTraceAsString(e));
      flowFile = session.penalize(flowFile);
      session.transfer(flowFile, FAILURE);
    }
  }
}
