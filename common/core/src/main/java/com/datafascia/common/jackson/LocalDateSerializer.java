// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

/**
 * Serializer for date without time of day in Java
 */
@Slf4j
public class LocalDateSerializer extends JsonSerializer<LocalDate> {
  @Override
  public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException, JsonProcessingException {
    String dateString = value.format(DateTimeFormatter.BASIC_ISO_DATE);
    jgen.writeString(dateString);
  }
}
