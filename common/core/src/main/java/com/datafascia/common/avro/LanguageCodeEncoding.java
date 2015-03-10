// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.avro;

import com.neovisionaries.i18n.LanguageCode;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;

/**
 * Converts between {@link LanguageCode} and String.
 */
public class LanguageCodeEncoding extends CustomEncoding<LanguageCode> {

  /**
   * Constructor
   */
  public LanguageCodeEncoding() {
    schema = Schema.create(Schema.Type.STRING);
    schema.addProp("CustomEncoding", LanguageCodeEncoding.class.getSimpleName());
  }

  @Override
  protected void write(Object datum, Encoder out) throws IOException {
    out.writeString(((LanguageCode) datum).name());
  }

  @Override
  protected LanguageCode read(Object reuse, Decoder in) throws IOException {
    return LanguageCode.getByCode(in.readString());
  }
}
