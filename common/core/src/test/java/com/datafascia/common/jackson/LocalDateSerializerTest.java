// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.common.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.Month;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link com.datafascia.common.jackson.IdSerializer} test
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
