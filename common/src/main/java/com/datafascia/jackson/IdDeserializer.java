// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.jackson;

import com.datafascia.common.persist.Id;
import com.datafascia.urn.URNFactory;
import com.datafascia.urn.URNMap;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;

/**
 * Jackson serializer for Id
 * 
 * NOTE: the SuppressWarnings is necessary because we do not have the underlying type for the Id.
 */
@Slf4j @SuppressWarnings("rawtypes")
public class IdDeserializer extends JsonDeserializer<Id> implements ContextualDeserializer {
  Class<?> declaringClass;

  /**
   * null constructor
   */
  public IdDeserializer() {
    declaringClass = IdDeserializer.class;
  }

  /**
   * Create deserializer for class
   *
   * @param clazz the class
   */
  public IdDeserializer(Class<?> clazz) {
    declaringClass = clazz;
  }

  @Override
  public JsonDeserializer<?> createContextual(
      DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
    return new IdDeserializer(property.getMember().getDeclaringClass());
  }

  @Override
  public Id deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonMappingException {
    String text = jp.getText();
    try {
      String ns = URNFactory.namespace(text);
      if (URNMap.getClassFromIDNamespace(ns).equals(declaringClass)) {
        return Id.of(URNFactory.path(text));
      }
    } catch (URISyntaxException e) {
    }
    throw new JsonMappingException("Illegal URN or namespace mapping for " + text);
  }
}
