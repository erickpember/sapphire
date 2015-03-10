// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.csv;

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
