// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.jackson;

import com.datafascia.urn.URNMap;
import org.testng.annotations.BeforeSuite;

/**
 * Base test for Jackson serializers and deserializers
 */
public class JacksonBaseTest {
  @BeforeSuite
  public void setup() {
    // Load the mappings by scanning the package
    URNMap.idNSMapping("com.datafascia.jackson");
  }
}
