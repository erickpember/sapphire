// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.Limit;
import com.datafascia.common.accumulo.RowMapper;
import com.datafascia.common.time.InstantFormatter;
import com.datafascia.domain.model.IngestMessage;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;

/**
 * Ingest message archive data access.
 * <p>
 * The row ID for a message is composed of:
 * <ol>
 * <li>the timestamp with millisecond resolution in ISO 8601 format with timezone code {@code Z}
 * <li>the {@code |} character
 * <li>the hash of the message
 * </ol>
 */
@Singleton @Slf4j
public class IngestMessageRepository extends BaseRepository {

  private static final String COLUMN_FAMILY = IngestMessage.class.getSimpleName();
  private static final String TIMESTAMP = "timestamp";
  private static final String INSTITUTION = "institution";
  private static final String FACILITY = "facility";
  private static final String DEPARTMENT = "department";
  private static final String SOURCE = "source";
  private static final String PAYLOAD_TYPE = "payloadType";
  private static final String PAYLOAD = "payload";
  private static final MessageRowMapper MESSAGE_ROW_MAPPER = new MessageRowMapper();

  /**
   * Constructor
   *
   * @param accumuloTemplate
   *     data access operations template
   */
  @Inject
  public IngestMessageRepository(AccumuloTemplate accumuloTemplate) {
    super(accumuloTemplate);
  }

  private static class MessageRowMapper implements RowMapper<IngestMessage> {
    private IngestMessage message;

    @Override
    public void onBeginRow(Key key) {
      message = new IngestMessage();
    }

    @Override
    public void onReadEntry(Map.Entry<Key, Value> entry) {
      byte[] value = entry.getValue().get();
      switch (entry.getKey().getColumnQualifier().toString()) {
        case TIMESTAMP:
          message.setTimestamp(decodeInstant(value));
          break;
        case INSTITUTION:
          message.setInstitution(URI.create(decodeString(value)));
          break;
        case FACILITY:
          message.setFacility(URI.create(decodeString(value)));
          break;
        case DEPARTMENT:
          message.setDepartment(URI.create(decodeString(value)));
          break;
        case SOURCE:
          message.setSource(URI.create(decodeString(value)));
          break;
        case PAYLOAD_TYPE:
          message.setPayloadType(URI.create(decodeString(value)));
          break;
        case PAYLOAD:
          message.setPayload(ByteBuffer.wrap(value));
          break;
      }
    }

    @Override
    public IngestMessage onEndRow() {
      return message;
    }
  }

  /**
   * Saves message to archive.
   *
   * @param message
   *     to save
   */
  public void save(IngestMessage message) {
    accumuloTemplate.save(
        Tables.INGEST_MESSAGE,
        message.getId().toString(),
        mutationBuilder ->
            mutationBuilder
                .columnFamily(COLUMN_FAMILY)
                .put(TIMESTAMP, message.getTimestamp())
                .put(INSTITUTION, message.getInstitution())
                .put(FACILITY, message.getFacility())
                .put(DEPARTMENT, message.getDepartment())
                .put(SOURCE, message.getSource())
                .put(PAYLOAD_TYPE, message.getPayloadType())
                .put(PAYLOAD, new Value(message.getPayload())));
  }

  /**
   * Lists messages.
   *
   * @param timeLower
   *     time lower bound (inclusive)
   * @param limit
   *     maximum number of messages to return
   * @return messages
   */
  public List<IngestMessage> list(Instant timeLower, int limit) {
    Scanner scanner = accumuloTemplate.createScanner(Tables.INGEST_MESSAGE);
    scanner.setRange(new Range(InstantFormatter.ISO_INSTANT_MILLI.format(timeLower), null));
    scanner.fetchColumnFamily(new Text(COLUMN_FAMILY));

    return accumuloTemplate.queryForList(
        scanner, MESSAGE_ROW_MAPPER, entity -> true, new Limit<>(limit));
  }
}
