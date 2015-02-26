// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import com.datafascia.common.avro.Deserializer;
import com.datafascia.common.avro.Serializer;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import com.datafascia.models.Gender;
import com.datafascia.models.MaritalStatus;
import com.datafascia.models.Race;
import com.neovisionaries.i18n.LanguageCode;
import java.net.URI;
import java.time.LocalDate;
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
  public void should_decode() {
    PatientData originalPatientData = PatientData.builder()
        .patientId("patientId")
        .accountNumber("accountNumber")
        .firstName("Wiley")
        .middleName("E")
        .lastName("Coyote")
        .gender(Gender.MALE)
        .birthDate(LocalDate.now())
        .maritalStatus(MaritalStatus.MARRIED)
        .race(Race.WHITE)
        .language(LanguageCode.en)
        .build();
    Event originalEvent = Event.builder()
        .institutionId(URI.create("institution"))
        .facilityId(URI.create("facility"))
        .type(EventType.ADMIT_PATIENT)
        .data(originalPatientData)
        .build();
    byte[] bytes = serializer.encodeReflect(TOPIC, originalEvent);

    Event event = deserializer.decodeReflect(TOPIC, bytes, Event.class);
    assertEquals(event.getType(), originalEvent.getType());

    PatientData patientData = (PatientData) event.getData();
    assertEquals(patientData.getFirstName(), originalPatientData.getFirstName());
    assertEquals(patientData.getGender(), originalPatientData.getGender());
    assertEquals(patientData.getBirthDate(), originalPatientData.getBirthDate());
    assertEquals(patientData.getLanguage(), originalPatientData.getLanguage());
  }
}
