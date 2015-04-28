// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.io.ResourceUtils;
import com.datafascia.common.jackson.DFObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Version model
 * <p>
 * Do not use any package in core for package name as the jar file with the manifest is only created
 * after the tests complete successfully.
 */
@Slf4j
public class VersionTest extends ModelTestBase {

  private static final ObjectMapper OBJECT_MAPPER = DFObjectMapper.objectMapper();

  @Test
  public void testSampledData() throws Exception {
    geneticEncodeDecodeTest(TestModels.sampledData);
  }

  @Test
  public void decodeTest() throws Exception {
    String jsonString = ResourceUtils.resource("version.json");
    Version version = OBJECT_MAPPER.readValue(jsonString, Version.class);
    assertEquals(1, version.getId());
    assertEquals("dataFascia Corporation", version.getVendor());
    assertEquals("0.0.1-SNAPSHOT", version.getRevision());
    assertEquals("api-server", version.getTitle());
  }

  @Test
  public void encodeTest() throws Exception {
    Version version = new Version(1, "com.fasterxml.jackson.core");
    OBJECT_MAPPER.writeValueAsString(version);
  }

  @Test
  public void encodeDecodeTest() throws Exception {
    Version version = new Version(456789, "com.fasterxml.jackson.core");
    String jsonString = OBJECT_MAPPER.writeValueAsString(version);
    Version decodeVersion = OBJECT_MAPPER.readValue(jsonString, Version.class);
    assertEquals(version.getId(), decodeVersion.getId());
    assertEquals(version.getVendor(), decodeVersion.getVendor());
    assertEquals(version.getRevision(), decodeVersion.getRevision());
    assertEquals(version.getTitle(), decodeVersion.getTitle());
  }
}
