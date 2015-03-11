// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact

package com.datafascia.common.jackson;

import com.datafascia.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.measure.Unit;
import lombok.Data;

/**
 * Base test for the serializer/deserializer
 */
public class UnitBaseTest extends JacksonBaseTest {
  @Data @IdNamespace("test-UnitBaseTest")
  protected static class Observation {
    @JsonDeserialize(using = UnitDeserializer.class) @JsonSerialize(using = UnitSerializer.class)
    @JsonProperty("unit")
    private Unit unit;
  }
}
