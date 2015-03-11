// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;

/**
 * Converts between {@link EventType} and String.
 */
public class EventTypeEncoding extends CustomEncoding<EventType> {

  /**
   * Constructor
   */
  public EventTypeEncoding() {
    schema = Schema.create(Schema.Type.STRING);
    schema.addProp("CustomEncoding", EventTypeEncoding.class.getSimpleName());
  }

  @Override
  protected void write(Object datum, Encoder out) throws IOException {
    out.writeString(((EventType) datum).getCode());
  }

  @Override
  protected EventType read(Object reuse, Decoder in) throws IOException {
    return EventType.of(in.readString()).orElse(EventType.UNKNOWN);
  }
}
