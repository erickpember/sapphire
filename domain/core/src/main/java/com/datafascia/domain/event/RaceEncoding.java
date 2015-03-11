// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import com.datafascia.models.Race;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;

/**
 * Converts between {@link Race} and String.
 */
public class RaceEncoding extends CustomEncoding<Race> {

  /**
   * Constructor
   */
  public RaceEncoding() {
    schema = Schema.create(Schema.Type.STRING);
    schema.addProp("CustomEncoding", RaceEncoding.class.getSimpleName());
  }

  @Override
  protected void write(Object datum, Encoder out) throws IOException {
    out.writeString(((Race) datum).getCode());
  }

  @Override
  protected Race read(Object reuse, Decoder in) throws IOException {
    return Race.of(in.readString()).orElse(Race.UNKNOWN);
  }
}
