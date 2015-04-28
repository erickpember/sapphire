// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.neovisionaries.i18n.CountryCode;
import java.time.Instant;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a physical address.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "Address")
public class Address {
  /** 1 or many lines that make up street name, number, direction, PO BOX, appt, etc. */
  @JsonProperty("lines")
  private List<String> lines;

  /** Home | work | temp | old - purpose of this address. Required in FHIR. */
  @JsonProperty("use")
  private AddressUse use;

  /** Text representation of the address.*/
  @JsonProperty("text")
  private String text;

  /** Time period when this address was/is in use. */
  @JsonProperty("period")
  private Interval<Instant> period;

  /** Name of the city.*/
  @JsonProperty("city")
  private String city;

  /** State or Province.*/
  @JsonProperty("stateProvince")
  private String stateProvince;

  /** Postal code for the province.*/
  @JsonProperty("postalCode")
  private String postalCode;

  /** Apartment or unit number.*/
  @JsonProperty("unit")
  private String unit;

  /** Name of the country.*/
  @JsonProperty("country")
  private CountryCode country;
}
