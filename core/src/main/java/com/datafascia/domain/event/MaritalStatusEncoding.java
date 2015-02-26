// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import com.datafascia.models.MaritalStatus;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;

/**
 * Converts between {@link MaritalStatus} and String.
 */
public class MaritalStatusEncoding extends CustomEncoding<MaritalStatus> {

  /**
   * Constructor
   */
  public MaritalStatusEncoding() {
    schema = Schema.create(Schema.Type.STRING);
    schema.addProp("CustomEncoding", MaritalStatusEncoding.class.getSimpleName());
  }

  @Override
  protected void write(Object datum, Encoder out) throws IOException {
    out.writeString(((MaritalStatus) datum).getCode());
  }

  @Override
  protected MaritalStatus read(Object reuse, Decoder in) throws IOException {
    return MaritalStatus.of(in.readString()).orElse(MaritalStatus.UNKNOWN);
  }
}
