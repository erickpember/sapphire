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
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.DecimalDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import com.datafascia.common.configuration.guice.ConfigureModule;
import com.datafascia.common.inject.Injectors;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.Languages;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.neovisionaries.i18n.LanguageCode;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IIdType;
import org.testng.annotations.BeforeSuite;

/**
 * Integration test for the various API resources
 */
@Slf4j
public class ApiIT {

  protected static final String API_ENDPOINT_URL = "http://localhost:9090/fhir";
  protected static final String FHIR_USERNAME = "testuser";
  protected static final String FHIR_PASSWORD = "supersecret";

  @Inject
  private FhirContext fhirContext;

  protected static IGenericClient client;

  @BeforeSuite
  public void beforeSuite() throws Exception {
    Injector injector = Guice.createInjector(new ConfigureModule(), new TestModule());
    Injectors.setInjector(injector);
    injector.injectMembers(this);

    client = fhirContext.newRestfulGenericClient(API_ENDPOINT_URL);

    addStaticData();
  }

  /**
   * Loads static data for the API tests
   */
  private void addStaticData() {
    List<UnitedStatesPatient> patients = addPatients();
    List<Location> locations = addLocations();
    List<Encounter> encounters = addEncounters(patients, locations);
    Medication medication = addMedication();

    List<Observation> observations = addObservations(patients, encounters);
    List<MedicationOrder> medicationOrders = addMedicationOrders(patients, encounters);
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

  private List<Observation> addObservations(List<UnitedStatesPatient> patients,
      List<Encounter> encounters) {
    Observation observation1 = new Observation();
    QuantityDt observationValue = new QuantityDt()
        .setValue(new DecimalDt("170"))
        .setUnit("cm");
    observation1.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("observation1");
    observation1.setCode(new CodeableConceptDt("system", "304894102"));
    observation1.setValue(observationValue);
    observation1.setSubject(new ResourceReferenceDt(patients.get(0)));
    observation1.setEncounter(new ResourceReferenceDt(encounters.get(0)));
    MethodOutcome outcome = client.create().resource(observation1)
        .encodedJson().execute();
    IIdType id = outcome.getId();
    observation1.setId(id);
    log.info("observation 1 ID: {}", id.getValue());

    Observation observation2 = new Observation();
    observationValue = new QuantityDt()
        .setValue(new DecimalDt("70"))
        .setUnit("kg");
    observation2.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("observation2");
    observation2.setCode(new CodeableConceptDt("system", "WT"));
    observation2.setValue(observationValue);
    observation2.setSubject(new ResourceReferenceDt(patients.get(0)));
    observation2.setEncounter(new ResourceReferenceDt(encounters.get(0)));
    outcome = client.create().resource(observation2)
        .encodedJson().execute();
    id = outcome.getId();
    observation2.setId(id);
    log.info("observation 2 ID: {}", id.getValue());

    Observation observation3 = new Observation();
    observation3.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("observation3");
    observationValue = new QuantityDt()
        .setValue(new DecimalDt("72"))
        .setUnit("in");
    observation3.setCode(new CodeableConceptDt("system", "304894102"));
    observation3.setValue(observationValue);
    observation3.setSubject(new ResourceReferenceDt(patients.get(1)));
    observation3.setEncounter(new ResourceReferenceDt(encounters.get(1)));
    outcome = client.create().resource(observation3)
        .encodedJson().execute();
    id = outcome.getId();
    observation3.setId(id);
    log.info("observation 3 ID: {}", id.getValue());

    Observation observation4 = new Observation();
    observationValue = new QuantityDt()
        .setValue(new DecimalDt("50"))
        .setUnit("kg");
    observation4.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("observation4");
    observation4.setCode(new CodeableConceptDt("system", "WT"));
    observation4.setValue(observationValue);
    observation4.setSubject(new ResourceReferenceDt(patients.get(1)));
    observation4.setEncounter(new ResourceReferenceDt(encounters.get(1)));
    outcome = client.create().resource(observation4)
        .encodedJson().execute();
    id = outcome.getId();
    observation4.setId(id);
    log.info("observation 4 ID: {}", id.getValue());

    Observation observation5 = new Observation();
    observation5.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("observation5");
    observationValue = new QuantityDt()
        .setValue(new DecimalDt("71"))
        .setUnit("in");
    observation5.setCode(new CodeableConceptDt("system", "304894102"));
    observation5.setValue(observationValue);
    observation5.setSubject(new ResourceReferenceDt(patients.get(2)));
    observation5.setEncounter(new ResourceReferenceDt(encounters.get(2)));
    outcome = client.create().resource(observation5)
        .encodedJson().execute();
    id = outcome.getId();
    observation5.setId(id);
    log.info("observation 5 ID: {}", id.getValue());

    Observation observation6 = new Observation();
    observationValue = new QuantityDt()
        .setValue(new DecimalDt("50"))
        .setUnit("kg");
    observation6.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("observation4");
    observation6.setCode(new CodeableConceptDt("system", "WT"));
    observation6.setValue(observationValue);
    observation6.setSubject(new ResourceReferenceDt(patients.get(2)));
    observation6.setEncounter(new ResourceReferenceDt(encounters.get(2)));
    outcome = client.create().resource(observation6)
        .encodedJson().execute();
    id = outcome.getId();
    observation6.setId(id);
    log.info("observation 6 ID: {}", id.getValue());

    DateTimeDt myApplies = new DateTimeDt();
    StringDt myValue = new StringDt("+");
    Observation observation7 = new Observation();
    observation7.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("observation4");
    observation7.setCode(new CodeableConceptDt("system", "304890023"));
    observation7.setValue(myValue);
    observation7.setEffective(myApplies.withCurrentTime());
    observation7.setSubject(new ResourceReferenceDt(patients.get(2)));
    observation7.setEncounter(new ResourceReferenceDt(encounters.get(2)));
    outcome = client.create().resource(observation7)
        .encodedJson().execute();
    id = outcome.getId();
    observation7.setId(id);
    log.info("observation 7 ID: {}", id.getValue());

    DateTimeDt myApplies8 = new DateTimeDt();
    StringDt myValue8 = new StringDt("UTA (RASS -4 or -5)");
    Observation observation8 = new Observation();
    observation7.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("observation4");
    observation8.setCode(new CodeableConceptDt("system", "304890023"));
    observation8.setValue(myValue8);
    observation8.setEffective(myApplies8.withCurrentTime());
    observation8.setSubject(new ResourceReferenceDt(patients.get(2)));
    observation8.setEncounter(new ResourceReferenceDt(encounters.get(2)));
    outcome = client.create().resource(observation8)
        .encodedJson().execute();
    id = outcome.getId();
    observation8.setId(id);
    log.info("observation 8 ID: {}", id.getValue());

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

  private MedicationOrder createPrescription(String identifier,
      List<UnitedStatesPatient> patients, List<Encounter> encounters) {
    MedicationOrder rx = new MedicationOrder();
    rx.addIdentifier().setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue(identifier);
    rx.setStatus(MedicationOrderStatusEnum.ACTIVE);
    rx.setEncounter(new ResourceReferenceDt(encounters.get(2)));
    rx.setPatient(new ResourceReferenceDt(patients.get(2)));

    return rx;
  }

  private List<MedicationOrder> addMedicationOrders(
      List<UnitedStatesPatient> patients,
      List<Encounter> encounters) {
    MedicationOrder rx1 = createPrescription("Continuous Infusion Lorazepam IV",
        patients, encounters);
    MethodOutcome outcome = client.create().resource(rx1).execute();
    IIdType id = outcome.getId();
    rx1.setId(id);

    MedicationOrder rx2 = createPrescription("Continuous Infusion Midazolam IV",
        patients, encounters);
    outcome = client.create().resource(rx2).execute();
    id = outcome.getId();
    rx2.setId(id);

    MedicationOrder rx3 = createPrescription("Intermittent Chlordiazepoxide Enteral",
        patients, encounters);
    outcome = client.create().resource(rx3).execute();
    id = outcome.getId();
    rx3.setId(id);

    MedicationOrder rx4 = createPrescription("Intermittent Lorazepam IV",
        patients, encounters);
    outcome = client.create().resource(rx4).execute();
    id = outcome.getId();
    rx4.setId(id);

    MedicationOrder rx5 = createPrescription("Intermittent Lorazepam Enteral",
        patients, encounters);
    outcome = client.create().resource(rx5).execute();
    id = outcome.getId();
    rx5.setId(id);

    MedicationOrder rx6 = createPrescription("Intermittent Midazolam IV",
        patients, encounters);
    outcome = client.create().resource(rx6).execute();
    id = outcome.getId();
    rx6.setId(id);

    MedicationOrder rx7 = createPrescription("Intermittent Something or Other IV",
        patients, encounters);
    outcome = client.create().resource(rx7).execute();
    id = outcome.getId();
    rx7.setId(id);

    return Arrays.asList(rx1, rx2, rx3, rx4, rx5, rx6);
  }
}
