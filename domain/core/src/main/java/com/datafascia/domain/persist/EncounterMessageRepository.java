// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.RowMapper;
import com.datafascia.common.persist.Id;
import com.datafascia.common.time.InstantFormatter;
import com.datafascia.domain.model.EncounterMessage;
import com.datafascia.domain.model.IngestMessage;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;

/**
 * Persists HL7 messages by encounter.
 * <p>
 * The row ID for a message is composed of:
 * <ol>
 * <li>the encounter identifier
 * <li>the {@code |} character
 * <li>the timestamp with millisecond resolution in ISO 8601 format with timezone code {@code Z}
 * <li>the {@code |} character
 * <li>the hash of the message
 * </ol>
 */
@Singleton
public class EncounterMessageRepository extends BaseRepository {

  private static final String COLUMN_FAMILY = IngestMessage.class.getSimpleName();
  private static final String PAYLOAD = "payload";
  private static final MessageRowMapper MESSAGE_ROW_MAPPER = new MessageRowMapper();
  private static final BaseEncoding ENCODING = BaseEncoding.base64Url().omitPadding();

  /**
   * Constructor
   *
   * @param accumuloTemplate
   *     data access operations template
   */
  @Inject
  public EncounterMessageRepository(AccumuloTemplate accumuloTemplate) {
    super(accumuloTemplate);

    accumuloTemplate.createTableIfNotExist(Tables.ENCOUNTER);
  }

  private static class MessageRowMapper implements RowMapper<EncounterMessage> {
    private EncounterMessage message;

    @Override
    public void onBeginRow(Key key) {
      message = new EncounterMessage();
      message.setId(key.getRow().toString());
    }

    @Override
    public void onReadEntry(Map.Entry<Key, Value> entry) {
      byte[] value = entry.getValue().get();
      switch (entry.getKey().getColumnQualifier().toString()) {
        case PAYLOAD:
          message.setPayload(ByteBuffer.wrap(value));
          break;
      }
    }

    @Override
    public EncounterMessage onEndRow() {
      return message;
    }
  }

  private static String toRowIdPrefix(Id<Encounter> encounterId) {
    return encounterId.toString() + '|';
  }

  /**
   * Saves message for an encounter.
   *
   * @param encounterId
   *     encounter ID
   * @param payload
   *     payload
   */
  public void save(Id<Encounter> encounterId, String payload) {
    byte[] value = payload.getBytes(StandardCharsets.UTF_8);

    String rowId =
        toRowIdPrefix(encounterId) +
        InstantFormatter.ISO_INSTANT_MILLI.format(Instant.now()) + '|' +
        ENCODING.encode(Hashing.sha1().hashBytes(value).asBytes());

    accumuloTemplate.save(
        Tables.ENCOUNTER,
        rowId,
        mutationBuilder ->
            mutationBuilder
                .columnFamily(COLUMN_FAMILY)
                .put(PAYLOAD, new Value(value)));
  }

  /**
   * Finds messages for an encounter.
   *
   * @param encounterId
   *     encounter ID
   * @return messages
   */
  public List<EncounterMessage> findByEncounterId(Id<Encounter> encounterId) {
    Scanner scanner = accumuloTemplate.createScanner(Tables.ENCOUNTER);
    scanner.setRange(Range.prefix(toRowIdPrefix(encounterId)));
    scanner.fetchColumnFamily(new Text(COLUMN_FAMILY));

    return accumuloTemplate.queryForList(scanner, MESSAGE_ROW_MAPPER);
  }

  /**
   * Deletes messages for an encounter.
   *
   * @param encounterId
   *     encounter ID
   */
  public void delete(Id<Encounter> encounterId) {
    accumuloTemplate.deleteRowIdPrefix(Tables.ENCOUNTER, toRowIdPrefix(encounterId));
  }
}
