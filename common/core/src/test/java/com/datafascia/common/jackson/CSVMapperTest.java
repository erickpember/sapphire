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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Arrays;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test for the CSVMapper
 */
@Slf4j
public class CSVMapperTest {
  CSVMapper<TestCSV> mapper = new CSVMapper<>(TestCSV.class);
  TestCSV testcsvObj = new TestCSV() {
    {
      setVal1("4");
      setVal2("teststr");
    }
  };
  String testcsvStr = "4,teststr";

  // Turn an object to a CSV line.
  @Test
  public void in() throws JsonProcessingException {
    String csv = mapper.asCSV(testcsvObj);
    assertEquals(csv, testcsvStr);
  }

  // Turn a line from a CSV into an object.
  @Test
  public void out() throws IOException {
    TestCSV csv = mapper.fromCSV(testcsvStr);
    assertEquals(csv, testcsvObj);
  }

  // Make sure the headers are what we expect.
  @Test
  public void header() {
    assertEquals(mapper.getHeaders(), Arrays.asList("Value One", "Value Two"));
  }

  /**
   * A dummy class to test with.
   */
  @NoArgsConstructor @EqualsAndHashCode
  public static class TestCSV {
    @Getter @Setter @JsonProperty(value = "Value One", index = 0)
    String val1;
    @Getter @Setter @JsonProperty(value = "Value Two", index = 1)
    String val2;
  }
}
