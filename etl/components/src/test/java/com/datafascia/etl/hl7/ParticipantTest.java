// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.HumanNames;
import com.datafascia.emerge.ucsf.valueset.PractitionerRoleEnum;
import java.util.List;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

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

    PeriodDt period = particpant.getPeriod();
    assertEquals(period.getStart().toInstant().toString(), "2014-10-01T07:00:00Z");
    assertNull(period.getEnd());
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
