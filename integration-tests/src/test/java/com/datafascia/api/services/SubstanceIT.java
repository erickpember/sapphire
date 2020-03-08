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
import ca.uhn.fhir.model.dstu2.resource.Substance;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.util.List;
import org.testng.annotations.Test;

import static com.datafascia.api.services.ApiTestSupport.client;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Integration tests for substance resources
 */
public class SubstanceIT extends ApiTestSupport {
  /**
   * Fetch admitted patients and validate them.
   *
   * @throws Exception
   */
  @Test
  public void testSubstance() throws Exception {

    int count = 0;

    Bundle results = client.search()
        .forResource(Substance.class)
        .execute();

    List<IResource> substances = ApiUtil.extractBundle(results, Substance.class);

    assertTrue(substances.size() >= 3, "testSubstance did not find its expected substances! Found "
        + results.size() + " results.");

    // test read
    Substance substance = client.read()
        .resource(Substance.class)
        .withId("aaaaaaaaae")
        .execute();
    assertEquals(substance.getId().getIdPart(), "aaaaaaaaae");
  }

  @Test(expectedExceptions = ResourceNotFoundException.class)
  public void should_not_find_substance() {
    client.read()
        .resource(Substance.class)
        .withId("thatIngredientThatOwesMe200MG")
        .execute();
  }
}
