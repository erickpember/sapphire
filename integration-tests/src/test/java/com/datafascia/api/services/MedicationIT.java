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
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Integration tests for medication resources.
 */
@Slf4j
public class MedicationIT extends ApiTestSupport {

  @Test
  public void should_read_medication() throws Exception {
    String id = "code";
    Medication medication = client.read()
        .resource(Medication.class)
        .withId(id)
        .execute();

    assertEquals(medication.getId().getIdPart(), id);

    Bundle results = client.search().forResource(Medication.class).execute();
    List<IResource> medications = ApiUtil.extractBundle(results, Medication.class);
    assertTrue(medications.size() > 0, "No-argument search failed.");
  }

  @Test(expectedExceptions = ResourceNotFoundException.class)
  public void should_not_find_medication() {
    client.read()
        .resource(Medication.class)
        .withId("researchSupportingHomeopathy")
        .execute();
  }
}
