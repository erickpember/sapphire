// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Duration;
import lombok.Data;

/**
 * Base test for the serializer/deserializer
 */
public class DurationBaseTest extends JacksonBaseTest {
  @Data @IdNamespace("test-DurationBaseTest")
  protected static class HealthcareServiceAvailableTime {
    @JsonProperty("availableStartTime") @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration availableStartTime;
  }
}
