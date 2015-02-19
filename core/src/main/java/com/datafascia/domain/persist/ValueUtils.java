// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.time.Instant;
import org.apache.accumulo.core.data.Value;

/**
 * Contains utilities for converting Values to actual types.
 */
public class ValueUtils {

  /**
   * Returns a date for a given Accumulo Value.
   * @param value The value to decode.
   * @return A date representation of the Value.
   */
  public static Instant getDate(Value value) {
    return Instant.ofEpochMilli(getLong(value));
  }

  /**
   * Returns a boolean for a given Accumulo Value.
   * @param value The value to decode.
   * @return A bool representation of the Value.
   */
  public static boolean getBool(Value value) {
    return value.toString().equals("true");
  }

  /**
   * Returns a Long for a given Accumulo Value.
   * @param value The value to decode.
   * @return A long representation of the Value.
   */
  public static Long getLong(Value value) {
    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
    buffer.put(value.get());
    buffer.flip();
    return buffer.getLong();
  }

  /**
   * Returns a String for a given Accumulo Value.
   * @param value The value to decode.
   * @return A string representation of the Value.
   */
  public static String getString(Value value) {
    try {
      return new String(value.get(), "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }

  private ValueUtils() {
  }
}
