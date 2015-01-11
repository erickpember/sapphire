// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a human being.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
public class Person {
  @JsonProperty("name")
  private Name name;
  @JsonProperty("address")
  private Address address;
  @JsonProperty("gender")
  private Gender gender;
  @JsonProperty("birthDate")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
  private Date birthDate;
  @JsonProperty("photo")
  private URI photo;
  @JsonProperty("organization")
  private String organization;
}
