// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is used to hold the Demographic data for Emerge and can be serialized, deserialized to
 * and from a CSV file.
 */
@Data @NoArgsConstructor
public class Demographic {

  private static final String DEFAULT_JHED_ID = "Topaz";
  private static final String DEFAULT_WEIGHT = "0";
  private static final String DEFAULT_HEIGHT = "0";
  private static final String DEFAULT_PRIOR_HOSPITAL_STAY = "Unknown";
  private static final String DEFAULT_HIGHEST_LEVEL_ACTIVITY = "Unknown";
  private static final String DEFAULT_SCREENING_TOOL_USED = "Yes";
  private static final String DEFAULT_IVC_FILTER = "No";

  @JsonProperty(value = "Entry #", index = 0)
  private String entry;

  @JsonProperty(value = "Date Created", index = 1)
  private String dateCreated;

  @JsonProperty(value = "Date Updated", index = 2)
  private String dateUpdated;

  @JsonProperty(value = "IP Address", index = 3)
  private String ipAddress;

  @JsonProperty(value = "Data Collection Date", index = 4)
  private String dataCollectionDate;

  @JsonProperty(value = "JHED ID", index = 5)
  private String jhedId = DEFAULT_JHED_ID;

  @JsonProperty(value = "Subject Patient ID", index = 6)
  private String subjectPatientId;

  @JsonProperty(value = "Subject Patcom", index = 7)
  private String subjectPatcom;

  @JsonProperty(value = "Patient Name", index = 8)
  private String patientName;

  @JsonProperty(value = "SICU Admission Date", index = 9)
  private String sicuAdmissionDate;

  @JsonProperty(value = "Patient Date of Birth", index = 10)
  private String patientDateOfBirth;

  @JsonProperty(value = "Gender", index = 11)
  private String gender;

  @JsonProperty(value = "Race", index = 12)
  private String race;

  @JsonProperty(value = "Patient Admission Weight (kg)", index = 13)
  private String patientAdmissionWeightKg = DEFAULT_WEIGHT;

  @JsonProperty(value = "Patient Admission Height (cm)", index = 14)
  private String patientAdmissionHeightCm = DEFAULT_HEIGHT;

  @JsonProperty(value = "Prior to Hospital Stay", index = 15)
  private String priorToHospitalStay = DEFAULT_PRIOR_HOSPITAL_STAY;

  @JsonProperty(value = "Highest-level Activity", index = 16)
  private String highestLevelActivity = DEFAULT_HIGHEST_LEVEL_ACTIVITY;

  @JsonProperty(value = "Screening Tool Used", index = 17)
  private String screeningToolUsed = DEFAULT_SCREENING_TOOL_USED;

  @JsonProperty(value = "IVC Filter", index = 18)
  private String ivcFilter = DEFAULT_IVC_FILTER;
}
