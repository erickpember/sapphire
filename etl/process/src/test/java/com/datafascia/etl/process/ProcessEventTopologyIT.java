// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.process;

import backtype.storm.ILocalCluster;
import backtype.storm.Testing;
import backtype.storm.testing.TestJob;
import backtype.storm.tuple.Values;
import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.AccumuloTemplate;
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
import com.datafascia.common.persist.entity.AccumuloReflectEntityStore;
import com.datafascia.common.persist.entity.ReflectEntityStore;
import com.datafascia.common.storm.trident.StreamFactory;
import com.datafascia.domain.event.AddObservationsData;
import com.datafascia.domain.event.AdmitPatientData;
import com.datafascia.domain.event.EncounterData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.EventType;
import com.datafascia.domain.event.ObservationData;
import com.datafascia.domain.event.ObservationType;
import com.datafascia.domain.event.PatientData;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Gender;
import com.datafascia.domain.model.MaritalStatus;
import com.datafascia.domain.model.Observation;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.model.Race;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.ObservationRepository;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.domain.persist.Tables;
import com.google.inject.Provides;
import com.neovisionaries.i18n.LanguageCode;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.accumulo.core.client.Connector;
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
      bind(ReflectEntityStore.class).to(AccumuloReflectEntityStore.class);
    }

    @Provides @Singleton
    public Connector connector(ConnectorFactory factory) {
      return factory.getConnector();
    }

    @Provides
    public ConnectorFactory connectorFactory() {
      return new ConnectorFactory(AccumuloConfiguration.builder()
          .instance(ConnectorFactory.MOCK_INSTANCE)
          .zooKeepers("")
          .user("root")
          .password("secret")
          .build());
    }

    @Provides @Singleton
    public AccumuloReflectEntityStore entityStore(
        AvroSchemaRegistry schemaRegistry, AccumuloTemplate accumuloTemplate) {

      return new AccumuloReflectEntityStore(schemaRegistry, Tables.ENTITY_PREFIX, accumuloTemplate);
    }
  }

  private static final String EVENT_TOPIC = "event";
  private static final String EVENT_SPOUT_ID = "eventSpout";

  private static final String NUMERICAL_PAIN_LEVEL_LOW = "numericalPainLevelLow";
  private static final String NUMERICAL_PAIN_LEVEL_HIGH = "numericalPainLevelHigh";
  private static final String NUMERICAL_PAIN_LEVEL_LOW_VALUE = "1";
  private static final String NUMERICAL_PAIN_LEVEL_HIGH_VALUE = "2";

  @Inject
  private Connector connector;

  @Inject
  private AccumuloReflectEntityStore entityStore;

  @Inject
  private PatientRepository patientRepository;

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private ObservationRepository observationRepository;

  @Inject
  private Serializer serializer;

  private ProcessEventTopology topology;
  private FeederBatchSpout eventSpout;

  @BeforeClass
  public void beforeClass() {
    Injectors.overrideWith(new TestModule());
    Injectors.getInjector().injectMembers(this);
  }

  @BeforeMethod
  public void beforeMethod() throws Exception {
    topology = new ProcessEventTopology();

    eventSpout = new FeederBatchSpout(Arrays.asList(F.BYTES));
    topology.setEventStreamFactory(
        new StreamFactory() {
          @Override
          public Stream newStream(TridentTopology topology) {
            return topology.newStream(EVENT_SPOUT_ID, eventSpout);
          }
        });
  }

  private Event createAdmitPatientEvent() {
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
    AdmitPatientData admitPatientData = AdmitPatientData.builder()
        .patient(patientData)
        .encounter(encounterData)
        .build();
    return Event.builder()
        .institutionId(URI.create("urn:df-institution:institution"))
        .facilityId(URI.create("urn:df-facility:facility"))
        .type(EventType.PATIENT_ADMIT)
        .data(admitPatientData)
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

  private static Id<Patient> getPatientId(String institutionPatientId) {
    Patient dummyPatient = new Patient();
    dummyPatient.setInstitutionPatientId(institutionPatientId);
    return PatientRepository.generateId(dummyPatient);
  }

  private Id<Encounter> getLastEncounterId(Id<Patient> patientId) {
    Patient patient = patientRepository.read(patientId).get();
    return patient.getLastEncounterId();
  }

  @Test
  public void should_save_patient_and_encounter() throws Exception {
    Event event = createAdmitPatientEvent();
    feedEvent(event);

    AdmitPatientData admitPatientData = (AdmitPatientData) event.getData();
    PatientData patientData = admitPatientData.getPatient();
    EncounterData encounterData = admitPatientData.getEncounter();

    Id<Patient> patientId = getPatientId(patientData.getInstitutionPatientId());
    Id<Encounter> encounterId = getLastEncounterId(patientId);

    Optional<Encounter> optionalEncounter = encounterRepository.read(patientId, encounterId);
    assertTrue(optionalEncounter.isPresent());

    Encounter encounter = optionalEncounter.get();
    assertEquals(encounter.getPeriod().getStartInclusive(), encounterData.getAdmitTime());
  }

  private Event createAddObservationsEvent(PatientData patient, EncounterData encounter) {
    ObservationData observation1 = ObservationData.builder()
        .id(NUMERICAL_PAIN_LEVEL_LOW)
        .value(Arrays.asList(NUMERICAL_PAIN_LEVEL_LOW_VALUE))
        .observationDateAndTime(Instant.now())
        .observationType(ObservationType.A01)
        .resultStatus("resultStatus")
        .valueType("valueType")
        .build();

    ObservationData observation2 = ObservationData.builder()
        .id(NUMERICAL_PAIN_LEVEL_HIGH)
        .value(Arrays.asList(NUMERICAL_PAIN_LEVEL_HIGH_VALUE))
        .observationDateAndTime(Instant.now())
        .observationType(ObservationType.A01)
        .resultStatus("resultStatus")
        .valueType("valueType")
        .build();

    AddObservationsData addObservationsData = AddObservationsData.builder()
        .institutionPatientId(patient.getInstitutionPatientId())
        .encounterIdentifier(encounter.getIdentifier())
        .observations(Arrays.asList(observation1, observation2))
        .build();

    return Event.builder()
        .institutionId(URI.create("urn:df-institution:institution"))
        .facilityId(URI.create("urn:df-facility:facility"))
        .type(EventType.OBSERVATIONS_ADD)
        .data(addObservationsData)
        .build();
  }

  @Test
  public void should_save_observations() throws Exception {
    Event admitPatientEvent = createAdmitPatientEvent();
    feedEvent(admitPatientEvent);

    AdmitPatientData admitPatientData = (AdmitPatientData) admitPatientEvent.getData();
    Event addObservationsEvent = createAddObservationsEvent(
        admitPatientData.getPatient(), admitPatientData.getEncounter());
    feedEvent(addObservationsEvent);

    AddObservationsData addObservationsData = (AddObservationsData) addObservationsEvent.getData();

    Id<Patient> patientId = getPatientId(addObservationsData.getInstitutionPatientId());
    Id<Encounter> encounterId = getLastEncounterId(patientId);

    List<Observation> observations = observationRepository.list(patientId, encounterId);
    assertEquals(observations.size(), 2);

    for (Observation observation : observations) {
      switch (observation.getName().getCodings().get(0)) {
        case NUMERICAL_PAIN_LEVEL_LOW:
          assertEquals(observation.getValue().getString(), NUMERICAL_PAIN_LEVEL_LOW_VALUE);
          break;
        case NUMERICAL_PAIN_LEVEL_HIGH:
          assertEquals(observation.getValue().getString(), NUMERICAL_PAIN_LEVEL_HIGH_VALUE);
          break;
      }
    }
  }
}
