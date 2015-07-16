// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.Languages;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.dropwizard.testing.DropwizardTestApp;
import com.google.common.io.Resources;
import com.neovisionaries.i18n.LanguageCode;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
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
  private static final String USERNAME = "testuser";
  private static final String PASSWORD = "supersecret";

  public static final DropwizardTestApp<APIConfiguration> app = new DropwizardTestApp<>(
      APIService.class, apiConfiguration());
  private static final FhirContext ctx = FhirContext.forDstu2();
  protected static IGenericClient client;

  /**
   * Prepare the integration test to run.
   *
   * @throws Exception
   */
  @BeforeSuite
  public void before() throws Exception {
    // Delay to allow time for Accumulo mini-cluster to start.
    TimeUnit.SECONDS.sleep(3);

    app.start();
    log.info("Started Dropwizard application listening on port {}", app.getLocalPort());

    // Register the interceptor with the client
    client = ctx.newRestfulGenericClient("http://localhost:" + app.getLocalPort() + "/fhir");
    client.registerInterceptor(new BasicAuthInterceptor(USERNAME, PASSWORD));

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

  private static String getZooKeepers() {
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
  private static String apiConfiguration() {
    String zooKeepers = getZooKeepers();
    System.setProperty("dw.accumulo.zooKeepers", zooKeepers);
    System.setProperty("dw.kafkaConfig.zookeeperConnect", zooKeepers);

    return Resources.getResource("api-server.yml").getFile();
  }

  private void addStaticData() {
    List<UnitedStatesPatient> patients = addPatients();
    addEncounters(patients);
  }

  private List<UnitedStatesPatient> addPatients() {
    UnitedStatesPatient patient1 = new UnitedStatesPatient();
    patient1.addIdentifier()
            .setSystem(IdentifierSystems.INSTITUTION_PATIENT)
            .setValue("96087004");
    patient1.addIdentifier()
            .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT)
            .setValue("urn:df-patientId-196087004");
    patient1.addName()
        .addGiven("ECMNOTES").addFamily("TEST");
    patient1.addCommunication()
        .setPreferred(true).setLanguage(Languages.createLanguage(LanguageCode.en));
    patient1
        .setRace(RaceEnum.AMERICAN_INDIAN)
        .setMaritalStatus(MaritalStatusCodesEnum.A)
        .setGender(AdministrativeGenderEnum.FEMALE)
        .setBirthDate(new DateDt("1977-01-01"))
        .setActive(true);
    MethodOutcome outcome = client.create().resource(patient1)
        .encodedJson().execute();
    IdDt id = outcome.getId();
    System.out.println("Got ID: " + id.getValue());

    UnitedStatesPatient patient2 = new UnitedStatesPatient();
    patient2.addIdentifier()
            .setSystem(IdentifierSystems.INSTITUTION_PATIENT)
            .setValue("96087039");
    patient2.addIdentifier()
            .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT)
            .setValue("urn:df-patientId-196087039");
    patient2.addName()
        .addGiven("ONE").addGiven("A").addFamily("ECM-MSSGE");
    patient2.addCommunication()
        .setPreferred(true).setLanguage(Languages.createLanguage(LanguageCode.en));
    patient2
        .setRace(RaceEnum.ASIAN)
        .setMaritalStatus(MaritalStatusCodesEnum.D)
        .setGender(AdministrativeGenderEnum.FEMALE)
        .setBirthDate(new DateDt("1960-06-06"))
        .setActive(true);
    outcome = client.create().resource(patient2)
        .encodedJson().execute();
    id = outcome.getId();
    System.out.println("Got ID: " + id.getValue());

    UnitedStatesPatient patient3 = new UnitedStatesPatient();
    patient3.addIdentifier()
            .setSystem(IdentifierSystems.INSTITUTION_PATIENT)
            .setValue("96087047");
    patient3.addIdentifier()
            .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT)
            .setValue("urn:df-patientId-196087047");
    patient3.addName()
        .addGiven("ONE").addGiven("B").addFamily("ECM-MSSGE");
    patient3.addCommunication()
        .setPreferred(true).setLanguage(Languages.createLanguage(LanguageCode.en));
    patient3.setRace(RaceEnum.BLACK)
        .setMaritalStatus(MaritalStatusCodesEnum.D)
        .setGender(AdministrativeGenderEnum.FEMALE)
        .setBirthDate(new DateDt("1954-10-29"))
        .setActive(true);
    outcome = client.create().resource(patient3)
        .encodedJson().execute();
    id = outcome.getId();
    System.out.println("Got ID: " + id.getValue());

    UnitedStatesPatient patient4 = new UnitedStatesPatient();
    patient4.addIdentifier()
            .setSystem(IdentifierSystems.INSTITUTION_PATIENT)
            .setValue("96087055");
    patient4.addIdentifier()
            .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT)
            .setValue("urn:df-patientId-196087055");
    patient4.addName()
        .addGiven("ONE").addGiven("C").addFamily("ECM-MSSGE");
    patient4.addCommunication()
        .setPreferred(true).setLanguage(Languages.createLanguage(LanguageCode.en));
    patient4.setRace(RaceEnum.OTHER)
        .setMaritalStatus(MaritalStatusCodesEnum.I)
        .setGender(AdministrativeGenderEnum.FEMALE)
        .setBirthDate(new DateDt("1996-07-29"))
        .setActive(true);
    outcome = client.create().resource(patient4)
        .encodedJson().execute();
    id = outcome.getId();
    System.out.println("Got ID: " + id.getValue());

    UnitedStatesPatient patient5 = new UnitedStatesPatient();
    patient5.addIdentifier()
            .setSystem(IdentifierSystems.INSTITUTION_PATIENT)
            .setValue("96087063");
    patient5.addIdentifier()
            .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT)
            .setValue("urn:df-patientId-196087063");
    patient5.addName()
        .addGiven("ONE").addGiven("D").addFamily("ECM-MSSGE");
    patient5.addCommunication()
        .setPreferred(true).setLanguage(Languages.createLanguage(LanguageCode.en));
    patient5.setRace(RaceEnum.WHITE)
        .setMaritalStatus(MaritalStatusCodesEnum.UNMARRIED)
        .setGender(AdministrativeGenderEnum.MALE)
        .setBirthDate(new DateDt("1977-10-29"))
        .setActive(true);
    outcome = client.create().resource(patient5)
        .encodedJson().execute();
    id = outcome.getId();
    System.out.println("Got ID: " + id.getValue());

    UnitedStatesPatient patient6 = new UnitedStatesPatient();
    patient6.addIdentifier()
            .setSystem(IdentifierSystems.INSTITUTION_PATIENT)
            .setValue("97534012");
    patient6.addIdentifier()
            .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT)
            .setValue("urn:df-patientId-197534012");
    patient6.addName()
        .addGiven("ONEFIVE").addGiven("C").addFamily("MB-CHILD");
    patient6.addCommunication()
        .setPreferred(true).setLanguage(Languages.createLanguage(LanguageCode.en));
    patient6.setRace(RaceEnum.PACIFIC_ISLANDER)
        .setMaritalStatus(MaritalStatusCodesEnum.UNMARRIED)
        .setGender(AdministrativeGenderEnum.MALE)
        .setBirthDate(new DateDt("1999-02-20"))
        .setActive(true);
    outcome = client.create().resource(patient6)
        .encodedJson().execute();
    id = outcome.getId();
    System.out.println("Got ID: " + id.getValue());

    return (Arrays.asList(patient1, patient2, patient3, patient4, patient5, patient6));
  }

  private void addEncounters(List<UnitedStatesPatient> patients) {
    Encounter encounter1 = new Encounter();
    encounter1.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("encounter1");
    encounter1.setStatus(EncounterStateEnum.IN_PROGRESS)
        .setPatient(new ResourceReferenceDt(patients.get(0)));
    MethodOutcome outcome = client.create().resource(encounter1)
        .encodedJson().execute();
    encounter1.setId(outcome.getId());

    Encounter encounter2 = new Encounter();
    encounter2.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("encounter2");
    encounter2.setStatus(EncounterStateEnum.ARRIVED)
        .setPatient(new ResourceReferenceDt(patients.get(0)));
    outcome = client.create().resource(encounter2)
        .encodedJson().execute();
    encounter2.setId(outcome.getId());

    Encounter encounter3 = new Encounter();
    encounter3.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("encounter3");
    encounter3.setStatus(EncounterStateEnum.IN_PROGRESS)
        .setPatient(new ResourceReferenceDt(patients.get(0)));
    outcome = client.create().resource(encounter3)
        .encodedJson().execute();
    encounter3.setId(outcome.getId());
  }
}
