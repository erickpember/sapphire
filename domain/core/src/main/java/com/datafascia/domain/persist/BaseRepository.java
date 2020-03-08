// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.domain.persist;

import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.jackson.DFObjectMapper;
import com.datafascia.common.persist.Id;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import javax.measure.Unit;
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
   * Converts value from database representation to specified Java type.
   *
   * @param json
   *     to convert
   * @param typeReference
   *     type reference
   * @param <T>
   *     target Java type
   * @return converted value
   */
  protected static <T> T decode(byte[] json, TypeReference<T> typeReference) {
    try {
      return OBJECT_MAPPER.readValue(json, typeReference);
    } catch (IOException e) {
      String jsonString = new String(json, StandardCharsets.UTF_8);
      throw new IllegalStateException(
          "Cannot convert JSON " + jsonString + " to type " + typeReference, e);
    }
  }

  /**
   * Converts value from database representation to BigDecimal.
   *
   * @param json
   *     to convert
   * @return converted value
   */
  protected static BigDecimal decodeBigDecimal(byte[] json) {
    try {
      return OBJECT_MAPPER.readValue(json, BigDecimal.class);
    } catch (IOException e) {
      String jsonString = new String(json, StandardCharsets.UTF_8);
      throw new IllegalStateException("Cannot convert JSON " + jsonString, e);
    }
  }

  /**
   * Converts value from database representation to boolean.
   *
   * @param json
   *     to convert
   * @return converted value
   */
  protected static boolean decodeBoolean(byte[] json) {
    try {
      return OBJECT_MAPPER.readValue(json, Boolean.class);
    } catch (IOException e) {
      String jsonString = new String(json, StandardCharsets.UTF_8);
      throw new IllegalStateException("Cannot convert JSON " + jsonString, e);
    }
  }

  /**
   * Converts value from database representation to Instant.
   *
   * @param json
   *     to convert
   * @return converted value
   */
  protected static Instant decodeInstant(byte[] json) {
    try {
      return OBJECT_MAPPER.readValue(json, Instant.class);
    } catch (IOException e) {
      String jsonString = new String(json, StandardCharsets.UTF_8);
      throw new IllegalStateException("Cannot convert JSON " + jsonString, e);
    }
  }

  /**
   * Converts value from database representation to LocalDate.
   *
   * @param json
   *     to convert
   * @return converted value
   */
  protected static LocalDate decodeLocalDate(byte[] json) {
    try {
      return OBJECT_MAPPER.readValue(json, LocalDate.class);
    } catch (IOException e) {
      String jsonString = new String(json, StandardCharsets.UTF_8);
      throw new IllegalStateException("Cannot convert JSON " + jsonString, e);
    }
  }

  /**
   * Converts value from database representation to String.
   *
   * @param json
   *     to convert
   * @return converted value
   */
  protected static String decodeString(byte[] json) {
    try {
      return OBJECT_MAPPER.readValue(json, String.class);
    } catch (IOException e) {
      String jsonString = new String(json, StandardCharsets.UTF_8);
      throw new IllegalStateException("Cannot convert JSON " + jsonString, e);
    }
  }

  /**
   * Converts value from database representation to String list.
   *
   * @param json
   *     to convert
   * @return converted value
   */
  protected static List<String> decodeStringList(byte[] json) {
    try {
      return OBJECT_MAPPER.readValue(json, STRING_LIST_TYPE_REFERENCE);
    } catch (IOException e) {
      String jsonString = new String(json, StandardCharsets.UTF_8);
      throw new IllegalStateException("Cannot convert JSON " + jsonString, e);
    }
  }

  /**
   * Converts value from database representation to Unit.
   *
   * @param json
   *     to convert
   * @return converted value
   */
  protected static Unit decodeUnit(byte[] json) {
    try {
      return OBJECT_MAPPER.readValue(json, Unit.class);
    } catch (IOException e) {
      String jsonString = new String(json, StandardCharsets.UTF_8);
      throw new IllegalStateException("Cannot convert JSON " + jsonString, e);
    }
  }
}
