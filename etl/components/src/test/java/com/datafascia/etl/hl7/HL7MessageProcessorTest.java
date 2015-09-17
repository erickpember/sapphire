// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.configuration.guice.ConfigureModule;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.etl.inject.ComponentsModule;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link HL7MessageProcessor} test
 */
@Test(singleThreaded = true)
public class HL7MessageProcessorTest {

  private static class TestModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public ConnectorFactory connectorFactory() {
      return new ConnectorFactory(
          AccumuloConfiguration.builder()
          .instance(ConnectorFactory.MOCK_INSTANCE)
          .zooKeepers("")
          .user("root")
          .password("secret")
          .build());
    }
  }

  @Inject
  private HL7MessageProcessor hl7MessageProcessor;

  @Inject
  private EncounterRepository encounterRepository;

  @BeforeMethod
  public void injectMembers() throws Exception {
    Injector injector = Guice.createInjector(
        new TestModule(), new ConfigureModule(), new ComponentsModule());
    injector.injectMembers(this);
  }

  private void processMessage(String hl7File) throws IOException {
    URL url = HL7MessageProcessorTest.class.getResource(hl7File);
    String message = Resources.toString(url, StandardCharsets.UTF_8).replace('\n', '\r');
    hl7MessageProcessor.accept(message.getBytes(StandardCharsets.UTF_8));
  }

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
    processMessage("ADT_A12.hl7");

    Id<Encounter> encounterId = Id.of("5014212");
    Encounter encounter = encounterRepository.read(encounterId).get();

    assertEquals(encounter.getStatusElement().getValueAsEnum(), EncounterStateEnum.IN_PROGRESS);
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
}
