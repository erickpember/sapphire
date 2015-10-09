// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import com.datafascia.domain.fhir.HumanNames;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.emerge.ucsf.DemographicData;
import com.datafascia.emerge.ucsf.HarmEvidence;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.inject.Inject;

/**
 * Updates demographic data for a patient.
 */
public class DemographicDataUpdater {

  @Inject
  private Clock clock;

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

  private Date formatNow() {
    return Date.from(Instant.now(clock));
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

  /**
   * Updates demographic data.
   *
   * @param harmEvidence
   *     to modify
   * @param patient
   *     patient
   * @param location
   *     location
   * @param encounter
   *     encounter
   */
  public void update(
      HarmEvidence harmEvidence,
      UnitedStatesPatient patient,
      Location location,
      Encounter encounter) {

    harmEvidence.setEncounterID(encounter.getIdentifierFirstRep().getValue());

    DemographicData demographicData = harmEvidence.getDemographicData()
        .withPatientName(
            HumanNames.toFullName(patient.getNameFirstRep()))
        .withMedicalRecordNumber(
            patient.getIdentifierFirstRep().getValue())
        .withGender(
            formatGender(patient))
        .withRace(
            formatRace(patient))
        .withRoomNumber(
            formatRoomNumber(location))
        .withUpdateTime(
            formatNow());

    if (!encounter.getPeriod().getStartElement().isEmpty()) {
      demographicData.setICUadmitDate(
          encounter.getPeriod().getStartElement().getValue());
    }

    if (!patient.getBirthDateElement().isEmpty()) {
      demographicData.setDateOfBirth(
          formatLocalDate(patient.getBirthDateElement().getValue()));
    }
  }
}