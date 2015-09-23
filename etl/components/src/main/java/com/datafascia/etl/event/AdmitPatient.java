// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.LocationRepository;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.persist.HarmEvidenceRepository;
import java.util.StringJoiner;
import javax.inject.Inject;

/**
 * Admits patient.
 */
public class AdmitPatient {

  @Inject
  private PatientRepository patientRepository;

  @Inject
  private LocationRepository locationRepository;

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private HarmEvidenceRepository harmEvidenceRepository;

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

  private static String formatRoomNumber(Location location) {
    String[] locationParts = location.getIdentifierFirstRep().getValue().split("\\^");
    return (locationParts.length > 1) ? locationParts[1] : "";
  }

  private HarmEvidence createHarmEvidence(
      Encounter encounter, UnitedStatesPatient patient, Location location) {

    HarmEvidence harmEvidence = HarmEvidence.builder()
        .encounterIdentifier(encounter.getIdentifierFirstRep().getValue())
        .patientIdentifier(patient.getIdentifierFirstRep().getValue())
        .patientAccountNumber(patient.getIdentifier().get(1).getValue())
        .patientName(formatPatientName(patient))
        .race(formatRace(patient))
        .gender(patient.getGender().equals("female") ? "Female" : "Male")
        .roomNumber(formatRoomNumber(location))
        .build();

    if (!encounter.getPeriod().getStartElement().isEmpty()) {
      harmEvidence.setSicuAdmissionDate(encounter.getPeriod().getStartElement().toHumanDisplay());
    }

    if (!patient.getBirthDateElement().isEmpty()) {
      harmEvidence.setPatientDateOfBirth(patient.getBirthDateElement().toHumanDisplay());
    }

    return harmEvidence;
  }

  /**
   * Admits patient.
   *
   * @param triggerEvent
   *     MSH trigger event
   * @param patient
   *     patient
   * @param location
   *     location
   * @param encounter
   *     encounter
   */
  public void accept(
      String triggerEvent,
      UnitedStatesPatient patient,
      Location location,
      Encounter encounter) {

    patientRepository.save(patient);

    locationRepository.save(location);

    encounter
        .setPatient(new ResourceReferenceDt(patient))
        .addLocation().setLocation(new ResourceReferenceDt(location));

    encounterRepository.save(encounter);

    HarmEvidence harmEvidence = createHarmEvidence(encounter, patient, location);
    harmEvidenceRepository.save(harmEvidence);
  }
}
