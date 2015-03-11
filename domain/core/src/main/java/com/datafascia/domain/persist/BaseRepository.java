// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.persist.Id;
import com.datafascia.jackson.DFObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.data.Key;

/**
 * Implements common data access methods.
 */
@Slf4j
public abstract class BaseRepository {

  private static final char COMPONENT_SEPARATOR = '=';
  private static final char KEY_SEPARATOR = '&';
  private static final ObjectMapper OBJECT_MAPPER = DFObjectMapper.objectMapper();
  private static final TypeReference<List<String>> STRING_LIST_TYPE_REFERENCE =
      new TypeReference<List<String>>() { };

  protected AccumuloTemplate accumuloTemplate;

  /**
   * Create data access with query template
   *
   * @param accumuloTemplate the query template to use
   */
  public BaseRepository(AccumuloTemplate accumuloTemplate) {
    this.accumuloTemplate = accumuloTemplate;
  }

  private static String escape(String input) {
    try {
      return URLEncoder.encode(input, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new AssertionError("Java must implement UTF-8");
    }
  }

  protected static String toRowId(Class<?> entityClass, Id<?> entityId) {
    return entityClass.getSimpleName() + COMPONENT_SEPARATOR +
        escape(entityId.toString()) + KEY_SEPARATOR;
  }

  private static String unescape(String input) {
    try {
      return URLDecoder.decode(input, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new AssertionError("Java must implement UTF-8");
    }
  }

  protected static String extractEntityId(Key key) {
    String rowId = key.getRow().toString();

    int endIndex = rowId.lastIndexOf(KEY_SEPARATOR);
    if (endIndex < 0) {
      throw new IllegalStateException(
          String.format("Row ID [%s] does not contain %c", rowId, KEY_SEPARATOR));
    }

    int beginIndex = rowId.lastIndexOf(COMPONENT_SEPARATOR, endIndex - 1);
    if (beginIndex < 0) {
      throw new IllegalStateException(
          String.format("Row ID [%s] does not contain %c", rowId, COMPONENT_SEPARATOR));
    }

    return unescape(rowId.substring(beginIndex + 1, endIndex));
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

  /**
   * Converts String list to database representation.
   *
   * @param value
   *     to convert
   * @return converted value
   */
  protected static String encode(List<String> value) {
    try {
      return OBJECT_MAPPER.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(String.format("Cannot convert %s to JSON", value), e);
    }
  }

  /**
   * Converts value from database representation to String list.
   *
   * @param json
   *     to convert
   * @return converted value
   */
  protected static List<String> decodeStringList(String json) {
    try {
      return OBJECT_MAPPER.readValue(json, STRING_LIST_TYPE_REFERENCE);
    } catch (IOException e) {
      throw new IllegalStateException(String.format("Cannot convert JSON [%s]", json), e);
    }
  }
}
