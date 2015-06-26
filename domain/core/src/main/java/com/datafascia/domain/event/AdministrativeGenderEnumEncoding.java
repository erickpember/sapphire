// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;

/**
 * Converts between {@link AdministrativeGenderEnum} and String.
 */
public class AdministrativeGenderEnumEncoding extends CustomEncoding<AdministrativeGenderEnum> {

  /**
   * Constructor
   */
  public AdministrativeGenderEnumEncoding() {
    schema = Schema.create(Schema.Type.STRING);
    schema.addProp("CustomEncoding", AdministrativeGenderEnumEncoding.class.getSimpleName());
  }

  @Override
  protected void write(Object datum, Encoder out) throws IOException {
    out.writeString(((AdministrativeGenderEnum) datum).getCode());
  }

  @Override
  protected AdministrativeGenderEnum read(Object reuse, Decoder in) throws IOException {
    return AdministrativeGenderEnum.UNKNOWN.forCode(in.readString());
  }
}
