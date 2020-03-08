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

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.EpisodeOfCare;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.IdentifierSystems;
import javax.inject.Inject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Integration tests for EpisodeOfCare resources.
 */
public class EpisodeOfCareIT extends ApiTestSupport {
  @Inject
  private ClientBuilder clientBuilder;

  @Test
  public void should_write_and_read() {
    EpisodeOfCare eoc1 = createEpisodeOfCare();
    clientBuilder.getEpisodeOfCareClient().save(eoc1);

    EpisodeOfCare eoc2 = clientBuilder.getEpisodeOfCareClient().get(eoc1.getId().getIdPart());

    assertNotNull(eoc2);
    assertEquals(eoc1.getIdentifierFirstRep().getValue(), eoc2.getIdentifierFirstRep().getValue());
    assertEquals(eoc1.getPatient().getReference().getIdPart(),
        eoc2.getPatient().getReference().getIdPart());
  }

  private EpisodeOfCare createEpisodeOfCare() {
    EpisodeOfCare eoc = new EpisodeOfCare();
    eoc.setPatient(new ResourceReferenceDt().setReference("96087004"));
    PeriodDt period = new PeriodDt();
    period.setStart(DateTimeDt.withCurrentTime());
    eoc.setPeriod(period);
    eoc.addIdentifier().setSystem(IdentifierSystems.EPISODE_OF_CARE).setValue("testepisode");
    return eoc;
  }
}
