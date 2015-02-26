// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.accumulo.AccumuloTemplate;
import com.datafascia.common.persist.Id;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.data.Value;

/**
 * Implements common data access methods.
 */
@Slf4j
public abstract class BaseRepository {

  private static final char COMPONENT_SEPARATOR = '=';
  private static final char KEY_SEPARATOR = '&';
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  protected AccumuloTemplate accumuloTemplate;

  /**
   * Create data access with query template
   *
   * @param accumuloTemplate the query template to use
   */
  public BaseRepository(AccumuloTemplate accumuloTemplate) {
    this.accumuloTemplate = accumuloTemplate;
  }

  protected static String toRowId(Class<?> entityClass, Id<?> entityId) {
    return entityClass.getSimpleName() + COMPONENT_SEPARATOR + entityId.toString() + KEY_SEPARATOR;
  }

  /**
   * Converts LocalDate to database representation.
   *
   * @param value
   *     to convert
   * @return converted value
   */
  protected static String encode(LocalDate value) {
    return DateTimeFormatter.BASIC_ISO_DATE.format(value);
  }

  /**
   * Converts value from database representation to LocalDate.
   *
   * @param value
   *     to convert
   * @return converted value
   */
  protected static LocalDate decodeLocalDate(String value) {
    return LocalDate.parse(value, DateTimeFormatter.BASIC_ISO_DATE);
  }

  private static String decodeJson(Value value) {
    return new String(value.get(), StandardCharsets.UTF_8);
  }

  /**
   * Converts value from database representation to String array.
   *
   * @param json
   *     to convert
   * @return converted value
   */
  protected static String[] decodeStringArray(String json) {
    try {
      return OBJECT_MAPPER.readValue(json, String[].class);
    } catch (IOException e) {
      throw new IllegalStateException(String.format("Cannot convert JSON [%s]", json), e);
    }
  }
}
