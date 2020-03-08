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
package com.datafascia.etl.hl7;

import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.HumanNames;
import com.datafascia.emerge.ucsf.valueset.PractitionerRoleEnum;
import java.util.List;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Tests {@link HL7MessageProcessor} processes participants
 */
@Test(singleThreaded = true)
public class ParticipantTest extends HL7MessageProcessorTestSupport {

  @Test
  public void should_extract_participant_primary_attending() throws Exception {
    processMessage("participant-primary-care-attending.hl7");
    processMessage("participant-primary-care-attending.hl7");

    Id<Encounter> encounterId = Id.of("5014212");
    Encounter encounter = encounterRepository.read(encounterId).get();
    assertEquals(encounter.getParticipant().size(), 1);

    Encounter.Participant particpant = encounter.getParticipantFirstRep();
    IdDt idDt = particpant.getIndividual().getReference();
    assertEquals(idDt.getResourceType(), Practitioner.class.getSimpleName());

    Id<Practitioner> practitionerId = Id.of(idDt.getIdPart());
    Practitioner practitioner = practitionerRepository.read(practitionerId).get();
    assertEquals(practitioner.getIdentifierFirstRep().getValue(), "14852");
    assertEquals(HumanNames.toFullName(practitioner.getName()), "DANIEL ELI ROTH");

    CodingDt roleCoding = practitioner.getPractitionerRoleFirstRep().getRole().getCodingFirstRep();
    assertEquals(roleCoding.getSystem(), PractitionerRoleEnum.PRIMARY_CARE_ATTENDING.getSystem());
    assertEquals(roleCoding.getCode(), PractitionerRoleEnum.PRIMARY_CARE_ATTENDING.getCode());
  }

  private void assertPractitionerRole(
      Encounter.Participant particpant, PractitionerRoleEnum expectedRole) {

    Id<Practitioner> practitionerId = Id.of(particpant.getIndividual().getReference().getIdPart());
    Practitioner practitioner = practitionerRepository.read(practitionerId).get();

    CodingDt roleCoding = practitioner.getPractitionerRoleFirstRep().getRole().getCodingFirstRep();
    assertEquals(roleCoding.getCode(), expectedRole.getCode());
  }

  @Test
  public void should_extract_participant_multiple() throws Exception {
    processMessage("participant-multiple.hl7");
    processMessage("participant-multiple.hl7");

    Id<Encounter> encounterId = Id.of("5014212");
    Encounter encounter = encounterRepository.read(encounterId).get();
    assertEquals(encounter.getParticipant().size(), 3);

    List<Encounter.Participant> participants = encounter.getParticipant();
    assertPractitionerRole(participants.get(0), PractitionerRoleEnum.PRIMARY_CARE_ATTENDING);
    assertPractitionerRole(participants.get(1), PractitionerRoleEnum.ICU_ATTENDING);
    assertPractitionerRole(participants.get(2), PractitionerRoleEnum.CLINICAL_NURSE);
  }
}
