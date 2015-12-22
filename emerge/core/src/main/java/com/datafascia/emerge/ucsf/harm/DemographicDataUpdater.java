// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import com.datafascia.domain.fhir.HumanNames;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.emerge.harms.demographic.BodyHeight;
import com.datafascia.emerge.harms.demographic.BodyWeight;
import com.datafascia.emerge.ucsf.DemographicData;
import com.datafascia.emerge.ucsf.EncounterUtils;
import com.datafascia.emerge.ucsf.HarmEvidence;
import java.math.BigDecimal;
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

  private static final BigDecimal ABSENT_VALUE = new BigDecimal(-1);

  @Inject
  private Clock clock;

  @Inject
  private BodyHeight bodyHeight;

  @Inject
  private BodyWeight bodyWeight;

  /**
   * Creates demographic data with initial values.
   *
   * @param encounter
   *     encounter
   * @return demographic data
   */
  public static DemographicData createDemographicData(Encounter encounter) {
    return new DemographicData()
        .withMedicalRecordNumber(
            encounter.getPatient().getReference().getIdPart())
        .withAdmissionHeight(
            ABSENT_VALUE)
        .withAdmissionWeight(
            ABSENT_VALUE);
  }

  private static RaceEnum getRace(UnitedStatesPatient patient) {
    for (RaceEnum race : patient.getRace().getValueAsEnum()) {
      return race;
    }
    return RaceEnum.UNKNOWN;
  }

  private static String formatRace(UnitedStatesPatient patient) {
    switch (getRace(patient)) {
      // Notice that this is defaulting to "Other" for two race codes.
      case PACIFIC_ISLANDER:
      case AMERICAN_INDIAN:
      case OTHER:
        return "Other";
      case ASIAN:
        return "Asian";
      case BLACK:
        return "Black";
      case WHITE:
        return "White";
      case UNKNOWN:
        return "Unknown";
      default:
        return "Not Documented";
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

    try {
      String zeroPadRoomNumber = String.format("%04d", Integer.parseInt(room));
      return zeroPadRoomNumber + '-' + bed;
    } catch (NumberFormatException e) {
      return room + '-' + bed;
    }
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
        .withICUadmitDate(
            Date.from(EncounterUtils.getIcuPeriodStart(encounter)))
        .withUpdateTime(
            formatNow());

    if (!patient.getBirthDateElement().isEmpty()) {
      demographicData.setDateOfBirth(
          formatLocalDate(patient.getBirthDateElement().getValue()));
    }
  }

  /**
   * Updates patient height.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateHeight(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getIdentifierFirstRep().getValue();

    harmEvidence.getDemographicData()
        .withAdmissionHeight(
            bodyHeight.apply(encounterId));
  }

  /**
   * Updates patient weight.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateWeight(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getIdentifierFirstRep().getValue();

    harmEvidence.getDemographicData()
        .withAdmissionWeight(
            bodyWeight.apply(encounterId));
  }
}
