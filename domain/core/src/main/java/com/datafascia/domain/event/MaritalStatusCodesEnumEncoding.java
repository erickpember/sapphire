// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;

/**
 * Converts between {@link MaritalStatusCodesEnum} and String.
 */
public class MaritalStatusCodesEnumEncoding extends CustomEncoding<MaritalStatusCodesEnum> {

  /**
   * Constructor
   */
  public MaritalStatusCodesEnumEncoding() {
    schema = Schema.create(Schema.Type.STRING);
    schema.addProp("CustomEncoding", MaritalStatusCodesEnumEncoding.class.getSimpleName());
  }

  @Override
  protected void write(Object datum, Encoder out) throws IOException {
    out.writeString(((MaritalStatusCodesEnum) datum).getCode());
  }

  @Override
  protected MaritalStatusCodesEnum read(Object reuse, Decoder in) throws IOException {
    return MaritalStatusCodesEnum.UNK.forCode(in.readString());
  }
}
