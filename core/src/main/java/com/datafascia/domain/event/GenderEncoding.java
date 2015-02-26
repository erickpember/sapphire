// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import com.datafascia.models.Gender;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;

/**
 * Converts between {@link Gender} and String.
 */
public class GenderEncoding extends CustomEncoding<Gender> {

  /**
   * Constructor
   */
  public GenderEncoding() {
    schema = Schema.create(Schema.Type.STRING);
    schema.addProp("CustomEncoding", GenderEncoding.class.getSimpleName());
  }

  @Override
  protected void write(Object datum, Encoder out) throws IOException {
    out.writeString(((Gender) datum).getCode());
  }

  @Override
  protected Gender read(Object reuse, Decoder in) throws IOException {
    return Gender.of(in.readString()).orElse(Gender.UNKNOWN);
  }
}
