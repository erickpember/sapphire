// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a physical address.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
public class Address {
  @JsonProperty("street")
  private String street;
  @JsonProperty("city")
  private String city;
  @JsonProperty("stateProvince")
  private String stateProvince;
  @JsonProperty("postalCode")
  private String postalCode;
  @JsonProperty("unit")
  private String unit;
  @JsonProperty("country")
  private CountryCode country;
}
