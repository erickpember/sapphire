// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.google.common.io.BaseEncoding;
import java.nio.charset.StandardCharsets;
import java.util.List;
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

  @Test
  public void should_read_location() throws Exception {
    String identifier = "13I^Room-2^Bed-B";
    String id = ENCODING.encode(identifier.getBytes(StandardCharsets.UTF_8));
    Location location = client.read()
        .resource(Location.class)
        .withId(id)
        .execute();

    assertEquals(location.getIdentifierFirstRep().getValue(), identifier);

    Bundle results = client.search().forResource(Location.class).execute();
    List<IResource> locations = ApiUtil.extractBundle(results, Location.class);
    assertEquals(locations.size(), 3);
  }

  @Test(expectedExceptions = ResourceNotFoundException.class)
  public void should_not_find_location() {
    client.read()
        .resource(Location.class)
        .withId("restaurantWithNoScreamingToddlers")
        .execute();
  }
}
