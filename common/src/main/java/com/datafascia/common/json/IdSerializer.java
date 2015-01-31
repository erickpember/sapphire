// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.json;

import com.datafascia.common.persist.Id;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

/**
 * Custom JSON serializer for {@link Id} instances.
 */
public class IdSerializer extends StdSerializer<Id> implements ContextualSerializer {

  public IdSerializer() {
    super(Id.class);
  }

  @Override
  public JsonSerializer<?> createContextual(
      SerializerProvider prov, BeanProperty property) throws JsonMappingException {

    Class<?> declaringClass = property.getMember().getDeclaringClass();
    return new NamespacePrefixingIdSerializer(declaringClass);
  }

  @Override
  public void serialize(Id value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {

    throw new UnsupportedOperationException("serialize not implemented");
  }

  private static class NamespacePrefixingIdSerializer extends StdSerializer<Id> {
    private Class<?> declaringClass;

    public NamespacePrefixingIdSerializer(Class<?> declaringClass) {
      super(Id.class);
      this.declaringClass = declaringClass;
    }

    @Override
    public void serialize(Id value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException {
      
      jgen.writeString(declaringClass.getSimpleName() + ':' + value.toString());
    }
  }
}
