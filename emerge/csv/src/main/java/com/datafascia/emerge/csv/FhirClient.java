// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.csv;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationPrescription;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides access to the FHIR Server
 */
@Slf4j
public class FhirClient {

  private static FhirContext ctx = FhirContext.forDstu2();
  private static IGenericClient client;

  /**
   * Class constructor
   *
   * @param apiEndpoint
   *     the URL of the FHIR server
   * @param user
   *     the user to be authenticated against the server
   * @param passwd
   *     the user's password
   */
  public FhirClient(String apiEndpoint, String user, String passwd) {
    client = ctx.newRestfulGenericClient(apiEndpoint + "/fhir");
    client.registerInterceptor(new BasicAuthInterceptor(user, passwd));
  }

  /**
   * Get the list of encounters for a given status
   *
   * @param institution
   *     the source institution
   * @return encounters
   *     the list of encounters
   */
  public List<Encounter> getInProgressEncounters(String institution) {
    Bundle bundle = client.search()
        .forResource(Encounter.class)
        .where(Encounter.STATUS.exactly().code(EncounterStateEnum.IN_PROGRESS.name()))
        .execute();

    List<Encounter> encounters = new ArrayList<>();
    boolean done = false;
    while (!done) {
      for (BundleEntry entry : bundle.getEntries()) {
        IResource resource = entry.getResource();
        Encounter encounter = null;
        if (resource instanceof Encounter) {
          encounter = (Encounter) resource;
          Optional<String> patientInstitution = getPatientInstitution(encounter);

          if (patientInstitution.isPresent() && institution.equals(patientInstitution.get())) {
            encounters.add(encounter);
          }
        }
      }
      if (!bundle.getLinkNext().isEmpty()) {
        // Load the next bundle
        bundle = client.loadPage().next(bundle).execute();
      } else {
        done = true;
      }
    }

    return encounters;
  }

  /**
   * Get the list of observations for a given encounter
   *
   * @param encounter
   *     the Encounter status to search
   * @return observations
   *     the list of observations for this encounter
   */
  public List<Observation> getObservations(Encounter encounter) {
    log.info("observations for: {}", encounter.getId().getValue());
    Bundle bundle = client.search().forResource(Observation.class)
        .where(new StringClientParam("encounter")
            .matches()
            .value(encounter.getId().getIdPart()))
        .execute();

    // Add all Observations resources to the list
    List<Observation> observations = bundle.getResources(Observation.class);
    while (bundle.getLinkNext().isEmpty() == false) {
      bundle = client.loadPage().next(bundle).execute();
      observations.addAll(bundle.getResources(Observation.class));
    }

    return observations;
  }

  /**
   * Get the list of medication administration for a given encounter
   *
   * @param encounter
   *     the patient encounter
   * @return medAdmins
   *     the list of medication administrations for this encounter
   */
  public List<MedicationAdministration> getMedicationAdministrations(Encounter encounter) {
    Bundle bundle = client.search().forResource(MedicationAdministration.class)
        .where(new StringClientParam("encounter").matches()
            .value(encounter.getId().getIdPart()))
        .execute();

    // Add all MedicationAdministrations resources to the list
    List<MedicationAdministration> medAdmins =
        bundle.getResources(MedicationAdministration.class);
    while (bundle.getLinkNext().isEmpty() == false) {
      bundle = client.loadPage().next(bundle).execute();
      medAdmins.addAll(bundle.getResources(MedicationAdministration.class));
    }

    return medAdmins;
  }

  /**
   * Get the list of medication prescriptions for a given encounter
   *
   * @param encounter
   *     the patient encounter
   * @return prescriptions
   *     the list of medication prescriptions for this encounter
   */
  public List<MedicationPrescription> getPrescriptions(Encounter encounter) {
    Bundle bundle = client.search().forResource(MedicationPrescription.class)
        .where(new StringClientParam("encounter").matches()
            .value(encounter.getId().getIdPart()))
        .execute();

    // Add all MedicationPrescription resources to the list
    List<MedicationPrescription> prescriptions =
        bundle.getResources(MedicationPrescription.class);
    while (bundle.getLinkNext().isEmpty() == false) {
      bundle = client.loadPage().next(bundle).execute();
      prescriptions.addAll(bundle.getResources(MedicationPrescription.class));
    }

    return prescriptions;
  }

  /**
   * Get patient from encounter
   *
   * @param encounter
   *     the patient encounter
   * @return patient
   *     the patient referenced in the encounter
   */
  public UnitedStatesPatient getPatientFromEncounter(Encounter encounter) {
    IdDt patientId = new IdDt(encounter.getPatient().getReference().getIdPart());
    log.info("patientId: {}", patientId.getValueAsString());
    UnitedStatesPatient patient = client.read().resource(UnitedStatesPatient.class)
        .withId(patientId.getValueAsString()). execute();

    return patient;
  }

  /**
   * Gets the patient's full name
   *
   * @param patient
   *     the patient resource
   * @return patient name
   *     the patient name in the form "firstName middleName lastName"
   */
  public Optional<String> getPatientName(UnitedStatesPatient patient) {
    if (0 == patient.getName().get(0).getGiven().size() &&
        0 == patient.getName().get(0).getFamily().size()) {
      return Optional.empty();
    }

    StringJoiner sj = new StringJoiner(" ");
    for (int i = 0; i < patient.getName().get(0).getGiven().size() && i < 2; i++) {
      sj.add(patient.getName().get(0).getGiven().get(i).getValue());
    }
    for (int i = 0; i < patient.getName().get(0).getFamily().size() && i < 2; i++) {
      sj.add(patient.getName().get(0).getFamily().get(i).getValue());
    }

    return Optional.of(sj.toString());
  }

  /**
   * Get the patient's gender
   *
   * @param patient
   *     the patient resource
   * @return patient gender
   *     the gender of the patient
   */
  public String getPatientGender(UnitedStatesPatient patient) {
    log.info("gender: {}", patient.getGenderElement().getValue());
    return patient.getGenderElement().getValue();
  }

  /**
   * Get the patient's race
   *
   * @param patient
   *     the patient resource
   * @return patient race
   *     the race of the patient
   */
  public RaceEnum getPatientRace(UnitedStatesPatient patient) {
    for (RaceEnum race : patient.getRace().getValueAsEnum()) {
      return race;
    }
    return RaceEnum.UNKNOWN;
  }

  /**
   * Get the patient's date of birth
   *
   * @param patient
   *     the patient resource
   * @return patient birth date
   *     the formatted birth date of the patient
   */
  public String getPatientDateOfBirth(UnitedStatesPatient patient) {
    log.info("date of birth: {}", patient.getBirthDateElement().getValueAsString());
    return patient.getBirthDateElement().getValueAsString();
  }

  /**
   * Get the patient's admission date
   *
   * @param encounter
   *     the encounter resource
   * @return patient's admission datetime
   *     the formatted birth date of the patient
   */
  public String getPatientAdmissionDate(Encounter encounter) {
    log.info("admissiondate: {}", encounter.getPeriod().getStartElement().getValueAsString());
    return encounter.getPeriod().getStartElement().getValueAsString();
  }

  /**
   * Get the patient's institution
   *
   * @param encounter
   *     the encounter resource
   * @return dpartment
   *     the patient's current institution
   */
  public Optional<String> getPatientInstitution(Encounter encounter) {
    String locationId =
        encounter.getLocationFirstRep().getLocation().getReference().getIdPart();

    Location location = client.read()
        .resource(Location.class)
        .withId(locationId)
        .execute();

    String[] locationParts = location.getIdentifierFirstRep().getValue().split("\\^");
    if (locationParts.length > 0) {
      String institution = locationParts[0];
      log.info("patient institution: {}", institution);
      return Optional.of(institution);
    } else {
      return Optional.empty();
    }
  }

  /**
   * Get the patient's room number
   *
   * @param encounter
   *     the encounter resource
   * @return room
   *     the patient's current room number
   */
  public Optional<String> getPatientRoom(Encounter encounter) {
    String locationId =
        encounter.getLocationFirstRep().getLocation().getReference().getIdPart();

    Location location = client.read()
        .resource(Location.class)
        .withId(locationId)
        .execute();

    String[] locationParts = location.getIdentifierFirstRep().getValue().split("\\^");
    if (locationParts.length > 1) {
      String room = locationParts[1];
      log.info("patient room: {}", room);
      return Optional.of(room);
    } else {
      return Optional.empty();
    }
  }

  /**
   * Gets the observation value.
   *
   * @param observation
   *     the observation to read
   * @return the quantity value
   */
  public Optional<String> getObservationQuantityValue(Observation observation) {
    QuantityDt quantity = (QuantityDt) observation.getValue();
    if (quantity != null && quantity.getValue() != null) {
      return Optional.of(quantity.getValue().toString());
    }

    return Optional.empty();
  }

  /**
   * Gets the observation value.
   *
   * @param observation
   *     the observation to read
   * @return the string value
   */
  public Optional<String> getObservationStringValue(Observation observation) {
    StringDt stringDt = (StringDt) observation.getValue();
    String value = stringDt.getValue();
    return Strings.isNullOrEmpty(value) ? Optional.empty() : Optional.of(value);
  }

  /**
   * Get the observation code
   *
   * @param observation
   *     the observation to search
   * @return code
   *     the code for which to search
   */
  public Optional<String> getObservationCode(Observation observation) {
    String code = observation.getCode().getCoding().get(0).getCode();
    if (!code.equals("")) {
      return Optional.of(code);
    }

    return Optional.empty();
  }

  /**
   * Get the observation time
   *
   * @param observation
   *     the observation to search
   * @return time
   *     the time of the observation
   */
  public Optional<DateTimeDt> getObservationTime(Observation observation) {
    IDatatype applies = observation.getApplies();
    if (applies instanceof DateTimeDt) {
      log.info("observation time (DateTimeDt): {}", ((DateTimeDt) applies).getValue());
      return Optional.of(((DateTimeDt) applies));
    } else if (applies instanceof PeriodDt) {
      // If the Observation contains a Period, only use the start time.
      log.info("observation time (PeriodDt): {}",
          ((PeriodDt) applies).getStartElement().getValue());
      return Optional.of(((PeriodDt) applies).getStartElement());
    }

    return Optional.empty();
  }

  /**
   * Get the observation units of measure
   *
   * @param observation
   *     the observation to search
   * @return value
   *     the units of measure for the observation
   */
  public String getObservationUnits(Observation observation) {
    QuantityDt quantity = (QuantityDt) observation.getValue();
    log.info("observation units: {}", quantity.getUnits());
    return quantity.getUnits();
  }

  /**
   * Get the patient's institution patient identifier
   *
   * @param patient
   *     the patient resource
   * @return identifier
   *     the patient identifier assigned by the institution
   */
  public String getInstitutionPatientId(UnitedStatesPatient patient) {
    log.info("patient institution identifier: {}", patient.getIdentifierFirstRep().getValue());
    return patient.getIdentifierFirstRep().getValue();
  }

  /**
   * Get the encounter identifier
   *
   * @param encounter
   *     the encounter resource
   * @return identifier
   *     the encounter identifier
   */
  public String getEncounterIdentifier(Encounter encounter) {
    return encounter.getIdentifierFirstRep().getValue();
  }

  /**
   * Get the medication name from the MedicationPrescription resource
   *
   * @param prescription
   *     the MedicationPrescription resource to search
   * @return name
   *     the name of the medication
   */
  public Optional<String> getMedicationName(MedicationPrescription prescription) {
    String name = prescription.getIdentifierFirstRep().getValue();
    if (!name.equals("")) {
      log.info("medication name: {}", name);
      return Optional.of(name);
    }

    log.info("medication name: {empty}");
    return Optional.empty();
  }

  /**
   * Get the medication status from the MedicationPrescription resource
   *
   * @param prescription
   *     the MedicationPrescription resource to search
   * @return status
   *     the status of the prescription
   */
  public Optional<String> getPrescriptionStatus(MedicationPrescription prescription) {
    String status = prescription.getStatus();
    if (!status.equals("")) {
      log.info("prescription status: {}", status);
      return Optional.of(status);
    }

    log.info("prescription status: {empty}");
    return Optional.empty();
  }

}
