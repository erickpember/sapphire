// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.HumanNames;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.emerge.ucsf.DemographicData;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.persist.HarmEvidenceRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Updates harm evidence for a patient in response to an event.
 */
public class HarmEvidenceUpdater {

  @Inject
  private HarmEvidenceRepository harmEvidenceRepository;

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

  private static String formatInstant(Instant instant) {
    return DateTimeFormatter.ISO_INSTANT.format(instant);
  }

  private static String formatDateTime(Date date) {
    return formatInstant(date.toInstant());
  }

  private static String formatNow() {
    return formatInstant(Instant.now());
  }

  private static String formatLocalDate(Date date) {
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    return DateTimeFormatter.ISO_LOCAL_DATE.format(zonedDateTime.toLocalDate());
  }

  private static DemographicData.Gender formatGender(UnitedStatesPatient patient) {
    return (patient.getGenderElement().getValueAsEnum() == AdministrativeGenderEnum.FEMALE)
        ? DemographicData.Gender.FEMALE : DemographicData.Gender.MALE;
  }
  private static String formatRoomNumber(Location location) {
    String[] locationParts = location.getIdentifierFirstRep().getValue().split("\\^");
    String room = locationParts[1];
    String bed = locationParts[2];
    return room + '-' + bed;
  }

  private HarmEvidence getHarmEvidence(String inputPatientId) {
    Id<HarmEvidence> patientId = Id.of(inputPatientId);
    Optional<HarmEvidence> optionalHarmEvidence = harmEvidenceRepository.read(patientId);
    if (!optionalHarmEvidence.isPresent()) {
      return new HarmEvidence()
          .withDemographicData(new DemographicData())
          .withMedicalData(new MedicalData());
    }
    return optionalHarmEvidence.get();
  }

  private HarmEvidence updateHarmEvidence(
      UnitedStatesPatient patient, Location location, Encounter encounter) {

    HarmEvidence harmEvidence = getHarmEvidence(patient.getId().getIdPart())
        .withPatientID(
            patient.getIdentifierFirstRep().getValue())
        .withEncounterID(
            encounter.getIdentifierFirstRep().getValue());

    DemographicData demographicData = harmEvidence.getDemographicData();
    demographicData
        .withPatientName(
            HumanNames.toFullName(patient.getNameFirstRep()))
        .withRace(
            formatRace(patient))
        .withGender(
            formatGender(patient))
        .withUpdateTime(
            formatNow());

    if (!encounter.getPeriod().getStartElement().isEmpty()) {
      demographicData.setICUadmitDate(
          formatDateTime(encounter.getPeriod().getStartElement().getValue()));
    }

    if (!patient.getBirthDateElement().isEmpty()) {
      demographicData.setDateOfBirth(
          formatLocalDate(patient.getBirthDateElement().getValue()));
    }

    return harmEvidence;
  }

  /**
   * Admits patient.
   *
   * @param patient
   *     patient
   * @param location
   *     location
   * @param encounter
   *     encounter
   */
  public void admitPatient(
      UnitedStatesPatient patient, Location location, Encounter encounter) {

    HarmEvidence harmEvidence = updateHarmEvidence(patient, location, encounter);
    harmEvidenceRepository.save(harmEvidence);
  }

  /**
   * Discharges patient.
   *
   * @param encounter
   *     encounter
   */
  public void dischargePatient(Encounter encounter) {
    String patientIdString = encounter.getPatient().getReference().getIdPart();
    Id<HarmEvidence> patientId = Id.of(patientIdString);
    harmEvidenceRepository.delete(patientId);
  }
}
