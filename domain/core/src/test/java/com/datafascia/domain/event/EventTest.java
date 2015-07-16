// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import com.datafascia.common.avro.Deserializer;
import com.datafascia.common.avro.Serializer;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import com.datafascia.domain.fhir.RaceEnum;
import com.neovisionaries.i18n.LanguageCode;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link Event} serialization test
 */
public class EventTest {

  private static final String TOPIC = "event";

  private MemorySchemaRegistry schemaRegistry = new MemorySchemaRegistry();
  private Serializer serializer = new Serializer(schemaRegistry);
  private Deserializer deserializer = new Deserializer(schemaRegistry);

  @Test
  public void observationDataTestAllFields() {
    ObservationData originalObservationData = ObservationData.builder()
        .abnormalFlags(Arrays.asList("flag1", "flag2"))
        .comments(Arrays.asList("comment1", "comment2"))
        .effectiveDateOfLastNormalObservation(Instant.now())
        .id("id")
        .natureOfAbnormalTest("natureOfAbnormalTest")
        .observationDateAndTime(Instant.now())
        .observationMethod(Arrays.asList("method1", "method2"))
        .observationType(ObservationType.A01)
        .probability("probability")
        .producersId("producersId")
        .referenceRange("referenceRange")
        .responsibleObserver("responsibleObserver")
        .resultStatus("resultStatus")
        .subId("subId")
        .userDefinedAccessChecks("userDefinedAccessChecks")
        .value(Arrays.asList("value"))
        .valueType("valueType").build();

    byte[] bytes = serializer.encodeReflect(TOPIC, originalObservationData);
    ObservationData observationData = deserializer.decodeReflect(TOPIC, bytes,
        ObservationData.class);
    assertEquals(observationData, originalObservationData);
  }

  @Test
  public void observationDataTestNoOptionalFields() {
    ObservationData originalObservationData = ObservationData.builder()
        .id("id")
        .observationType(ObservationType.A01)
        .resultStatus("resultStatus")
        .value(Arrays.asList("value"))
        .valueType("valueType").build();

    byte[] bytes = serializer.encodeReflect(TOPIC, originalObservationData);
    ObservationData observationData = deserializer.decodeReflect(TOPIC, bytes,
        ObservationData.class);
    assertEquals(observationData, originalObservationData);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void observationDataTestShouldFail() {
    ObservationData originalObservationData = ObservationData.builder().build();

    byte[] bytes = serializer.encodeReflect(TOPIC, originalObservationData);
    ObservationData observationData = deserializer.decodeReflect(TOPIC, bytes,
        ObservationData.class);
    assertEquals(observationData, originalObservationData);
  }

  @Test
  public void should_decode_admit_patient_event() {
    PatientData originalPatientData = PatientData.builder()
        .institutionPatientId("institutionPatientId")
        .accountNumber("accountNumber")
        .firstName("Wiley")
        .middleName("E")
        .lastName("Coyote")
        .gender(AdministrativeGenderEnum.MALE)
        .birthDate(LocalDate.now())
        .maritalStatus(MaritalStatusCodesEnum.M)
        .race(RaceEnum.WHITE)
        .language(LanguageCode.en)
        .build();
    EncounterData originalEncounterData = EncounterData.builder()
        .identifier("encounterIdentifier")
        .location("locationIdentifier")
        .admitTime(Instant.now())
        .build();
    AdmitPatientData originalAdmitPatientData = AdmitPatientData.builder()
        .patient(originalPatientData)
        .encounter(originalEncounterData)
        .build();
    Event originalEvent = Event.builder()
        .institutionId(URI.create("institution"))
        .facilityId(URI.create("facility"))
        .type(EventType.PATIENT_ADMIT)
        .data(originalAdmitPatientData)
        .build();
    byte[] bytes = serializer.encodeReflect(TOPIC, originalEvent);

    Event event = deserializer.decodeReflect(TOPIC, bytes, Event.class);
    assertEquals(event.getType(), originalEvent.getType());

    AdmitPatientData admitPatientData = (AdmitPatientData) event.getData();

    PatientData patientData = admitPatientData.getPatient();
    assertEquals(patientData.getFirstName(), originalPatientData.getFirstName());
    assertEquals(patientData.getGender(), originalPatientData.getGender());
    assertEquals(patientData.getBirthDate(), originalPatientData.getBirthDate());
    assertEquals(patientData.getLanguage(), originalPatientData.getLanguage());

    EncounterData encounterData = admitPatientData.getEncounter();
    assertEquals(encounterData.getAdmitTime(), originalEncounterData.getAdmitTime());
  }
}
