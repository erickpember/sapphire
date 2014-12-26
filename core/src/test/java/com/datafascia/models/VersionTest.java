// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.resources.ResourceUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Version model
 *
 * Do not use any package in core for package name as the jar file with the manifest is only created
 * after the tests complete successfully.
 */
@Slf4j
public class VersionTest {
  ObjectMapper mapper;

  @BeforeMethod
  public void beforeTests() {
    mapper = new ObjectMapper();
  }

  @Test
  public void decodeTest() throws IOException {
    String jsonString = ResourceUtils.resource("version.json");
    Version version = mapper.readValue(jsonString, Version.class);
    assertEquals(1, version.getId());
    assertEquals("dataFascia Corporation", version.getVendor());
    assertEquals("0.0.1-SNAPSHOT", version.getRevision());
    assertEquals("api-server", version.getTitle());
  }

  @Test
  public void encodeTest() throws IOException {
    Version version = new Version(1, "com.fasterxml.jackson.core");
    String jsonString = mapper.writeValueAsString(version);
  }

  @Test
  public void encodeDecodeTest() throws IOException {
    Version version = new Version(456789, "com.fasterxml.jackson.core");
    String jsonString = mapper.writeValueAsString(version);
    Version decodeVersion = mapper.readValue(jsonString, Version.class);
    assertEquals(version.getId(), decodeVersion.getId());
    assertEquals(version.getVendor(), decodeVersion.getVendor());
    assertEquals(version.getRevision(), decodeVersion.getRevision());
    assertEquals(version.getTitle(), decodeVersion.getTitle());
  }
}
