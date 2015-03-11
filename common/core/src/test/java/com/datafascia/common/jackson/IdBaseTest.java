// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * Base test for the serializer/deserializer
 */
public class IdBaseTest extends JacksonBaseTest {
  @Data @IdNamespace("test-IdBaseTest")
  protected static class Observation {
    @JsonDeserialize(using = IdDeserializer.class) @JsonSerialize(using = IdSerializer.class)
    @JsonProperty("@id")
    private Id<Observation> id;
  }
}
