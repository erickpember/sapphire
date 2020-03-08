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
package com.datafascia.common.io;

import com.google.common.base.Charsets;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for ResourceUtils
 */
@Slf4j
public class ResourceUtilsTest {
  @Test
  public void utf8Resource() throws IOException {
    String content = ResourceUtils.resource("utf8.txt", Charsets.UTF_8);
    assertEquals(content, "ᚠᛇᚻ᛫ᛒᛦᚦ᛫ᚠᚱᚩᚠᚢᚱ᛫ᚠᛁᚱᚪ᛫ᚷᛖᚻᚹᛦᛚᚳᚢᛗ");
  }

  @Test
  public void latinResource() throws IOException {
    String content = ResourceUtils.resource("latin.txt", Charsets.ISO_8859_1);
    assertEquals(content, "äöü ÄÖÜ");
  }

  @Test
  public void defaultCharResource() throws IOException {
    String content = ResourceUtils.resource("utf8.txt");
    assertEquals(content, "ᚠᛇᚻ᛫ᛒᛦᚦ᛫ᚠᚱᚩᚠᚢᚱ᛫ᚠᛁᚱᚪ᛫ᚷᛖᚻᚹᛦᛚᚳᚢᛗ");
  }
}
