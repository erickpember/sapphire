// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import com.datafascia.domain.event.AdmitPatientData;
import com.datafascia.domain.event.EncounterData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.PatientData;
import com.datafascia.domain.fhir.Dates;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.Languages;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.LocationRepository;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.persist.HarmEvidenceRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.StringJoiner;
import java.util.function.Consumer;
import javax.inject.Inject;

/**
 * Processes admit patient event.
 */
public class AdmitPatient implements Consumer<Event> {

  private static final ZoneId TIME_ZONE = ZoneId.of("America/Los_Angeles");

  @Inject
  private transient PatientRepository patientRepository;

  @Inject
  private transient LocationRepository locationRepository;

  @Inject
  private transient EncounterRepository encounterRepository;

  @Inject
  private transient HarmEvidenceRepository harmEvidenceRepository;

  private static UnitedStatesPatient createPatient(PatientData patientData) {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT)
        .setValue(patientData.getInstitutionPatientId());
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT)
        .setValue(patientData.getAccountNumber());
    patient.addName()
        .addGiven(patientData.getFirstName())
        .addGiven(patientData.getMiddleName())
        .addFamily(patientData.getLastName());
    patient.addCommunication()
        .setPreferred(true).setLanguage(Languages.createLanguage(patientData.getLanguage()));
    patient
        .setRace(patientData.getRace())
        .setGender(patientData.getGender())
        .setBirthDate(Dates.toDate(patientData.getBirthDate()))
        .setMaritalStatus(patientData.getMaritalStatus())
        .setActive(true);
    return patient;
  }

  private static Location createLocation(EncounterData fromEncounter) {
    String identifier = fromEncounter.getLocation();
    Location location = new Location()
        .setName(identifier);
    location.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_LOCATION)
        .setValue(identifier);
    return location;
  }

  private static Encounter createEncounter(
      EncounterData fromEncounter, UnitedStatesPatient patient, Location location) {

    PeriodDt period = new PeriodDt();
    period.setStart(Date.from(fromEncounter.getAdmitTime()), TemporalPrecisionEnum.SECOND);

    Encounter encounter = new Encounter();
    encounter.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue(fromEncounter.getIdentifier());
    encounter
        .setStatus(EncounterStateEnum.IN_PROGRESS)
        .setPeriod(period)
        .setPatient(new ResourceReferenceDt(patient.getId()))
        .addLocation().setLocation(new ResourceReferenceDt(location.getId()));
    return encounter;
  }

  private static String formatPatientName(UnitedStatesPatient patient) {
    StringJoiner joiner = new StringJoiner(" ");

    HumanNameDt humanName = patient.getNameFirstRep();
    humanName.getGiven()
        .stream()
        .forEach(name -> joiner.add(name.getValue()));
    humanName.getFamily()
        .stream()
        .forEach(name -> joiner.add(name.getValue()));

    return joiner.toString();
  }

  private static RaceEnum getRace(UnitedStatesPatient patient) {
    for (RaceEnum race : patient.getRace().getValueAsEnum()) {
      return race;
    }
    return RaceEnum.UNKNOWN;
  }

  private static String formatRace(UnitedStatesPatient patient) {
    switch (getRace(patient)) {
      case AMERICAN_INDIAN:
        return "I";
      case ASIAN:
        return "N";
      case BLACK:
        return "B";
      case OTHER:
        return "O";
      case PACIFIC_ISLANDER:
        return "P";
      case WHITE:
        return "W";
      default:
        return "U";
    }
  }

  private static String formatLocalDate(Date date) {
    LocalDate localDate = date.toInstant().atZone(TIME_ZONE).toLocalDate();
    return DateTimeFormatter.ISO_LOCAL_DATE.format(localDate);
  }

  private static String formatRoomNumber(Location location) {
    String[] locationParts = location.getIdentifierFirstRep().getValue().split("\\^");
    return (locationParts.length > 1) ? locationParts[1] : "";
  }

  private static HarmEvidence createHarmEvidence(
      Encounter encounter, UnitedStatesPatient patient, Location location) {

    return HarmEvidence.builder()
        .encounterIdentifier(encounter.getIdentifierFirstRep().getValue())
        .sicuAdmissionDate(formatLocalDate(encounter.getPeriod().getStart()))
        .patientIdentifier(patient.getIdentifierFirstRep().getValue())
        .patientAccountNumber(patient.getIdentifier().get(1).getValue())
        .patientName(formatPatientName(patient))
        .race(formatRace(patient))
        .gender(patient.getGender().equals("female") ? "Female" : "Male")
        .patientDateOfBirth(formatLocalDate(patient.getBirthDate()))
        .roomNumber(formatRoomNumber(location))
        .build();
  }

  @Override
  public void accept(Event event) {
    AdmitPatientData admitPatientData = (AdmitPatientData) event.getData();

    UnitedStatesPatient patient = createPatient(admitPatientData.getPatient());
    patientRepository.save(patient);

    Location location = createLocation(admitPatientData.getEncounter());
    locationRepository.save(location);

    Encounter encounter = createEncounter(admitPatientData.getEncounter(), patient, location);
    encounterRepository.save(encounter);

    HarmEvidence harmEvidence = createHarmEvidence(encounter, patient, location);
    harmEvidenceRepository.save(harmEvidence);
  }
}
