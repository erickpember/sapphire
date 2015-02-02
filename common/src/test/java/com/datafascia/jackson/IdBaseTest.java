// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
// For license information, please contact http://datafascia.com/contact
package com.datafascia.jackson;

import com.datafascia.common.persist.Id;
import com.datafascia.urn.annotations.IdNamespace;
import com.datafascia.urn.URNMap;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.testng.annotations.BeforeSuite;

/**
 * Base test for the serializer/deserializer
 */
public class IdBaseTest {
  @BeforeSuite
  public void setup() {
    // Load the mappings by scanning the package
    URNMap.idNSMapping("com.datafascia.jackson");
  }

  @Data @IdNamespace("test-ns")
  protected static class Observation {
    @JsonDeserialize(using = IdDeserializer.class) @JsonSerialize(using = IdSerializer.class)
    @JsonProperty("@id")
    private Id<Observation> id;
  }
}
