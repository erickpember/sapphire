// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.Month;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link IdSerializer} test
 */
public class LocalDateSerializerTest extends LocalDateBaseTest {
  @Test
  public void should_serialize_birthDate() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    Person person = new Person();
    person.setBirthDate(LocalDate.of(2009, Month.DECEMBER, 31));

    String json = objectMapper.writeValueAsString(person);
    assertEquals(json, "{\"birthDate\":\"20091231\"}");
  }
}
