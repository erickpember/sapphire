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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * {@link AccumuloReflectEntityStore} test
 */
public class AccumuloReflectEntityStoreTest {

  private static final String TABLE_NAME_PREFIX = "Test";

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

  private void scan() {
    Scanner scanner = accumuloTemplate.createScanner(TABLE_NAME_PREFIX + "Data");
    try {
      for (Map.Entry<Key, Value> entry : scanner) {
        System.out.format(
            "%s %s %s %s\n",
            entry.getKey().getRow(),
            entry.getKey().getColumnFamily(),
            entry.getKey().getColumnQualifier(),
            entry.getValue());
      }
    } finally {
      scanner.close();
    }
  }

  @Test
  public void should_save_entity() {
    Patient patient = createPatient();

    Id<Patient> patientId = Id.of("patientId1");
    EntityId entityId = new EntityId(Patient.class, patientId);

    entityStore.save(entityId, patient);

    scan();
  }
}
