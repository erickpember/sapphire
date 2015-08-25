// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.dstu2.resource.Location;
import com.google.common.io.BaseEncoding;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Integration tests for location resources.
 */
@Slf4j
public class LocationIT extends ApiIT {
  // Used to encode URL arguments to bypass problems with URL-illegal characters.
  private static final BaseEncoding ENCODING = BaseEncoding.base64Url().omitPadding();

  /**
   * Validates Location retrieval.
   *
   * @throws Exception
   */
  @Test
  public void testLocation() throws Exception {
    String identifier = "13I^Room-2^Bed-B";
    String id = ENCODING.encode(identifier.getBytes(StandardCharsets.UTF_8));
    Location location = client.read()
        .resource(Location.class)
        .withId(id)
        .execute();

    assertEquals(location.getIdentifierFirstRep().getValue(), identifier);
  }
}
