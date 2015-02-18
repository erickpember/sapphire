// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.accumulo.QueryTemplate;
import com.datafascia.accumulo.RowMapper;
import com.datafascia.common.persist.Id;
import com.datafascia.message.RawMessage;
import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.hadoop.io.Text;

/**
 * Message archive data access.
 */
@Singleton @Slf4j
public class MessageDao {

  private static final String COLUMN_FAMILY = RawMessage.class.getSimpleName();
  private static final ColumnVisibility COLUMN_VISIBILITY = new ColumnVisibility("System");
  private static final String TIMESTAMP = "timestamp";
  private static final String INSTITUTION = "institution";
  private static final String FACILITY = "facility";
  private static final String DEPARTMENT = "department";
  private static final String SOURCE = "source";
  private static final String PAYLOAD_TYPE = "payloadType";
  private static final String PAYLOAD = "payload";
  private static final MessageRowMapper MESSAGE_ROW_MAPPER = new MessageRowMapper();

  private BatchWriter writer;
  private QueryTemplate queryTemplate;

  /**
   * Constructor
   *
   * @param connector
   *     connector
   * @param queryTemplate
   *     query template
   */
  @Inject
  public MessageDao(Connector connector, QueryTemplate queryTemplate) {
    try {
      writer = connector.createBatchWriter(Tables.MESSAGE, new BatchWriterConfig());
    } catch (TableNotFoundException e) {
      throw new IllegalStateException("createBatchWriter", e);
    }

    this.queryTemplate = queryTemplate;
  }

  private static class MessageRowMapper implements RowMapper<RawMessage> {
    private RawMessage message;

    @Override
    public void onBeginRow(Key key) {
      message = new RawMessage();
    }

    @Override
    public void onReadEntry(Map.Entry<Key, Value> entry) {
      String value = entry.getValue().toString();
      switch (entry.getKey().getColumnQualifier().toString()) {
        case TIMESTAMP:
          message.setTimestamp(Instant.parse(value));
          break;
        case INSTITUTION:
          message.setInstitution(URI.create(value));
          break;
        case FACILITY:
          message.setFacility(URI.create(value));
          break;
        case DEPARTMENT:
          message.setDepartment(URI.create(value));
          break;
        case SOURCE:
          message.setSource(URI.create(value));
          break;
        case PAYLOAD_TYPE:
          message.setPayloadType(URI.create(value));
          break;
        case PAYLOAD:
          message.setPayload(value);
          break;
      }
    }

    @Override
    public RawMessage onEndRow() {
      return message;
    }
  }

  /**
   * Reads message.
   *
   * @param messageId
   *     primary key
   * @return optional entity, not present if not found
   */
  public Optional<RawMessage> read(Id<RawMessage> messageId) {
    Scanner scanner = queryTemplate.createScanner(Tables.MESSAGE);
    scanner.setRange(Range.exact(messageId.toString()));
    scanner.fetchColumnFamily(new Text(COLUMN_FAMILY));

    return Optional.ofNullable(queryTemplate.queryForObject(scanner, MESSAGE_ROW_MAPPER));
  }

  /**
   * Saves message to archive.
   *
   * @param message
   *     to save
   */
  public void save(RawMessage message) {
    try {
      writer.addMutation(toMutation(message));
    } catch (MutationsRejectedException e) {
      throw new IllegalStateException("save", e);
    }
  }

  private Mutation toMutation(RawMessage message) {
    Mutation mutation = new Mutation(message.getId().toString());
    putValue(mutation, TIMESTAMP, message.getTimestamp());
    putValue(mutation, INSTITUTION, message.getInstitution());
    putValue(mutation, FACILITY, message.getFacility());
    putValue(mutation, DEPARTMENT, message.getDepartment());
    putValue(mutation, SOURCE, message.getSource());
    putValue(mutation, PAYLOAD_TYPE, message.getPayloadType());
    putValue(mutation, PAYLOAD, message.getPayload());
    return mutation;
  }

  private void putValue(Mutation mutation, String columnQualifier, Object value) {
    if (value != null) {
      mutation.put(COLUMN_FAMILY, columnQualifier, COLUMN_VISIBILITY, value.toString());
    }
  }
}
