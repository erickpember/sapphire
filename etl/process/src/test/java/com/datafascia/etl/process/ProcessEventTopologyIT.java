// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.process;

import backtype.storm.ILocalCluster;
import backtype.storm.Testing;
import backtype.storm.testing.TestJob;
import backtype.storm.tuple.Values;
import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.AuthorizationsSupplier;
import com.datafascia.common.accumulo.ColumnVisibilityPolicy;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.accumulo.FixedAuthorizationsSupplier;
import com.datafascia.common.accumulo.FixedColumnVisibilityPolicy;
import com.datafascia.common.avro.Serializer;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import com.datafascia.common.configuration.guice.ConfigureModule;
import com.datafascia.common.inject.Injectors;
import com.datafascia.common.persist.Id;
import com.datafascia.common.storm.trident.StreamFactory;
import com.datafascia.domain.event.AdmitData;
import com.datafascia.domain.event.EncounterData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.EventType;
import com.datafascia.domain.event.PatientData;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Gender;
import com.datafascia.domain.model.MaritalStatus;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.model.Race;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.domain.persist.Tables;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.neovisionaries.i18n.LanguageCode;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import javax.inject.Singleton;
import org.apache.accumulo.core.client.Connector;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.testing.FeederBatchSpout;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * {@link ProcessEventTopology} integration test
 */
@Test(singleThreaded = true)
public class ProcessEventTopologyIT {

  private static class TestModule extends ConfigureModule {
    @Override
    protected void onConfigure() {
      bind(AuthorizationsSupplier.class).to(FixedAuthorizationsSupplier.class);
      bind(AvroSchemaRegistry.class).to(MemorySchemaRegistry.class).in(Singleton.class);
      bind(ColumnVisibilityPolicy.class).to(FixedColumnVisibilityPolicy.class);
    }

    @Provides
    public ConnectorFactory getConnectorFactory() {
      return ProcessEventTopologyIT.connectorFactory;
    }

    @Provides @Singleton
    public Connector getConnector(ConnectorFactory factory) {
      return factory.getConnector();
    }
  }

  private static final String EVENT_TOPIC = "event";
  private static final String EVENT_SPOUT_ID = "eventSpout";

  private static ConnectorFactory connectorFactory;
  private static Injector injector;
  private static Connector connector;
  private static PatientRepository patientRepository;
  private static EncounterRepository encounterRepository;

  private ProcessEventTopology topology;
  private FeederBatchSpout eventSpout;
  private Serializer serializer;

  @BeforeClass
  public void beforeClass() {
    connectorFactory = new ConnectorFactory(AccumuloConfiguration.builder()
        .instance(ConnectorFactory.MOCK_INSTANCE)
        .zooKeepers("")
        .user("root")
        .password("")
        .build());

    Injectors.overrideWith(new TestModule());
    injector = Injectors.getInjector();

    connector = injector.getInstance(Connector.class);
  }

  @BeforeMethod
  public void beforeMethod() throws Exception {
    connector.tableOperations().create(Tables.PATIENT);

    patientRepository = injector.getInstance(PatientRepository.class);
    encounterRepository = injector.getInstance(EncounterRepository.class);

    topology = new ProcessEventTopology();

    eventSpout = new FeederBatchSpout(Arrays.asList(F.BYTES));
    topology.setEventStreamFactory(
        new StreamFactory() {
          @Override
          public Stream newStream(TridentTopology topology) {
            return topology.newStream(EVENT_SPOUT_ID, eventSpout);
          }
        });

    serializer = new Serializer(injector.getInstance(AvroSchemaRegistry.class));
  }

  @AfterMethod
  public void afterMethod() throws Exception {
    connector.tableOperations().delete(Tables.PATIENT);
  }

  private Event createEvent() {
    PatientData patientData = PatientData.builder()
        .institutionPatientId("institutionPatientId")
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
    EncounterData encounterData = EncounterData.builder()
        .identifier("encounterIdentifier")
        .admitTime(Instant.now())
        .build();
    AdmitData admitData = AdmitData.builder()
        .patient(patientData)
        .encounter(encounterData)
        .build();
    return Event.builder()
        .institutionId(URI.create("urn:df-institution:institution"))
        .facilityId(URI.create("urn:df-facility:facility"))
        .type(EventType.PATIENT_ADMIT)
        .data(admitData)
        .build();
  }

  private void feedEvent(final byte[] bytes) {
    Testing.withLocalCluster(new TestJob() {
      @Override
      public void run(ILocalCluster cluster) throws Exception {
        cluster.submitTopology(
            ProcessEventTopology.class.getSimpleName(),
            topology.configureTopology(),
            topology.buildTopology());

        eventSpout.feed(new Values(Arrays.asList(bytes)));
      }
    });
  }

  private void feedEvent(Event event) {
    feedEvent(serializer.encodeReflect(EVENT_TOPIC, event));
  }

  @Test
  public void should_save_patient_and_encounter() throws Exception {
    Event event = createEvent();
    feedEvent(event);

    AdmitData admitData = (AdmitData) event.getData();
    PatientData patientData = admitData.getPatient();

    Patient dummyPatient = new Patient();
    dummyPatient.setInstitutionPatientId(patientData.getInstitutionPatientId());
    Id<Patient> patientId = PatientRepository.getEntityId(dummyPatient);

    Patient patient = patientRepository.read(patientId).get();
    assertEquals(patient.getInstitutionPatientId(), patientData.getInstitutionPatientId());

    EncounterData encounterData = admitData.getEncounter();
    Id<Encounter> encounterId = patient.getLastEncounterId();
    Optional<Encounter> optionalEncounter = encounterRepository.read(patientId, encounterId);
    assertTrue(optionalEncounter.isPresent());

    Encounter encounter = optionalEncounter.get();
    assertEquals(
        encounter.getHospitalisation().getPeriod().getStartInclusive(),
        encounterData.getAdmitTime());
  }
}
