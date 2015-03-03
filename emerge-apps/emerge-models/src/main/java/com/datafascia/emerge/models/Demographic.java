// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is used to hold the Demographic data for Emerge and can be serialized, deserialized to
 * and from a CSV file.
 */
@Slf4j @NoArgsConstructor @EqualsAndHashCode
public class Demographic {

  private static final String DEFAULT_JHED_ID = "Topaz";
  private static final String DEFAULT_READMISSION = "No";
  private static final String DEFAULT_WEIGHT = "0";
  private static final String DEFAULT_HEIGHT = "0";
  private static final String DEFAULT_PRIOR_HOSPITAL_STAY = "Unknown";
  private static final String DEFAULT_HIGHEST_LEVEL_ACTIVITY = "Unknown";
  private static final String DEFAULT_SCREENING_TOOL_USED = "Yes";
  private static final String DEFAULT_IVC_FILTER = "No";

  @Getter @Setter @JsonProperty(value = "Entry #", index = 0)
  private String entry;
  @Getter @Setter @JsonProperty(value = "Date Created", index = 1)
  private String dateCreated;
  @Getter @Setter @JsonProperty(value = "Date Updated", index = 2)
  private String dateUpdated;
  @Getter @Setter @JsonProperty(value = "IP Address", index = 3)
  private String ipAddress;
  @Getter @Setter @JsonProperty(value = "Data Collection Date", index = 4)
  private String dataCollectionDate;
  @Getter @Setter @JsonProperty(value = "JHED ID", index = 5)
  private String jhedId = DEFAULT_JHED_ID;
  @Getter @Setter @JsonProperty(value = "Subject Patient ID", index = 6)
  private String subjectPatientId;
  @Getter @Setter @JsonProperty(value = "Subject Patcom", index = 7)
  private String subjectPatcom;
  @Getter @Setter @JsonProperty(value = "Patient Name", index = 8)
  private String patientName;
  @Getter @Setter @JsonProperty(value = "SICU Admission Date", index = 9)
  private String sicuAdmissionDate;
  @Getter @Setter @JsonProperty(value = "Readmission", index = 10)
  private String readmission = DEFAULT_READMISSION;
  @Getter @Setter @JsonProperty(value = "Patient Date of Birth", index = 11)
  private String patientDateOfBirth;
  @Getter @Setter @JsonProperty(value = "Gender", index = 12)
  private String gender;
  @Getter @Setter @JsonProperty(value = "Race", index = 13)
  private String race;
  @Getter @Setter @JsonProperty(value = "Patient Admission Weight (kg)", index = 14)
  private String patientAdmissionWeightKg = DEFAULT_WEIGHT;
  @Getter @Setter @JsonProperty(value = "Patient Admission Height (cm)", index = 15)
  private String patientAdmissionHeightCm = DEFAULT_HEIGHT;
  @Getter @Setter @JsonProperty(value = "Prior to Hospital Stay", index = 16)
  private String priorToHospitalStay = DEFAULT_PRIOR_HOSPITAL_STAY;
  @Getter @Setter @JsonProperty(value = "Highest-level Activity", index = 17)
  private String highestLevelActivity = DEFAULT_HIGHEST_LEVEL_ACTIVITY;
  @Getter @Setter @JsonProperty(value = "Screening Tool Used", index = 18)
  private String screeningToolUsed = DEFAULT_SCREENING_TOOL_USED;
  @Getter @Setter @JsonProperty(value = "IVC Filter", index = 19)
  private String ivcFilter = DEFAULT_IVC_FILTER;
}
