// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.datafascia.common.persist.Id;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Module adds serializer and deserializer for persist classes.
 */
public class PersistModule extends SimpleModule {

  /**
   * Constructor
   */
  public PersistModule() {
    addSerializer(Id.class, new IdSerializer());
    addDeserializer(Id.class, new IdDeserializer());
  }
}
