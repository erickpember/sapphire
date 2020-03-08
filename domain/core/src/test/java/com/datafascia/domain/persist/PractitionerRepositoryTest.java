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
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.IdentifierSystems;
import java.util.List;
import javax.inject.Inject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link PractitionerRepository} test
 */
public class PractitionerRepositoryTest extends RepositoryTestSupport {

  @Inject
  private PractitionerRepository practitionerRepository;

  private Practitioner createPractitioner(String identifier) {
    Practitioner practitioner = new Practitioner();
    practitioner.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PRACTITIONER).setValue(identifier);
    practitioner.setName(new HumanNameDt().addGiven("firstname").addGiven("middlename").addFamily(
        "lastname"));
    return practitioner;
  }

  @Test
  public void should_read_practitioner() {
    Practitioner originalPractitioner = createPractitioner("prac1");
    practitionerRepository.save(originalPractitioner);

    Practitioner unOriginalPractitioner = createPractitioner("prac2");
    practitionerRepository.save(unOriginalPractitioner);

    Id<Practitioner> practitionerId = Id.of(originalPractitioner.getId().getIdPart());
    Practitioner practitioner = practitionerRepository.read(practitionerId).get();
    assertEquals(practitioner.getName().getGivenFirstRep().getValue(), "firstname");

    List<Practitioner> practitioners = practitionerRepository.list();
    assertEquals(practitioners.size(), 2);
  }
}
