// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.Month;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link com.datafascia.common.jackson.IdDeserializer} test
 */
public class LocalDateDeserializerTest extends LocalDateBaseTest {
  @Test
  public void should_deserialize_birthDate() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    Person expectedPerson = new Person();
    expectedPerson.setBirthDate(LocalDate.of(2009, Month.DECEMBER, 31));
    String json = objectMapper.writeValueAsString(expectedPerson);

    Person testPerson = objectMapper.readValue(json, Person.class);
    assertEquals(testPerson, expectedPerson);
  }
}
