// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.RowMapper;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.model.IngestMessage;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
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
          message.setPayload(ByteBuffer.wrap(entry.getValue().get()));
          break;
      }
    }

    @Override
    public IngestMessage onEndRow() {
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
  public Optional<IngestMessage> read(Id<IngestMessage> messageId) {
    Scanner scanner = accumuloTemplate.createScanner(Tables.INGEST_MESSAGE);
    scanner.setRange(Range.exact(messageId.toString()));
    scanner.fetchColumnFamily(new Text(COLUMN_FAMILY));

    return Optional.ofNullable(accumuloTemplate.queryForObject(scanner, MESSAGE_ROW_MAPPER));
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
}
