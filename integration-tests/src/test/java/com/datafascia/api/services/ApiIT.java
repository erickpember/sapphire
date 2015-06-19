// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.datafascia.api.client.DatafasciaApi;
import com.datafascia.api.client.DatafasciaApiBuilder;
import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.AccumuloImport;
import com.datafascia.common.accumulo.AccumuloModule;
import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.AuthorizationsSupplier;
import com.datafascia.common.accumulo.ColumnVisibilityPolicy;
import com.datafascia.common.accumulo.FixedColumnVisibilityPolicy;
import com.datafascia.common.accumulo.SubjectAuthorizationsSupplier;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import com.datafascia.common.kafka.KafkaConfig;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.AccumuloReflectEntityStore;
import com.datafascia.common.persist.entity.ReflectEntityStore;
import com.datafascia.common.shiro.FakeRealm;
import com.datafascia.common.shiro.RoleExposingRealm;
import com.datafascia.domain.model.CodeableConcept;
import com.datafascia.domain.model.Gender;
import com.datafascia.domain.model.HumanName;
import com.datafascia.domain.model.MaritalStatus;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.model.PatientCommunication;
import com.datafascia.domain.model.Race;
import com.datafascia.domain.model.Version;
import com.datafascia.domain.persist.Tables;
import com.datafascia.dropwizard.testing.DropwizardTestApp;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.minicluster.MiniAccumuloInstance;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.Factory;
import org.apache.shiro.util.ThreadState;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * Integration test for the various API resources
 */
@Slf4j
public class ApiIT {
  protected static final String MODELS_PKG = Version.class.getPackage().getName();
  protected static final String OPAL_TABLE = "opal_dF_data";
  protected static final DateTimeFormatter dateFormat
      = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  protected static AccumuloConfiguration accConfig;
  protected static KafkaConfig kafkaConfig;
  protected static Injector injector;
  public static DropwizardTestApp<APIConfiguration> app;
  protected static Connector connect;
  protected static DatafasciaApi api;
  protected static ThreadState threadState;

  /**
   * Prepare the integration test to run.
   *
   * @throws Exception
   */
  @BeforeSuite
  public void before() throws Exception {
    // Delay to allow time for Accumulo mini-cluster to start.
    TimeUnit.SECONDS.sleep(3);

    String configFile = apiConfiguration();
    setupGuice();
    setupShiro();
    connect = injector.getInstance(Connector.class);

    log.info("Starting dropwizard app");
    app = new DropwizardTestApp<>(APIService.class, configFile);
    app.start();

    api = DatafasciaApiBuilder.endpoint(
        new URI("http://localhost:" + app.getLocalPort()), "testuser", "supersecret");
    addStaticData();
  }

  /**
   * Stop the test application.
   *
   * @throws Exception
   */
  @AfterSuite
  public static void after() throws Exception {
    app.stop();
    threadState.clear();
  }

  /**
   * @return the Accumulo configuration to use
   */
  private AccumuloConfiguration accumuloConfig() {
    if (accConfig == null) {
      // Keep the values here in sync with what is in the pom.xml
      String instanceName = "integration-test";
      try {
        Instance instance = new MiniAccumuloInstance(instanceName,
            new File("target/accumulo-maven-plugin/" + instanceName));
        accConfig = new AccumuloConfiguration();
        accConfig.setInstance(instanceName);
        accConfig.setZooKeepers(instance.getZooKeepers());
        accConfig.setUser("root");
        accConfig.setPassword("secret");
      } catch (Exception e) {
        throw new RuntimeException("Error get Accumulo instance.", e);
      }
    }

    return accConfig;
  }

  /**
   * @return the Kafka configuration to use
   */
  private KafkaConfig kafkaConfig() {
    if (kafkaConfig == null) {
      kafkaConfig = new KafkaConfig();
      kafkaConfig.setZookeeperConnect(accumuloConfig().getZooKeepers());
    }

    return kafkaConfig;
  }

  /**
   * @return the API configuration to use as a file
   *
   * @throws Exception should not happen
   */
  private String apiConfiguration() throws Exception {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    ObjectNode node = mapper.createObjectNode();
    node.putPOJO("accumulo", accumuloConfig());
    node.putPOJO("kafkaConfig", kafkaConfig());
    String value = mapper.writeValueAsString(node);

    String configFile = System.getProperty("java.io.tmpdir") + File.separatorChar + "test.yml";
    FileWriter fw = new FileWriter(configFile);
    fw.write(value, 0, value.length());
    fw.close();

    return configFile;
  }

  /**
   * Set up Shiro
   */
  private void setupShiro() {
    Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
    RealmSecurityManager securityManager = (RealmSecurityManager) factory.getInstance();
    securityManager.setRealm(new FakeRealm());
    SecurityUtils.setSecurityManager(securityManager);

    Subject subject = new Subject.Builder()
        .principals(new SimplePrincipalCollection("root", FakeRealm.class.getSimpleName()))
        .buildSubject();
    threadState = new SubjectThreadState(subject);
    threadState.bind();
  }

  /**
   * Import test data into accumulo
   *
   * @throws Exception from underling Accumulo or IO calls
   */
  private void importData() throws Exception {
    // Find the accumulo data and populate it into our minicluster.
    File failDir = Files.createTempDir();
    String resourceFile = Resources.getResource("version.json").getPath();
    String path = resourceFile.substring(0, resourceFile.lastIndexOf(File.separator));
    AccumuloImport.importData(connect, OPAL_TABLE, path + "/accumulo_data", failDir.getPath());
    failDir.delete();

    connect.tableOperations().create("Patient");
  }

  private void addStaticData() throws Exception {
    api.createPatient(new Patient() {
      {
        setNames(Arrays.asList(new HumanName() {
          {
            setFirstName("ECMNOTES");
            setLastName("TEST");
          }
        }));
        setGender(Gender.FEMALE);
        setMaritalStatus(MaritalStatus.ANNULLED);
        setRace(Race.AMERICAN_INDIAN);
        setActive(true);
        setCommunication(new PatientCommunication() {
          {
            setPreferred(true);
            setLanguage(new CodeableConcept() {
              {
                setCodings(Arrays.asList("EN"));
                setText("EN");
              }
            });
          }
        });
        setBirthDate(LocalDate.parse("1977-01-01T05:00:00Z", dateFormat));
        setId(Id.of("urn:df-patientId-1:96087004"));
        setInstitutionPatientId("urn:df-institution-patientId-1:UCSF::96087004");
        setLastEncounterId(Id.of("UCSF |  | 039ae46a-20a1-4bcd-abb9-68e38d4222c0"));
        setAccountNumber("AccountNumber1");
      }
    }
    );

    api.createPatient(new Patient() {
      {
        setNames(Arrays.asList(new HumanName() {
          {
            setFirstName("ONE");
            setMiddleName("A");
            setLastName("ECM-MSSGE");
          }
        }));
        setGender(Gender.FEMALE);
        setMaritalStatus(MaritalStatus.DIVORCED);
        setRace(Race.ASIAN);
        setActive(true);
        setCommunication(new PatientCommunication() {
          {
            setPreferred(true);
            setLanguage(new CodeableConcept() {
              {
                setCodings(Arrays.asList("EN"));
                setText("EN");
              }
            });
          }
        });
        setBirthDate(LocalDate.parse("1960-06-06T04:00:00Z", dateFormat));
        setId(Id.of("urn:df-patientId-1:96087039"));
        setInstitutionPatientId("urn:df-institution-patientId-1:UCSF::96087039");
        setLastEncounterId(Id.of("UCSF |  | 0728eb62-2f16-484f-8628-a320e99c635d"));
        setAccountNumber("AccountNumber2");
      }
    }
    );

    api.createPatient(new Patient() {
      {
        setNames(Arrays.asList(new HumanName() {
          {
            setFirstName("ONE");
            setMiddleName("B");
            setLastName("ECM-MSSGE");
          }
        }));
        setGender(Gender.FEMALE);
        setMaritalStatus(MaritalStatus.DOMESTIC_PARTNER);
        setRace(Race.BLACK);
        setActive(true);
        setCommunication(new PatientCommunication() {
          {
            setPreferred(true);
            setLanguage(new CodeableConcept() {
              {
                setCodings(Arrays.asList("EN"));
                setText("EN");
              }
            });
          }
        });
        setBirthDate(LocalDate.parse("1954-10-29T05:00:00Z", dateFormat));
        setId(Id.of("urn:df-patientId-1:96087047"));
        setInstitutionPatientId("urn:df-institution-patientId-1:UCSF:SICU:96087047");
        setAccountNumber("AccountNumber3");
      }
    }
    );

    api.createPatient(new Patient() {
      {
        setNames(Arrays.asList(new HumanName() {
          {
            setFirstName("ONE");
            setMiddleName("C");
            setLastName("ECM-MSSGE");
          }
        }));
        setGender(Gender.MALE);
        setMaritalStatus(MaritalStatus.INTERLOCUTORY);
        setRace(Race.OTHER);
        setActive(true);
        setCommunication(new PatientCommunication() {
          {
            setPreferred(true);
            setLanguage(new CodeableConcept() {
              {
                setCodings(Arrays.asList("EN"));
                setText("EN");
              }
            });
          }
        });
        setBirthDate(LocalDate.parse("1996-07-29T04:00:00Z", dateFormat));
        setId(Id.of("urn:df-patientId-1:96087055"));
        setInstitutionPatientId("urn:df-institution-patientId-1:UCSF::96087055");
        setAccountNumber("AccountNumber4");
      }
    }
    );

    api.createPatient(new Patient() {
      {
        setNames(Arrays.asList(new HumanName() {
          {
            setFirstName("ONE");
            setMiddleName("D");
            setLastName("ECM-MSSGE");
          }
        }));
        setGender(Gender.MALE);
        setMaritalStatus(MaritalStatus.NEVER_MARRIED);
        setRace(Race.WHITE);
        setActive(true);
        setCommunication(new PatientCommunication() {
          {
            setPreferred(true);
            setLanguage(new CodeableConcept() {
              {
                setCodings(Arrays.asList("EN"));
                setText("EN");
              }
            });
          }
        });
        setBirthDate(LocalDate.parse("1977-10-29T04:00:00Z", dateFormat));
        setId(Id.of("urn:df-patientId-1:96087063"));
        setInstitutionPatientId("urn:df-institution-patientId-1:UCSF::96087063");
        setAccountNumber("AccountNumber5");
      }
    }
    );

    api.createPatient(new Patient() {
      {
        setNames(Arrays.asList(new HumanName() {
          {
            setFirstName("ONE");
            setMiddleName("C");
            setLastName("MB-CHILD");
          }
        }));
        setGender(Gender.MALE);
        setMaritalStatus(MaritalStatus.UNMARRIED);
        setRace(Race.PACIFIC_ISLANDER);
        setActive(true);
        setCommunication(new PatientCommunication() {
          {
            setPreferred(true);
            setLanguage(new CodeableConcept() {
              {
                setCodings(Arrays.asList("EN"));
                setText("EN");
              }
            });
          }
        });
        setBirthDate(LocalDate.parse("1999-02-20T05:00:00Z", dateFormat));
        setId(Id.of("urn:df-patientId-1:97534012"));
        setInstitutionPatientId("urn:df-institution-patientId-1:UCSF:SICU:97534012");
        setAccountNumber("AccountNumber6");
      }
    }
    );
  }

  /**
   * Set up Guice modules
   */
  private void setupGuice() {
    injector = Guice.createInjector(
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(AccumuloConfiguration.class).toInstance(accumuloConfig());
            bind(AuthorizationsSupplier.class).to(SubjectAuthorizationsSupplier.class);
            bind(AvroSchemaRegistry.class).to(MemorySchemaRegistry.class).in(Singleton.class);
            bind(ColumnVisibilityPolicy.class).to(FixedColumnVisibilityPolicy.class);
            bind(RoleExposingRealm.class).to(FakeRealm.class);
          }

          @Provides @Singleton
          public ReflectEntityStore entityStore(
              AvroSchemaRegistry schemaRegistry, AccumuloTemplate accumuloTemplate) {

            return new AccumuloReflectEntityStore(
                schemaRegistry, Tables.ENTITY_PREFIX, accumuloTemplate);
          }
        },
        new AccumuloModule());
  }

  public static Injector getInjector() {
    return injector;
  }
}
