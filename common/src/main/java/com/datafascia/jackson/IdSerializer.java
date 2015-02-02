// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.jackson;

import com.datafascia.common.persist.Id;
import com.datafascia.urn.URNFactory;
import com.datafascia.urn.URNMap;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * Jackson serializer for Id
 * 
 * NOTE: the SuppressWarnings is necessary because we do not have the underlying type for the Id.
 */
@Slf4j @SuppressWarnings("rawtypes")
public class IdSerializer extends StdSerializer<Id> implements ContextualSerializer {
  public IdSerializer() {
    super(Id.class);
  }

  @Override
  public JsonSerializer<?> createContextual(
      SerializerProvider prov, BeanProperty property) throws JsonMappingException {
    Class<?> declaringClass = property.getMember().getDeclaringClass();

    return new IdURNSerializer(declaringClass);
  }

  @Override
  public void serialize(Id value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    log.error("IdSerializer serializer should never be called.");
    throw new UnsupportedOperationException("IdSerializer serializer should never be called.");
  }

  /*
   * The actual serializer
   */
  private static class IdURNSerializer extends StdSerializer<Id> {
    private Class<?> declaringClass;

    /**
     * default constructor
     *
     * @param declaringClass the declaring class
     */
    public IdURNSerializer(Class<?> declaringClass) {
      super(Id.class);
      this.declaringClass = declaringClass;
    }

    @Override
    public void serialize(Id value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException {
      jgen.writeString(URNFactory.urn(URNMap.getIdNamespace(declaringClass), value.toString()));
    }
  }
}
