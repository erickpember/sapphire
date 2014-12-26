// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.resources;

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
