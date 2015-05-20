// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.persist.entity;

import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.AuthorizationsSupplier;
import com.datafascia.common.accumulo.ColumnVisibilityPolicy;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.accumulo.FixedAuthorizationsSupplier;
import com.datafascia.common.accumulo.FixedColumnVisibilityPolicy;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import com.datafascia.common.persist.Id;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.accumulo.core.client.Connector;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link AccumuloReflectEntityStore} test
 */
public class AccumuloReflectEntityStoreTest {

  private static final String TABLE_NAME_PREFIX = "Test";
  private static final EntityId PATIENT_ID = new EntityId(Patient.class, Id.of("patientId1"));

  /**
   * Provides test dependencies
   */
  private static class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(AuthorizationsSupplier.class).to(FixedAuthorizationsSupplier.class);
      bind(AvroSchemaRegistry.class).to(MemorySchemaRegistry.class);
      bind(ColumnVisibilityPolicy.class).to(FixedColumnVisibilityPolicy.class);
    }

    @Provides @Singleton
    public Connector connector(ConnectorFactory factory) {
      return factory.getConnector();
    }

    @Provides @Singleton
    public ConnectorFactory connectorFactory() {
      return new ConnectorFactory(AccumuloConfiguration.builder()
          .instance(ConnectorFactory.MOCK_INSTANCE)
          .zooKeepers("")
          .user("root")
          .password("secret")
          .build());
    }

    @Provides @Singleton
    public ReflectEntityStore entityStore(
        AvroSchemaRegistry schemaRegistry, AccumuloTemplate accumuloTemplate) {

      return new AccumuloReflectEntityStore(schemaRegistry, TABLE_NAME_PREFIX, accumuloTemplate);
    }
  }

  @AllArgsConstructor @Builder @Data @NoArgsConstructor
  private static class HumanName {
    private String givenName;
    private String familyName;
  }

  @AllArgsConstructor @Builder @Data @NoArgsConstructor
  private static class Patient {
    private HumanName humanName;
    private LocalDate birthDate;
    private List<Integer> integers;
  }

  @AllArgsConstructor @Builder @Data @NoArgsConstructor
  private static class Encounter {
    private String identifier;
    private Instant admitTime;
  }

  @Inject
  private AccumuloTemplate accumuloTemplate;

  @Inject
  private ReflectEntityStore entityStore;

  @BeforeClass
  public void beforeClass() throws Exception {
    Injector injector = Guice.createInjector(new TestModule());
    injector.injectMembers(this);
  }

  private static HumanName createHumanName() {
    return HumanName.builder()
        .givenName("givenName1")
        .build();
  }

  private static Patient createPatient() {
    return Patient.builder()
        .humanName(createHumanName())
        .birthDate(LocalDate.parse("1980-12-31"))
        .integers(Arrays.asList(1, 2, 3))
        .build();
  }

  private static Encounter createEncounter(String identifier) {
    return Encounter.builder()
        .identifier(identifier)
        .admitTime(Instant.now())
        .build();
  }

  @Test
  public void should_read_entity() {
    Patient patient = createPatient();
    entityStore.save(PATIENT_ID, patient);

    Optional<Patient> optionalPatient = entityStore.read(PATIENT_ID);
    assertEquals(optionalPatient.get(), patient);
  }

  @Test
  public void should_list_root_entities() {
    Patient patient = createPatient();
    entityStore.save(PATIENT_ID, patient);

    List<Patient> patients = entityStore.stream(Patient.class)
        .collect(Collectors.toList());
    assertEquals(1, patients.size());
    assertEquals(patients.get(0), patient);
  }

  @Test
  public void should_list_child_entities() {
    Patient patient = createPatient();
    entityStore.save(PATIENT_ID, patient);

    Encounter encounter1 = createEncounter("encounterId1");
    EntityId encounterId1 = EntityId.builder()
        .path(PATIENT_ID)
        .path(Encounter.class, Id.of("encounterId1"))
        .build();
    entityStore.save(encounterId1, encounter1);

    Encounter encounter2 = createEncounter("encounterId2");
    EntityId encounterId2 = EntityId.builder()
        .path(PATIENT_ID)
        .path(Encounter.class, Id.of("encounterId2"))
        .build();
    entityStore.save(encounterId2, encounter2);

    List<Encounter> encounters = entityStore.stream(PATIENT_ID, Encounter.class)
        .collect(Collectors.toList());
    assertEquals(2, encounters.size());
    assertEquals(encounters.get(0), encounter1);
    assertEquals(encounters.get(1), encounter2);
  }
}
