// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Substance;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.DecimalDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.configuration.guice.ConfigureModule;
import com.datafascia.common.inject.Injectors;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.Languages;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.dropwizard.testing.DropwizardTestApp;
import com.datafascia.etl.inject.ComponentsModule;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.util.Modules;
import com.neovisionaries.i18n.LanguageCode;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IIdType;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

/**
 * Common implementation for integration tests that call the API server.
 */
@Slf4j
public abstract class ApiTestSupport {

  protected static final String API_ENDPOINT_URL = "http://localhost:9090/fhir";
  protected static final String FHIR_USERNAME = "testuser";
  protected static final String FHIR_PASSWORD = "supersecret";
  private static final ZoneId ZONE_ID = ZoneId.of("America/Los_Angeles");

  private static class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(Clock.class)
          .toInstance(Clock.fixed(Instant.parse("2014-09-29T23:48:59Z"), ZONE_ID));
    }

    @Provides
    @Singleton
    public ConnectorFactory connectorFactory() {
      return new ConnectorFactory(TestAccumuloInstance.getConfiguration());
    }
  }

  private static final DropwizardTestApp<APIConfiguration> app = new DropwizardTestApp<>(
      APIService.class, getConfigurationFileName());

  protected static IGenericClient client;

  @BeforeSuite
  public void beforeSuite() throws Exception {
    TimeZone.setDefault(TimeZone.getTimeZone(ZONE_ID));

    Injector injector = Guice.createInjector(
        Modules.override(new ConfigureModule(), new ComponentsModule())
        .with(new TestModule()));
    Injectors.setInjector(injector);
    injector.injectMembers(this);

    app.start();
    log.info("Started Dropwizard application listening on port {}", app.getLocalPort());

    FhirContext fhirContext = injector.getInstance(FhirContext.class);
    client = fhirContext.newRestfulGenericClient(API_ENDPOINT_URL);
    client.registerInterceptor(new BasicAuthInterceptor(FHIR_USERNAME, FHIR_PASSWORD));

    addStaticData();
  }

  @AfterSuite
  public void afterSuite() throws Exception {
    app.stop();
  }

  @BeforeClass
  public void beforeApiTestSupport() throws Exception {
    Injectors.getInjector().injectMembers(this);
  }

  private static String getConfigurationFileName() {
    String zooKeepers = TestAccumuloInstance.getZooKeepers();
    System.setProperty("dw.accumulo.zooKeepers", zooKeepers);
    System.setProperty("dw.kafkaConfig.zookeeperConnect", zooKeepers);

    return Resources.getResource("api-server.yml").getFile();
  }

  /**
   * Loads static data for the API tests
   */
  private void addStaticData() {
    List<UnitedStatesPatient> patients = addPatients();
    List<Substance> substances = addSubstances();
    List<Location> locations = addLocations();
    List<Encounter> encounters = addEncounters(patients, locations);
    Medication medication = addMedication();

    List<Observation> observations = addObservations(patients, encounters);
  }

  private List<Substance> addSubstances() {
    Substance substance1 = new Substance();
    substance1.addIdentifier()
        .setSystem(CodingSystems.MEDICATION_INGREDIENT)
        .setValue("aaaaaaaaae");
    MethodOutcome outcome = client.create().resource(substance1)
        .encodedJson().execute();
    IIdType id = outcome.getId();
    substance1.setId(id);
    log.info("substance 1 ID: {}", id.getValue());

    Substance substance2 = new Substance();
    substance2.addIdentifier()
        .setSystem(CodingSystems.MEDICATION_INGREDIENT)
        .setValue("aaaaaaaaaf");
    MethodOutcome outcome2 = client.create().resource(substance2)
        .encodedJson().execute();
    IIdType id2 = outcome2.getId();
    substance2.setId(id);
    log.info("substance 2 ID: {}", id2.getValue());

    Substance substance3 = new Substance();
    substance3.addIdentifier()
        .setSystem(CodingSystems.MEDICATION_INGREDIENT)
        .setValue("aaaaaaaab0");
    MethodOutcome outcome3 = client.create().resource(substance3)
        .encodedJson().execute();
    IIdType id3 = outcome3.getId();
    substance3.setId(id);
    log.info("substance 3 ID: {}", id3.getValue());

    return Arrays.asList(substance1, substance2, substance3);
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
    IIdType id = outcome.getId();
    patient1.setId(id);
    log.info("patient 1 ID: {}", id.getValue());

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
    patient2.setId(id);
    log.info("patient 2 ID: {}", id.getValue());

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
        .setGender(AdministrativeGenderEnum.MALE)
        .setBirthDate(new DateDt("1954-10-29"))
        .setActive(true);
    outcome = client.create().resource(patient3)
        .encodedJson().execute();
    id = outcome.getId();
    patient3.setId(id);
    log.info("patient 3 ID: {}", id.getValue());

    return Arrays.asList(patient1, patient2, patient3);
  }

  private List<Encounter> addEncounters(List<UnitedStatesPatient> patients,
      List<Location> locations) {
    PeriodDt period = new PeriodDt();
    period.setStart(new Date(), TemporalPrecisionEnum.DAY);

    Encounter encounter1 = new Encounter();
    encounter1.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("encounter1");
    encounter1.setPeriod(period);
    encounter1.setStatus(EncounterStateEnum.IN_PROGRESS);
    encounter1.setPatient(new ResourceReferenceDt(patients.get(0)));
    encounter1.addLocation().setLocation(new ResourceReferenceDt(locations.get(0)));
    MethodOutcome outcome = client.create().resource(encounter1)
        .encodedJson().execute();
    IIdType id = outcome.getId();
    encounter1.setId(id);
    log.info("encounter 1 ID: {}", id.getValue());

    Encounter encounter2 = new Encounter();
    encounter2.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("encounter2");
    encounter2.setPeriod(period);
    encounter2.setStatus(EncounterStateEnum.ARRIVED);
    encounter2.setPatient(new ResourceReferenceDt(patients.get(1)));
    encounter2.addLocation().setLocation(new ResourceReferenceDt(locations.get(1)));
    outcome = client.create().resource(encounter2)
        .encodedJson().execute();
    id = outcome.getId();
    encounter2.setId(id);
    log.info("encounter 2 ID: {}", id.getValue());

    Encounter encounter3 = new Encounter();
    encounter3.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("encounter3");
    encounter3.setPeriod(period);
    encounter3.setStatus(EncounterStateEnum.IN_PROGRESS);
    encounter3.setPatient(new ResourceReferenceDt(patients.get(2)));
    encounter3.addLocation().setLocation(new ResourceReferenceDt(locations.get(2)));
    outcome = client.create().resource(encounter3)
        .encodedJson().execute();
    id = outcome.getId();
    encounter3.setId(id);
    log.info("encounter 3 ID: {}", id.getValue());

    return Arrays.asList(encounter1, encounter2, encounter3);
  }

  private List<Observation> addObservations(
      List<UnitedStatesPatient> patients, List<Encounter> encounters) {

    Observation observation1 = new Observation();
    observation1.setId(new IdDt(Observation.class.getSimpleName(), "observation1"));
    QuantityDt observationValue = new QuantityDt()
        .setValue(new DecimalDt("170"))
        .setUnit("cm");
    observation1.setCode(new CodeableConceptDt("system", "304894102"));
    observation1.setValue(observationValue);
    observation1.setSubject(new ResourceReferenceDt(patients.get(0)));
    observation1.setEncounter(new ResourceReferenceDt(encounters.get(0)));
    client.update().resource(observation1).encodedJson().execute();

    Observation observation2 = new Observation();
    observation2.setId(new IdDt(Observation.class.getSimpleName(), "observation2"));
    observationValue = new QuantityDt()
        .setValue(new DecimalDt("70"))
        .setUnit("kg");
    observation2.setCode(new CodeableConceptDt("system", "WT"));
    observation2.setValue(observationValue);
    observation2.setSubject(new ResourceReferenceDt(patients.get(0)));
    observation2.setEncounter(new ResourceReferenceDt(encounters.get(0)));
    client.update().resource(observation2).encodedJson().execute();

    Observation observation3 = new Observation();
    observation3.setId(new IdDt(Observation.class.getSimpleName(), "observation3"));
    observationValue = new QuantityDt()
        .setValue(new DecimalDt("72"))
        .setUnit("in");
    observation3.setCode(new CodeableConceptDt("system", "304894102"));
    observation3.setValue(observationValue);
    observation3.setSubject(new ResourceReferenceDt(patients.get(1)));
    observation3.setEncounter(new ResourceReferenceDt(encounters.get(1)));
    client.update().resource(observation3).encodedJson().execute();

    Observation observation4 = new Observation();
    observation4.setId(new IdDt(Observation.class.getSimpleName(), "observation4"));
    observationValue = new QuantityDt()
        .setValue(new DecimalDt("50"))
        .setUnit("kg");
    observation4.setCode(new CodeableConceptDt("system", "WT"));
    observation4.setValue(observationValue);
    observation4.setSubject(new ResourceReferenceDt(patients.get(1)));
    observation4.setEncounter(new ResourceReferenceDt(encounters.get(1)));
    client.update().resource(observation4).encodedJson().execute();

    Observation observation5 = new Observation();
    observation5.setId(new IdDt(Observation.class.getSimpleName(), "observation5"));
    observationValue = new QuantityDt()
        .setValue(new DecimalDt("71"))
        .setUnit("in");
    observation5.setCode(new CodeableConceptDt("system", "304894102"));
    observation5.setValue(observationValue);
    observation5.setSubject(new ResourceReferenceDt(patients.get(2)));
    observation5.setEncounter(new ResourceReferenceDt(encounters.get(2)));
    client.update().resource(observation5).encodedJson().execute();

    Observation observation6 = new Observation();
    observation6.setId(new IdDt(Observation.class.getSimpleName(), "observation6"));
    observationValue = new QuantityDt()
        .setValue(new DecimalDt("50"))
        .setUnit("kg");
    observation6.setCode(new CodeableConceptDt("system", "WT"));
    observation6.setValue(observationValue);
    observation6.setSubject(new ResourceReferenceDt(patients.get(2)));
    observation6.setEncounter(new ResourceReferenceDt(encounters.get(2)));
    client.update().resource(observation6).encodedJson().execute();

    DateTimeDt myApplies = new DateTimeDt();
    StringDt myValue = new StringDt("+");
    Observation observation7 = new Observation();
    observation7.setId(new IdDt(Observation.class.getSimpleName(), "observation7"));
    observation7.setCode(new CodeableConceptDt("system", "304890023"));
    observation7.setValue(myValue);
    observation7.setEffective(myApplies.withCurrentTime());
    observation7.setSubject(new ResourceReferenceDt(patients.get(2)));
    observation7.setEncounter(new ResourceReferenceDt(encounters.get(2)));
    client.update().resource(observation7).encodedJson().execute();

    DateTimeDt myApplies8 = new DateTimeDt();
    StringDt myValue8 = new StringDt("UTA (RASS -4 or -5)");
    Observation observation8 = new Observation();
    observation8.setId(new IdDt(Observation.class.getSimpleName(), "observation8"));
    observation8.setCode(new CodeableConceptDt("system", "304890023"));
    observation8.setValue(myValue8);
    observation8.setEffective(myApplies8.withCurrentTime());
    observation8.setSubject(new ResourceReferenceDt(patients.get(2)));
    observation8.setEncounter(new ResourceReferenceDt(encounters.get(2)));
    client.update().resource(observation8).encodedJson().execute();

    return Arrays.asList(
        observation1, observation2, observation3, observation4, observation5, observation6,
        observation7);
  }

  private Location createLocation(String identifier) {
    Location location = new Location()
        .setName(identifier);
    location.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_LOCATION).setValue(identifier);
    return location;
  }

  private List<Location> addLocations() {
    Location location1 = createLocation("13I^Room-1^Bed-A");
    MethodOutcome outcome = client.create().resource(location1).execute();
    IIdType id = outcome.getId();
    location1.setId(id);
    log.info("location 1 ID: {}", id.getValue());

    Location location2 = createLocation("13I^Room-2^Bed-B");
    outcome = client.create().resource(location2).execute();
    id = outcome.getId();
    location2.setId(id);
    log.info("location 2 ID: {}", id.getValue());

    Location location3 = createLocation("13I^Room-3^Bed-A");
    outcome = client.create().resource(location3).execute();
    id = outcome.getId();
    location3.setId(id);
    log.info("location 3 ID: {}", id.getValue());

    return Arrays.asList(location1, location2, location3);
  }

  private Medication createMedication() {
    Medication medication = new Medication();
    medication.setCode(new CodeableConceptDt("code", "code").setText("name"));
    medication.setIsBrand(true);
    medication.setManufacturer(new ResourceReferenceDt("manufacturerId"));

    Medication.Product product = new Medication.Product();
    product.setForm(new CodeableConceptDt("formCode", "formCode"));
    medication.setProduct(product);

    Medication.Package aPackage = new Medication.Package();
    aPackage.setContainer(new CodeableConceptDt("containerCode", "containerCode"));
    medication.setPackage(aPackage);
    return medication;
  }

  private Medication addMedication() {
    Medication medication = createMedication();
    MethodOutcome outcome = client.create().resource(medication).execute();
    IIdType id = outcome.getId();
    medication.setId(id);
    log.info("medication ID: {}", id.getValue());

    return medication;
  }
}
