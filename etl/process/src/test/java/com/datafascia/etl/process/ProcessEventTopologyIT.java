// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.process;

import backtype.storm.ILocalCluster;
import backtype.storm.Testing;
import backtype.storm.testing.TestJob;
import backtype.storm.tuple.Values;
import com.datafascia.accumulo.AccumuloConfiguration;
import com.datafascia.accumulo.AuthorizationsProvider;
import com.datafascia.accumulo.ConnectorFactory;
import com.datafascia.accumulo.FixedAuthorizationsProvider;
import com.datafascia.common.avro.Serializer;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import com.datafascia.common.configuration.guice.ConfigureModule;
import com.datafascia.common.inject.Injectors;
import com.datafascia.common.persist.Id;
import com.datafascia.common.storm.trident.StreamFactory;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.EventType;
import com.datafascia.domain.event.PatientData;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.domain.persist.Tables;
import com.datafascia.models.Gender;
import com.datafascia.models.MaritalStatus;
import com.datafascia.models.Patient;
import com.datafascia.models.Race;
import com.datafascia.urn.URNFactory;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.neovisionaries.i18n.LanguageCode;
import java.net.URI;
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
      bind(AuthorizationsProvider.class).to(FixedAuthorizationsProvider.class);
      bind(AvroSchemaRegistry.class).to(MemorySchemaRegistry.class).in(Singleton.class);
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
    return Event.builder()
        .institutionId(URI.create("urn:df-institution:institution"))
        .facilityId(URI.create("urn:df-facility:facility"))
        .type(EventType.ADMIT_PATIENT)
        .data(patientData)
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
  public void should_save_patient() throws Exception {
    Event event = createEvent();
    feedEvent(event);

    PatientData patientData = (PatientData) event.getData();
    URI primaryKey = URNFactory.institutionPatientId(
        event.getInstitutionId().toString(),
        event.getFacilityId().toString(),
        patientData.getPatientId());
    Id<Patient> id = Id.of(primaryKey.toString());
    Optional<Patient> optionalPatient = patientRepository.read(id);
    assertTrue(optionalPatient.isPresent());

    Patient patient = optionalPatient.get();
    assertEquals(patient.getPatientId(), patientData.getPatientId());
  }
}
