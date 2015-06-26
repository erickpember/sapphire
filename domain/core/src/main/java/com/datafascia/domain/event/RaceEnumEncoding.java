// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import com.datafascia.domain.fhir.RaceEnum;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;

/**
 * Converts between {@link RaceEnum} and String.
 */
public class RaceEnumEncoding extends CustomEncoding<RaceEnum> {

  /**
   * Constructor
   */
  public RaceEnumEncoding() {
    schema = Schema.create(Schema.Type.STRING);
    schema.addProp("CustomEncoding", RaceEnumEncoding.class.getSimpleName());
  }

  @Override
  protected void write(Object datum, Encoder out) throws IOException {
    out.writeString(((RaceEnum) datum).getCode());
  }

  @Override
  protected RaceEnum read(Object reuse, Decoder in) throws IOException {
    return RaceEnum.of(in.readString()).orElse(RaceEnum.UNKNOWN);
  }
}
