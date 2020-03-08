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
