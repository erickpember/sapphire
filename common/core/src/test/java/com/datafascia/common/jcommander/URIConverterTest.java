// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jcommander;

import com.beust.jcommander.ParameterException;
import java.net.URI;
import org.testng.annotations.Test;

/**
 * Class to test URI conversion.
 */
public class URIConverterTest {
  URIConverter uc = new URIConverter("test");

  // Fail when fed a bad URI.
  @Test(expectedExceptions = ParameterException.class)
  public void badUrl() {
    URI convert = uc.convert("\\\\network\\driveD\\files\\data");
  }

  // Valid URIs should pass fine.
  @Test
  public void goodUrl() {
    URI convert1 = uc.convert("http://www.datafascia.com");
    URI convert2 = uc.convert("urn:patient:92356");
  }
}
