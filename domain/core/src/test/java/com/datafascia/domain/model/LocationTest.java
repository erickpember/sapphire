// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for location model.
 */
public class LocationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testLocation() throws IOException, URISyntaxException {
    Location decoded = (Location) geneticEncodeDecodeTest(TestModels.location);

    assertEquals(decoded.getId(), Id.of("1234"));
    assertEquals(decoded.getName(), "Castle Anthrax");
    assertEquals(decoded.getDescription(), "Look for a grail-shaped beacon");
    assertEquals(decoded.getMode(), LocationMode.INSTANCE);
    assertEquals(decoded.getType(), TestModels.codeable);
    assertEquals(decoded.getTelecoms(), Arrays.asList(TestModels.contactPoint));
    assertEquals(decoded.getAddress(), TestModels.address);
    assertEquals(decoded.getPhysicalType(), TestModels.codeable);
    assertEquals(decoded.getPosition(), TestModels.position);
    assertEquals(decoded.getManagingOrganizationId(), Id.of("organization"));
    assertEquals(decoded.getPartOfId(), Id.of("partOf"));
    assertEquals(decoded.getStatus(), LocationStatus.INACTIVE);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("@id");
    jsonProperties.add("name");
    jsonProperties.add("description");
    jsonProperties.add("mode");
    jsonProperties.add("type");
    jsonProperties.add("telecoms");
    jsonProperties.add("address");
    jsonProperties.add("physicalType");
    jsonProperties.add("position");
    jsonProperties.add("managingOrganizationId");
    jsonProperties.add("partOfId");
    jsonProperties.add("status");
    geneticJsonContainsFieldsTest(TestModels.location, jsonProperties);
  }
}
