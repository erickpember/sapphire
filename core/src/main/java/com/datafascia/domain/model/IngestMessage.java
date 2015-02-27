// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.avro.InstantEncoding;
import com.datafascia.common.persist.Id;
import com.datafascia.common.time.InstantFormatter;
import com.google.common.hash.Hashing;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.avro.reflect.AvroEncode;
import org.apache.avro.reflect.Nullable;

/**
 * The envelope used to transfer incoming data for start of the ETL processing. This envelope is
 * used to wrap the messages and inserted into the ingestion queue.
 */
@AllArgsConstructor @Builder @Data @NoArgsConstructor
public class IngestMessage {

  /** when the system received the message */
  @AvroEncode(using = InstantEncoding.class)
  private Instant timestamp;

  /** Institution identifier */
  @Nullable
  private URI institution;

  /** Facility identifier */
  @Nullable
  private URI facility;

  /** Department identifier */
  @Nullable
  private URI department;

  /** Source identifier */
  @Nullable
  private URI source;

  /** Payload type */
  private URI payloadType;

  /** Payload */
  private ByteBuffer payload;

  /**
   * Gets database primary key
   *
   * @return primary key
   */
  public Id<IngestMessage> getId() {
    String id =
        InstantFormatter.ISO_INSTANT_MILLI.format(timestamp) + '|' +
        Hashing.sha1().hashString(toString(), StandardCharsets.UTF_8).toString();
    return Id.of(id);
  }
}
