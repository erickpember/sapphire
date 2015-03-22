// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to create dF instance of object mapper. This ensures that all necessary
 * serializer/deserializers are loaded
 */
@Slf4j
public class DFObjectMapper {
  /**
   * @return dF instance of Jackson ObjectMapper with all modules loaded
   */
  public static ObjectMapper objectMapper() {
    return new ObjectMapper().findAndRegisterModules();
  }
}
