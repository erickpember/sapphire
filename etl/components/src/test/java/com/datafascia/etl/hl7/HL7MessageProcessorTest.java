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

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link HL7MessageProcessor} test
 */
@Test(singleThreaded = true)
public class HL7MessageProcessorTest extends HL7MessageProcessorTestSupport {

  @Test
  public void ADT_A01_should_create_encounter_in_progress() throws Exception {
    processMessage("ADT_A01.hl7");

    Id<Encounter> encounterId = Id.of("5014212");
    Encounter encounter = encounterRepository.read(encounterId).get();

    assertEquals(encounter.getStatusElement().getValueAsEnum(), EncounterStateEnum.IN_PROGRESS);
  }

  @Test
  public void ADT_A02_should_update_encounter_in_progress() throws Exception {
    processMessage("ADT_A01.hl7");
    processMessage("ADT_A02.hl7");

    Id<Encounter> encounterId = Id.of("5014212");
    Encounter encounter = encounterRepository.read(encounterId).get();

    assertEquals(encounter.getStatusElement().getValueAsEnum(), EncounterStateEnum.IN_PROGRESS);

    Id<Location> locationId = Id.of(
        encounter.getLocationFirstRep().getLocation().getReference().getIdPart());
    Location location = locationRepository.read(locationId).get();

    assertEquals(location.getIdentifierFirstRep().getValue(), "A4I^A4561^05^5102^R^^^^^^OUTADT");
  }

  @Test
  public void ADT_A03_should_update_encounter_finished() throws Exception {
    processMessage("ADT_A01.hl7");
    processMessage("ADT_A03.hl7");

    Id<Encounter> encounterId = Id.of("5014212");
    Encounter encounter = encounterRepository.read(encounterId).get();

    assertEquals(encounter.getStatusElement().getValueAsEnum(), EncounterStateEnum.FINISHED);
  }

  // HL7 defines that ADT A04 reuses the same message structure as ADT A01.
  @Test
  public void ADT_A04_should_create_encounter_arrived() throws Exception {
    processMessage("ADT_A04.hl7");

    Id<Encounter> encounterId = Id.of("5014212");
    Encounter encounter = encounterRepository.read(encounterId).get();

    assertEquals(encounter.getStatusElement().getValueAsEnum(), EncounterStateEnum.ARRIVED);
  }

  @Test
  public void ADT_A06_should_update_encounter_in_progress() throws Exception {
    processMessage("ADT_A01.hl7");
    processMessage("ADT_A06.hl7");

    Id<Encounter> encounterId = Id.of("5014212");
    Encounter encounter = encounterRepository.read(encounterId).get();

    assertEquals(encounter.getStatusElement().getValueAsEnum(), EncounterStateEnum.IN_PROGRESS);
  }

  // HL7 defines that ADT A07 reuses the same message structure as ADT A06.
  @Test
  public void ADT_A07_should_update_encounter_in_progress() throws Exception {
    processMessage("ADT_A01.hl7");
    processMessage("ADT_A07.hl7");

    Id<Encounter> encounterId = Id.of("5014212");
    Encounter encounter = encounterRepository.read(encounterId).get();

    assertEquals(encounter.getStatusElement().getValueAsEnum(), EncounterStateEnum.IN_PROGRESS);
  }

  // HL7 defines that ADT A08 reuses the same message structure as ADT A01.
  @Test
  public void ADT_A08_should_update_encounter_in_progress() throws Exception {
    processMessage("ADT_A01.hl7");
    processMessage("ADT_A08.hl7");

    Id<Encounter> encounterId = Id.of("5014212");
    Encounter encounter = encounterRepository.read(encounterId).get();

    assertEquals(encounter.getStatusElement().getValueAsEnum(), EncounterStateEnum.IN_PROGRESS);
  }

  @Test
  public void ADT_A08_should_create_nonexistent_encounter_in_progress() throws Exception {
    processMessage("ADT_A08.hl7");

    Id<Encounter> encounterId = Id.of("5014212");
    Encounter encounter = encounterRepository.read(encounterId).get();

    assertEquals(encounter.getStatusElement().getValueAsEnum(), EncounterStateEnum.IN_PROGRESS);
  }

  // HL7 defines that ADT A11 reuses the same message structure as ADT A09.
  @Test
  public void ADT_A11_should_update_encounter_cancelled() throws Exception {
    processMessage("ADT_A01.hl7");
    processMessage("ADT_A11.hl7");

    Id<Encounter> encounterId = Id.of("5014212");
    Encounter encounter = encounterRepository.read(encounterId).get();

    assertEquals(encounter.getStatusElement().getValueAsEnum(), EncounterStateEnum.CANCELLED);
  }

  // HL7 defines that ADT A12 reuses the same message structure as ADT A09.
  @Test
  public void ADT_A12_should_update_encounter_in_progress() throws Exception {
    processMessage("ADT_A01.hl7");
    processMessage("ADT_A02.hl7");
    processMessage("ADT_A12.hl7");

    Id<Encounter> encounterId = Id.of("5014212");
    Encounter encounter = encounterRepository.read(encounterId).get();

    assertEquals(encounter.getStatusElement().getValueAsEnum(), EncounterStateEnum.IN_PROGRESS);

    Id<Location> locationId = Id.of(
        encounter.getLocationFirstRep().getLocation().getReference().getIdPart());
    Location location = locationRepository.read(locationId).get();

    assertEquals(location.getIdentifierFirstRep().getValue(), "A6A^A6597^28^5102^D^^^^^^OUTADT");
  }

  // HL7 defines that ADT A13 reuses the same message structure as ADT A01.
  @Test
  public void ADT_A13_should_update_encounter_in_progress() throws Exception {
    processMessage("ADT_A01.hl7");
    processMessage("ADT_A03.hl7");
    processMessage("ADT_A13.hl7");

    Id<Encounter> encounterId = Id.of("5014212");
    Encounter encounter = encounterRepository.read(encounterId).get();

    assertEquals(encounter.getStatusElement().getValueAsEnum(), EncounterStateEnum.IN_PROGRESS);
  }

  @Test
  public void ADT_A17_should_update_encounter_in_progress() throws Exception {
    processMessage("ADT_A01.hl7");
    processMessage("ADT_A17.hl7");

    Id<Encounter> encounterId = Id.of("5014212");
    Encounter encounter = encounterRepository.read(encounterId).get();

    assertEquals(encounter.getStatusElement().getValueAsEnum(), EncounterStateEnum.IN_PROGRESS);
  }

  private void assertPatientGenderEquals(AdministrativeGenderEnum expected) {
    Id<UnitedStatesPatient> patientId = Id.of("97552037");
    UnitedStatesPatient patient = patientRepository.read(patientId).get();

    assertEquals(patient.getGenderElement().getValueAsEnum(), expected);
  }

  @Test
  public void should_extract_gender_female() throws Exception {
    processMessage("gender-female.hl7");
    assertPatientGenderEquals(AdministrativeGenderEnum.FEMALE);
  }

  @Test
  public void should_extract_gender_male() throws Exception {
    processMessage("gender-male.hl7");
    assertPatientGenderEquals(AdministrativeGenderEnum.MALE);
  }

  @Test
  public void should_extract_gender_other() throws Exception {
    processMessage("gender-other.hl7");
    assertPatientGenderEquals(AdministrativeGenderEnum.OTHER);
  }

  @Test
  public void should_extract_gender_unknown() throws Exception {
    processMessage("gender-unknown.hl7");
    assertPatientGenderEquals(AdministrativeGenderEnum.UNKNOWN);
  }
}
