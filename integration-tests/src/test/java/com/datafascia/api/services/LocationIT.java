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
import static org.testng.Assert.assertTrue;

/**
 * Integration tests for location resources.
 */
@Slf4j
public class LocationIT extends ApiTestSupport {
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
    assertTrue(locations.size() > 1, "No-argument search failed.");
  }

  @Test(expectedExceptions = ResourceNotFoundException.class)
  public void should_not_find_location() {
    client.read()
        .resource(Location.class)
        .withId("restaurantWithNoScreamingToddlers")
        .execute();
  }
}
