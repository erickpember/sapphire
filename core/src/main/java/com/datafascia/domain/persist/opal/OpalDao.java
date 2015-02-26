// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist.opal;

import com.codahale.metrics.Timer;
import com.datafascia.accumulo.AccumuloTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;

/**
 * Handles interacting with Accumulo instances that are formatted in the style of Opal.
 */
@Slf4j
public abstract class OpalDao {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  protected static final String ObjectStore = "ObjectStore";
  protected static final String PatientVisitMap = "PatientVisitMap";
  protected static final String admitWeight = "df_obxWeight";
  protected static final String admitHeight = "df_obxHeight";
  protected static final String admitTime = "dF_pv1AdmitDateTime";
  protected static final String LastVisitOiid = "LastVisitOiid";
  private static final String KEY_SEPARATOR = "\0";
  protected static final String PatientObject = "PatientObject";
  protected static final String OPAL_DATA = "opal_dF_data";

  protected AccumuloTemplate accumuloTemplate;

  /**
   * Constructor
   *
   * @param accumuloTemplate
   *     data access operations template
   */
  protected OpalDao(AccumuloTemplate accumuloTemplate) {
    this.accumuloTemplate = accumuloTemplate;
  }

  /**
   * Returns a usable scanner of the opal_dF_data table.
   *
   * @return A scanner for the opal_dF_data table.
   * @throws RuntimeException if table not found.
   */
  protected Scanner getScanner() {
    return accumuloTemplate.createScanner(OPAL_DATA);
  }

  protected static String[] splitKey(String key) {
    return key.split(KEY_SEPARATOR);
  }

  /**
   * Returns a key/value pairing of fields for a given Opal object.
   *
   * @param namespace The Opal namespace (e.g. ObjectStore)
   * @param kind The Opal kind (e.g. PatientVisitMap)
   * @param id The id of the Opal object.
   * @return An optional map pairing Opal fields to values.
   */
  protected Optional<Map<String, Value>> getObjectFields(String namespace, String kind, String id,
      Authorizations auths) {
    HashMap<String, Value> map = new HashMap<String, Value>();

    Scanner scanner = getScanner();
    scanner.setRange(toRange(namespace, kind, id));
    Iterator<Entry<Key, Value>> iter = scanner.iterator();
    if (!iter.hasNext()) {
      return Optional.empty();
    }

    while (iter.hasNext()) {
      Entry<Key, Value> e = iter.next();
      Value value = e.getValue();
      String[] colfStr = splitKey(e.getKey().getColumnFamily().toString());
      map.put(colfStr[0], value);
    }

    return Optional.of(map);
  }

  /**
   * Returns the value of a given field in an Opal Object.
   *
   * @param namespace The Opal namespace (e.g. ObjectStore)
   * @param kind The Opal kind (e.g. PatientVisitMap)
   * @param id The id of the Opal object.
   * @param requestedFieldName The name of the field to pull.
   * @return A given field value for an Opal object.
   */
  protected Optional<Value> getFieldValue(String namespace, String kind, String id,
      String requestedFieldName) {

    Timer.Context timerContext = accumuloTemplate.getTimerContext(
        getClass(), "getFieldValue", kind, requestedFieldName);
    try {
      Scanner scanner = getScanner();
      scanner.setRange(toRange(namespace, kind, id));
      Iterator<Entry<Key, Value>> iter = scanner.iterator();

      while (iter.hasNext()) {
        Entry<Key, Value> e = iter.next();

        // The column family contains the field name and the field type.
        String[] colfStr = splitKey(e.getKey().getColumnFamily().toString());
        String fieldType = colfStr[0];
        String fieldName = colfStr[1];

        if (fieldName.equals(requestedFieldName)) {
          return Optional.of(e.getValue());
        }
      }

      return Optional.empty();
    } finally {
      timerContext.stop();
    }
  }

  /**
   * Converts key to range covering row.
   */
  protected static Range toRange(String namespace, String kind, String id) {
    return Range.exact(namespace + KEY_SEPARATOR + kind + KEY_SEPARATOR + id);
  }

  /**
   * Converts key in ObjectStore namespace to range covering row.
   */
  protected static Range toRange(String kind, String id) {
    return toRange(ObjectStore, kind, id);
  }

  /**
   * Converts kind in ObjectStore namespace to range covering rows containing that kind.
   */
  protected static Range toRange(String kind) {
    return Range.prefix(ObjectStore + KEY_SEPARATOR + kind + KEY_SEPARATOR);
  }

  /**
   * Converts field type and field name to column family.
   */
  protected static Text toColumnFamily(FieldType fieldType, String fieldName) {
    return new Text(fieldType.name() + KEY_SEPARATOR + fieldName);
  }

  /**
   * Converts value from database representation to Java boolean.
   *
   * @param value
   *     to convert
   * @return converted value
   */
  protected static boolean decodeBoolean(Value value) {
    String json = decodeJson(value);
    try {
      return OBJECT_MAPPER.readValue(json, Boolean.class);
    } catch (IOException e) {
      throw new IllegalStateException(String.format("Cannot convert JSON [%s]", json), e);
    }
  }

  /**
   * Converts value from database representation to Java Date.
   *
   * @param value
   *     to convert
   * @return converted value
   */
  protected static Instant decodeDate(Value value) {
    String json = decodeJson(value);
    try {
      long millis = OBJECT_MAPPER.readValue(json, Long.class);
      return Instant.ofEpochMilli(millis);
    } catch (IOException e) {
      throw new IllegalStateException(String.format("Cannot convert JSON [%s]", json), e);
    }
  }

  /**
   * Converts value from database representation to Java long.
   *
   * @param value
   *     to convert
   * @return converted value
   */
  protected static long decodeLong(Value value) {
    String json = decodeJson(value);
    try {
      return OBJECT_MAPPER.readValue(json, Long.class);
    } catch (IOException e) {
      throw new IllegalStateException(String.format("Cannot convert JSON [%s]", json), e);
    }
  }

  /**
   * Converts value from database representation to Java String.
   *
   * @param value
   *     to convert
   * @return converted value
   */
  protected static String decodeString(Value value) {
    String json = decodeJson(value);
    try {
      return OBJECT_MAPPER.readValue(json, String.class);
    } catch (IOException e) {
      throw new IllegalStateException(String.format("Cannot convert JSON [%s]", json), e);
    }
  }

  /**
   * Converts value from database representation to Java String array.
   *
   * @param value
   *     to convert
   * @return converted value
   */
  protected static String[] decodeStringArray(Value value) {
    String json = decodeJson(value);
    try {
      return OBJECT_MAPPER.readValue(json, String[].class);
    } catch (IOException e) {
      throw new IllegalStateException(String.format("Cannot convert JSON [%s]", json), e);
    }
  }

  private static String decodeJson(Value value) {
    return new String(value.get(), StandardCharsets.UTF_8);
  }
}
