// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import lombok.Data;

/**
 * Base test for the serializer/deserializer
 */
public class LocalDateBaseTest {
  @Data @IdNamespace("test-LocalDateBaseTest")
  protected static class Person {
    @JsonProperty("birthDate") @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate birthDate;
  }
}
