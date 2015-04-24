// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test code for the Group model.
 */
public class GroupTest extends ModelTestBase {
  @Test
  public <T extends Object> void testGroup() throws IOException, URISyntaxException {
    Group decoded = (Group) geneticEncodeDecodeTest(TestModels.group);

    assertEquals(decoded.getQuantity(), new BigDecimal(9001));
    assertTrue(decoded.getActual());
    assertEquals(decoded.getCode(), TestModels.codeable);
    assertEquals(decoded.getType(), GroupType.ANIMAL);
    assertEquals(decoded.getId(), Id.of("Group"));
    assertEquals(decoded.getCharacteristics(), Arrays.asList(TestModels.groupCharacteristic));
    assertEquals(decoded.getMembers(), Arrays.asList(TestModels.groupMember));
    assertEquals(decoded.getName(), "name");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("actual");
    jsonProperties.add("characteristics");
    jsonProperties.add("code");
    jsonProperties.add("@id");
    jsonProperties.add("members");
    jsonProperties.add("name");
    jsonProperties.add("quantity");
    jsonProperties.add("type");

    geneticJsonContainsFieldsTest(TestModels.group, jsonProperties);
  }
}
