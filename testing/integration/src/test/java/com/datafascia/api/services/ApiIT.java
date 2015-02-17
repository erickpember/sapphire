// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.datafascia.accumulo.AccumuloConfiguration;
import com.datafascia.accumulo.AccumuloImport;
import com.datafascia.accumulo.AccumuloModule;
import com.datafascia.api.client.DatafasciaApi;
import com.datafascia.api.client.DatafasciaApiBuilder;
import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.common.shiro.FakeRealm;
import com.datafascia.common.shiro.RoleExposingRealm;
import com.datafascia.dropwizard.testing.DropwizardTestApp;
import com.datafascia.models.Encounter;
import com.datafascia.models.Observation;
import com.datafascia.models.Patient;
import com.datafascia.models.Version;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
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
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Integration test for the various API resources
 */
@Slf4j
public class ApiIT {
  private static final String MODELS_PKG = "com.datafascia.models";
  private static final String OPAL_TABLE = "opal_dF_data";
  private static final AccumuloConfiguration config
      = new AccumuloConfiguration(System.getProperty("accumuloConfig"));
  private static final SimpleDateFormat dateFormat
      = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  private static DropwizardTestApp<APIConfiguration> app;
  private static Connector connect;
  private static DatafasciaApi api;
  private static ThreadState threadState;

  /**
   * Prepare the integration test to run.
   *
   * @throws Exception
   */
  @BeforeSuite
  public static void before() throws Exception {
    prepAccumulo();

    // Copy test.yml to /tmp/test.yml and update the port number.
    String ymlPath = Resources.getResource("test.yml").getPath();
    String tmpYml = updatePort(ymlPath, connect.getInstance().getZooKeepers());

    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

    app = new DropwizardTestApp<>(APIService.class, tmpYml);
    log.info("Starting dropwizard app");
    app.start();

    api = DatafasciaApiBuilder.endpoint(new URI("http://localhost:" + app.getLocalPort()),
        "testuser", "supersecret");
  }

  /**
   * Get an accumulo connection and stage it with data.
   *
   * @throws Exception
   */
  public static void prepAccumulo() throws Exception {
    // Delay to allow time for Accumulo mini-cluster to start.
    TimeUnit.SECONDS.sleep(3);

    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(AccumuloConfiguration.class).toInstance(config);
        bind(RoleExposingRealm.class).to(FakeRealm.class);
      }}, new AccumuloModule());

    connect = injector.getInstance(Connector.class);

    // Find the accumulo data and populate it into our minicluster.
    File failDir = Files.createTempDir();
    String resourceFile = Resources.getResource("version.json").getPath();
    String path = resourceFile.substring(0, resourceFile.lastIndexOf(File.separator));
    AccumuloImport.importData(connect, OPAL_TABLE, path + "/accumulo_data", failDir.getPath());
    failDir.delete();

    Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
    RealmSecurityManager securityManager = (RealmSecurityManager) factory.getInstance();
    securityManager.setRealm(new FakeRealm());
    SecurityUtils.setSecurityManager(securityManager);

    Subject subject = new Subject.Builder()
        .principals(new SimplePrincipalCollection("root", FakeRealm.class.getSimpleName()))
        .buildSubject();
    threadState = new SubjectThreadState(subject);
    threadState.bind();
    log.info("Accumulo populated and ready.");
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
   * Validate that schemas are returned.
   */
  @Test
  public static void testSchemaPull() {
    /* A handful of models to test. Unfortuantely, it seems there's no way to enumerate the
     package. */
    List<String> models = Arrays.asList(new String[]{"Patient", "Encounter", "Observation"});

    List<JsonSchema> schemas = api.schemas();
    assertTrue(schemas.size() > 0);

    for (String model : models) {
      /* Make sure the class exists, so we aren't requesting schemas that we don't know shouldn't
       exist. */
      try {
        Class.forName(MODELS_PKG + "." + model);
      } catch (ClassNotFoundException e) {
        log.error("The model " + model
            + " is referenced in the integration test, but doesn't seem to exist anymore.", e);
        continue;
      }

      /* No asserts. Just making sure the schemas are coming across. */
      api.schema(model);
    }
  }

  /**
   * Validate that the version is set.
   */
  @Test
  public static void testVersion() {
    Version version = api.version(MODELS_PKG);
    assertEquals(version.getId(), 1);
    assertEquals(version.getVendor(), "dataFascia Corporation");
  }

  /**
   * Fetch admitted patients and validate them.
   *
   * @throws Exception
   */
  @Test
  public static void testPatient() throws Exception {
    List<Patient> patients = api.patients(true);
    log.info("Found " + patients.size() + " patients");
    for (Patient pat : patients) {

      String id = pat.getId().toString();

      switch (id) {
        case "urn:df-patientId-1:96087004":
          validatePatient(pat,
              "ECMNOTES",
              null,
              "TEST",
              dateFormat.parse("1977-01-01T05:00:00Z"),
              "urn:df-patientId-1:96087004",
              "urn:df-institution-patientId-1:UCSF::96087004");
          break;
        case "urn:df-patientId-1:96087039":
          validatePatient(pat,
              "ONE",
              "A",
              "ECM-MSSGE",
              dateFormat.parse("1960-06-06T04:00:00Z"),
              "urn:df-patientId-1:96087039",
              "urn:df-institution-patientId-1:UCSF::96087039");
          break;
        case "urn:df-patientId-1:96087047":
          validatePatient(pat,
              "ONE",
              "B",
              "ECM-MSSGE",
              dateFormat.parse("1954-10-29T05:00:00Z"),
              "urn:df-patientId-1:96087047",
              "urn:df-institution-patientId-1:UCSF:SICU:96087047");
          break;
        case "urn:df-patientId-1:96087055":
          validatePatient(pat,
              "ONE",
              "C",
              "ECM-MSSGE",
              dateFormat.parse("1996-07-29T04:00:00Z"),
              "urn:df-patientId-1:96087055",
              "urn:df-institution-patientId-1:UCSF::96087055");
          break;
        case "urn:df-patientId-1:96087063":
          validatePatient(pat,
              "ONE",
              "D",
              "ECM-MSSGE",
              dateFormat.parse("1977-10-29T04:00:00Z"),
              "urn:df-patientId-1:96087063",
              "urn:df-institution-patientId-1:UCSF::96087063");
          break;
        case "urn:df-patientId-1:97534012":
          validatePatient(pat,
              "ONEFIVE",
              "C",
              "MB-CHILD",
              dateFormat.parse("1999-02-20T05:00:00Z"),
              "urn:df-patientId-1:97534012",
              "urn:df-institution-patientId-1:UCSF:SICU:97534012");
          break;
      }
    }
  }

  /**
   * Validates a patient object against various expected values.
   *
   * @param patient
   * @param firstName
   * @param middleName
   * @param lastName
   * @param birthDate
   * @param patId
   * @param instId
   */
  public static void validatePatient(Patient patient, String firstName, String middleName,
      String lastName, Date birthDate, String patId, String instId) {
    assertEquals(patient.getName().getFirst(), firstName);
    assertEquals(patient.getName().getMiddle(), middleName);
    assertEquals(patient.getName().getLast(), lastName);
    assertEquals(patient.getBirthDate(), birthDate);
    assertEquals(patient.getId().toString(), patId);
    assertEquals(patient.getInstitutionPatientId().toString(), instId);
  }

  /**
   * Fetch a few encounters and validate them.
   *
   * @throws Exception
   */
  @Test
  public static void testEncounter() throws Exception {
    // Test direct encounter query.
    validateEncounter(api.encounter("UCSF |  | 039ae46a-20a1-4bcd-abb9-68e38d4222c0"),
        new BigDecimal("4.3"), "kg", new BigDecimal("20.98"), "in",
        dateFormat.parse("2014-11-19T10:00:00Z"));

    validateEncounter(api.encounter("UCSF |  | 0728eb62-2f16-484f-8628-a320e99c635d"),
        new BigDecimal("72.576"), "kg", new BigDecimal("62.99"), "in",
        dateFormat.parse("2014-11-24T11:39:07Z"));

    // Test getting the last encounter for a patient.
    validateEncounter(api.lastvisit("97540012"),
        new BigDecimal("4.99"), "kg", new BigDecimal("23"), "in",
        dateFormat.parse("2014-08-04T10:51:00Z"));
  }

  /**
   * Validate an encounter against expected values.
   *
   * @param enco
   * @param weight
   * @param weightUnits
   * @param height
   * @param heightUnits
   * @param admitDate
   */
  public static void validateEncounter(Encounter enco, BigDecimal weight, String weightUnits,
      BigDecimal height, String heightUnits, Date admitDate) {
    boolean foundHeight = false;
    boolean foundWeight = false;
    for (Observation ob : enco.getObservations()) {
      switch (ob.getName().getCode()) {
        case "Weight":
          foundWeight = true;
          assertEquals(ob.getValues().getQuantity().getValue(), weight);
          assertEquals(ob.getValues().getQuantity().getUnits(), "kg");
          break;
        case "Height":
          foundHeight = true;
          assertEquals(ob.getValues().getQuantity().getValue(), height);
          assertEquals(ob.getValues().getQuantity().getUnits(), "in");
          break;
      }
    }
    assertTrue(foundWeight);
    assertTrue(foundHeight);

    assertEquals(admitDate, enco.getHospitalisation().getPeriod().getStart());
  }

  /**
   * Update the port number in a temp test.yml file.
   *
   * @param path Path to test.yml
   * @param zookeeper The zookeeper line to use.
   * @throws IOException
   */
  public static String updatePort(String path, String zookeeper) throws IOException {
    // Load the existing test.yml to a string array.
    Scanner sc = new Scanner(new File(path));
    List<String> lines = new ArrayList<>();
    while (sc.hasNextLine()) {
      lines.add(sc.nextLine());
    }
    String[] arr = lines.toArray(new String[0]);

    // Update the zookeepers line.
    for (int i = 0; i < arr.length; i++) {
      String line = arr[i];
      if (line.contains("zooKeepers:")) {
        line = "  zooKeepers: " + zookeeper;
      }
      arr[i] = line;
    }

    // Write to a new temp test.yml
    String tmppath = System.getProperty("java.io.tmpdir") + File.separatorChar + "test.yml";
    FileWriter fw = new FileWriter(tmppath);
    for (int i = 0; i < arr.length; i++) {
      fw.write(arr[i] + "\n");
    }
    fw.close();

    return tmppath;
  }
}
