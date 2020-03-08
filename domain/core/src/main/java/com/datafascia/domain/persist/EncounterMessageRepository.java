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
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.persist.Id;
import com.datafascia.common.time.InstantFormatter;
import com.datafascia.domain.model.EncounterMessage;
import com.datafascia.domain.model.IngestMessage;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
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
  private static final String LAST_PROCESSED_MESSAGE_ID = "lastProcessedMessageId";
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

  private String readLastProcessedMessageId(Id<Encounter> encounterId) {
    Scanner scanner = accumuloTemplate.createScanner(Tables.ENCOUNTER);
    scanner.setRange(Range.exact(encounterId.toString()));
    scanner.fetchColumn(new Text(COLUMN_FAMILY), new Text(LAST_PROCESSED_MESSAGE_ID));
    Iterator<Map.Entry<Key, Value>> iterator = scanner.iterator();
    try {
      return iterator.hasNext() ? decodeString(iterator.next().getValue().get()) : null;
    } finally {
      scanner.close();
    }
  }

  private String findLastMessageId(Id<Encounter> encounterId) {
    Scanner scanner = accumuloTemplate.createScanner(Tables.ENCOUNTER);
    scanner.setRange(Range.prefix(toRowIdPrefix(encounterId)));
    scanner.fetchColumn(new Text(COLUMN_FAMILY), new Text(PAYLOAD));
    try {
      String lastMessageId = null;
      for (Map.Entry<Key, Value> entry : scanner) {
        lastMessageId = entry.getKey().getRow().toString();
      }

      return lastMessageId;
    } finally {
      scanner.close();
    }
  }

  /**
   * Sets last processsed message ID to last message if not already set.
   *
   * @param encounterId
   *     encounter ID
   */
  public void initializeLastProcessedMessageId(Id<Encounter> encounterId) {
    String lastProcesssedMessageId = readLastProcessedMessageId(encounterId);
    if (!Strings.isNullOrEmpty(lastProcesssedMessageId)) {
      return;
    }

    String lastMessageId = findLastMessageId(encounterId);
    if (!Strings.isNullOrEmpty(lastMessageId)) {
      saveLastProcessedMessageId(encounterId, lastMessageId);
    }
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
    scanner.fetchColumn(new Text(COLUMN_FAMILY), new Text(PAYLOAD));

    String rowIdPrefix = toRowIdPrefix(encounterId);
    String lastProcessedMessageId = readLastProcessedMessageId(encounterId);
    if (Strings.isNullOrEmpty(lastProcessedMessageId)) {
      scanner.setRange(Range.prefix(rowIdPrefix));
    } else {
      scanner.setRange(new Range(lastProcessedMessageId, false, null, true));
    }

    try {
      List<EncounterMessage> messages = new ArrayList<>();
      for (Map.Entry<Key, Value> entry : scanner) {
        String id = entry.getKey().getRow().toString();
        if (!id.startsWith(rowIdPrefix)) {
          break;
        }

        EncounterMessage message = new EncounterMessage();
        message.setId(id);
        message.setPayload(ByteBuffer.wrap(entry.getValue().get()));

        messages.add(message);
      }

      return messages;
    } finally {
      scanner.close();
    }
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

  /**
   * Saves last processed message ID.
   *
   * @param encounterId
   *     encounter ID
   * @param lastProcessedMessageId
   *     row ID
   */
  public void saveLastProcessedMessageId(Id<Encounter> encounterId, String lastProcessedMessageId) {
    accumuloTemplate.save(
        Tables.ENCOUNTER,
        encounterId.toString(),
        mutationBuilder ->
            mutationBuilder
                .columnFamily(COLUMN_FAMILY)
                .put(LAST_PROCESSED_MESSAGE_ID, lastProcessedMessageId));
  }
}
