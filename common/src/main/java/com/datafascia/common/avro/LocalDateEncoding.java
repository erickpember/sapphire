// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.avro;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;

/**
 * Converts between LocalDate and String.
 */
public class LocalDateEncoding extends CustomEncoding<LocalDate> {

  /**
   * Constructor
   */
  public LocalDateEncoding() {
    schema = Schema.create(Schema.Type.STRING);
    schema.addProp("CustomEncoding", LocalDateEncoding.class.getSimpleName());
  }

  @Override
  protected void write(Object datum, Encoder out) throws IOException {
    out.writeString(DateTimeFormatter.BASIC_ISO_DATE.format((LocalDate) datum));
  }

  @Override
  protected LocalDate read(Object reuse, Decoder in) throws IOException {
    return DateTimeFormatter.BASIC_ISO_DATE.parse(in.readString(), LocalDate::from);
  }
}
