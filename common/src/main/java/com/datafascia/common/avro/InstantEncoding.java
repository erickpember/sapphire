// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.avro;

import java.io.IOException;
import java.time.Instant;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;

/**
 * Writes an Instant to a long and reads an Instant from a long.
 * The long stores the number of milliseconds since 1970-01-01T00:00:00Z.
 */
public class InstantEncoding extends CustomEncoding<Instant> {

  /**
   * Constructor
   */
  public InstantEncoding() {
    schema = Schema.create(Schema.Type.LONG);
    schema.addProp("CustomEncoding", InstantEncoding.class.getSimpleName());
  }

  @Override
  protected void write(Object datum, Encoder out) throws IOException {
    out.writeLong(((Instant) datum).toEpochMilli());
  }

  @Override
  protected Instant read(Object reuse, Decoder in) throws IOException {
    return Instant.ofEpochMilli(in.readLong());
  }
}
