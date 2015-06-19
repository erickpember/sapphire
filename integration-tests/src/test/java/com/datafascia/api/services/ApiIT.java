// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.datafascia.api.client.DatafasciaApi;
import com.datafascia.api.client.DatafasciaApiBuilder;
import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.model.CodeableConcept;
import com.datafascia.domain.model.Gender;
import com.datafascia.domain.model.HumanName;
import com.datafascia.domain.model.MaritalStatus;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.model.PatientCommunication;
import com.datafascia.domain.model.Race;
import com.datafascia.domain.model.Version;
import com.datafascia.dropwizard.testing.DropwizardTestApp;
import com.google.common.io.Resources;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.minicluster.MiniAccumuloInstance;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * Integration test for the various API resources
 */
@Slf4j
public class ApiIT {
  protected static final String MODELS_PKG = Version.class.getPackage().getName();
  protected static final DateTimeFormatter dateFormat
      = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  public static DropwizardTestApp<APIConfiguration> app;
  protected static DatafasciaApi api;

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

    app = new DropwizardTestApp<>(APIService.class, configFile);
    app.start();
    log.info("Started Dropwizard application listening on port {}", app.getLocalPort());

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
  }

  private String getZooKeepers() {
    String instanceName = "integration-test";
    try {
      Instance instance = new MiniAccumuloInstance(
          instanceName,
          new File("target/accumulo-maven-plugin/" + instanceName));
      return instance.getZooKeepers();
    } catch (FileNotFoundException e) {
      throw new IllegalStateException("Cannot get Accumulo instance", e);
    }
  }

  /**
   * @return the API configuration to use as a file
   */
  private String apiConfiguration() {
    String zooKeepers = getZooKeepers();
    System.setProperty("dw.accumulo.zooKeepers", zooKeepers);
    System.setProperty("dw.kafkaConfig.zookeeperConnect", zooKeepers);

    return Resources.getResource("api-server.yml").getFile();
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
}
