// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import com.datafascia.common.persist.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Demographic and daily process information intended for consumption by Emerge at UCSF.
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class HarmEvidence {

  @JsonProperty("@id")
  private Id<HarmEvidence> id;

  @JsonProperty(value = "Ward Identifier")
  private String wardIdentifier;

  @JsonProperty(value = "Encounter Identifier")
  private String encounterIdentifier;

  // Demographic Fields
  private static final String DEFAULT_READMISSION = "No";
  private static final String DEFAULT_WEIGHT = "0";
  private static final String DEFAULT_HEIGHT = "0";
  private static final String DEFAULT_PRIOR_HOSPITAL_STAY = "Unknown";
  private static final String DEFAULT_HIGHEST_LEVEL_ACTIVITY = "Unknown";
  private static final String DEFAULT_SCREENING_TOOL_USED = "Yes";
  private static final String DEFAULT_IVC_FILTER = "No";

  @JsonProperty(value = "Patient Identifier")
  private String patientIdentifier;

  @JsonProperty(value = "Patient Account Number")
  private String patientAccountNumber;

  @JsonProperty(value = "Patient Name")
  private String patientName;

  @JsonProperty(value = "SICU Admission Date")
  private String sicuAdmissionDate;

  @JsonProperty(value = "Readmission")
  private String readmission = DEFAULT_READMISSION;

  @JsonProperty(value = "Patient Date of Birth")
  private String patientDateOfBirth;

  @JsonProperty(value = "Gender")
  private String gender;

  @JsonProperty(value = "Race")
  private String race;

  @JsonProperty(value = "Patient Admission Weight (kg)")
  private String patientAdmissionWeightKg = DEFAULT_WEIGHT;

  @JsonProperty(value = "Patient Admission Height (cm)")
  private String patientAdmissionHeightCm = DEFAULT_HEIGHT;

  @JsonProperty(value = "Prior to Hospital Stay")
  private String priorToHospitalStay = DEFAULT_PRIOR_HOSPITAL_STAY;

  @JsonProperty(value = "Highest-level Activity")
  private String highestLevelActivity = DEFAULT_HIGHEST_LEVEL_ACTIVITY;

  @JsonProperty(value = "Screening Tool Used")
  private String screeningToolUsed = DEFAULT_SCREENING_TOOL_USED;

  @JsonProperty(value = "IVC Filter")
  private String ivcFilter = DEFAULT_IVC_FILTER;

  // Daily process fields
  @JsonProperty(value = "Date Created")
  private String dateCreated;
  @JsonProperty(value = "Date Updated")
  private String dateUpdated;
  @JsonProperty(value = "IP Address")
  private String ipAddress;
  @JsonProperty(value = "Data Collection Date")
  private String dataCollectionDate;
  @JsonProperty(value = "JHED ID")
  private String jhedId = "Topaz";
  @JsonProperty(value = "Room Number")
  private String roomNumber;
  @JsonProperty(value = "Daily Goal Sheet Present")
  private String dailyGoalSheetPresent = "Not Available";
  @JsonProperty(value = "Comfort Care Orders")
  private String comfortCareOrders = "No";
  @JsonProperty(value = "Family Meeting Note")
  private String familyMeetingNote = "No";
  @JsonProperty(value = "Palliative Care Note")
  private String palliativeCareNote = "No";
  @JsonProperty(value = "Pastoral Care Note")
  private String pastoralCareNote = "No";
  @JsonProperty(value = "Social Work Note")
  private String socialWorkNote = "No";
  @JsonProperty(value = "Clinician Pain Goal")
  private String clinicianPainGoal = "11";
  @JsonProperty(value = "Clinician RASS Goal")
  private String clinicianRassGoal = "11";
  @JsonProperty(value = "Numerical Pain Level (Low)")
  private String numericalPainLevelLow = "11";
  @JsonProperty(value = "Numerical Pain Level (High)")
  private String numericalPainLevelHigh = "11";
  @JsonProperty(value = "Behavior Pain Level (Low)")
  private String behaviorPainLevelLow = "11";
  @JsonProperty(value = "Behavior Pain Level (High)")
  private String behaviorPainLevelHigh = "11";
  @JsonProperty(value = "RASS Level (Low)")
  private String rassLevelLow = "11";
  @JsonProperty(value = "RASS Level (High)")
  private String rassLevelHigh = "11";
  @JsonProperty(value = "CAM-ICU Result")
  private String camIcuResult = "Not Completed";
  @JsonProperty(value = "Continuous Infusion Lorazepam")
  private String continuousInfusionLorazepam = "No";
  @JsonProperty(value = "Continuous Infusion Midazolam")
  private String continuousInfusionMidazolam = "No";
  @JsonProperty(value = "Daily Sedation Interruption Candidate")
  private String dailySedationInterruptionCandidate = "No: Off Sedation";
  @JsonProperty(value = "Daily Sedation Interruption")
  private String dailySedationInterruption = "Not Applicable";
  @JsonProperty(value = "Intermittent Chlordiazepoxide")
  private String intermittentChlordiazepoxide = "No";
  @JsonProperty(value = "Intermittent Lorazepam IV")
  private String intermittentLorazepamIv = "No";
  @JsonProperty(value = "Intermittent Lorazepam PO")
  private String intermittentLorazepamPo = "No";
  @JsonProperty(value = "Intermittent Midazolam IV")
  private String intermittentMidazolamIv = "No";
  @JsonProperty(value = "Number of Assists")
  private String numberOfAssists = "Not Documented";
  @JsonProperty(value = "Assist Devices")
  private String assistDevices = "Not Documented";
  @JsonProperty(value = "HOB greater than or equal to 30 degrees")
  private String hobGreaterThanOrEqualTo30Degrees = "No";
  @JsonProperty(value = "Proton Pump Inhibitor / H2 Blocker")
  private String protonPumpInhibitorH2Blocker = "No";
  @JsonProperty(value = "Ventilated")
  private String ventilated = "No";
  @JsonProperty(value = "Ventilation Mode")
  private String ventilationMode = "0";
  @JsonProperty(value = "Current Tidal Volume (millimeters)")
  private String currentTidalVolumeMillimeters = "0";
  @JsonProperty(value = "Tracheostomy")
  private String tracheostomy = "No";
  @JsonProperty(value = "Sub-glottic Suction ETT")
  private String subGlotticSuctionEtt = "No";
  @JsonProperty(value = "Oral Care")
  private String oralCare = "No";
  @JsonProperty(value = "In-line Suction")
  private String inLineSuction = "No";
  @JsonProperty(value = "Daily Spontaneous Breathing Trial")
  private String dailySpontaneousBreathingTrial = "0";
  @JsonProperty(value = "Daily Needs Assessment")
  private String dailyNeedsAssessment = "No";
  @JsonProperty(value = "Anti-Coagulated")
  private String antiCoagulated = "No";
  @JsonProperty(value = "Mechanical")
  private String mechanical = "No: Not Ordered";
  @JsonProperty(value = "Pharmacologic")
  private String pharmacologic = "No";

  // Orphan values
  @JsonProperty(value = "Intermittent Clonazepam Enteral")
  private String intermittentClonazepamEnteral;

  @JsonProperty(value = "Intermittent Diazepam IV")
  private String intermittentDiazepamIv;

  @JsonProperty(value = "Intermittent Diazepam Enteral")
  private String intermittentDiazepamEnteral;

  @JsonProperty(value = "Intermittent Alprazalom Enteral")
  private String intermittentAlprazalomEnteral;

  @JsonProperty(value = "Benzodiazepine Avoidance Contraindicated")
  private String benzodiazepineAvoidanceContraindicated;

  @JsonProperty(value = "Lower Extremity SCDsContraindicated")
  private String lowerExtremityScdscontraindicated;

  @JsonProperty(value = "SCDs Ordered")
  private String scdsOrdered;

  @JsonProperty(value = "SCDs In Use")
  private String scdsInUse;

  @JsonProperty(value = "Pharmacologic VTE ProphylaxisContraindicated")
  private String pharmacologicVteProphylaxiscontraindicated;

  @JsonProperty(value = "Pharmacologic VTE Prophylaxis Ordered?")
  private String pharmacologicVteProphylaxisOrdered;

  @JsonProperty(value = "Pharmacologic VTE Prophylaxis Administered?")
  private String pharmacologicVteProphylaxisAdministered;

  @JsonProperty(value = "Sub-glottic Suction Use")
  private String subGlotticSuctionUse;

  @JsonProperty(value = "Recent Stress Ulcer ProphylaxisAdministration")
  private String recentStressUlcerProphylaxisadministration;

  @JsonProperty(value = "Mechanical Ventilation >48 Hours")
  private String mechanicalVentilationOver48Hours;

  @JsonProperty(value = "Line 1 Type")
  private String line1Type;

  @JsonProperty(value = "Line 1 Site")
  private String line1Site;

  @JsonProperty(value = "Line 1 Days")
  private String line1Days;

  @JsonProperty(value = "Line 2 Type")
  private String line2Type;

  @JsonProperty(value = "Line 2 Site")
  private String line2Site;

  @JsonProperty(value = "Line 2 Days")
  private String line2Days;

  @JsonProperty(value = "Line 3 Type")
  private String line3Type;

  @JsonProperty(value = "Line 3 Site")
  private String line3Site;

  @JsonProperty(value = "Line 3 Days")
  private String line3Days;

  @JsonProperty(value = "Line 4 Type")
  private String line4Type;

  @JsonProperty(value = "Line 4 Site")
  private String line4Site;

  @JsonProperty(value = "Line 4 Days")
  private String line4Days;

  @JsonProperty(value = "Line 5 Type")
  private String line5Type;

  @JsonProperty(value = "Line 5 Site")
  private String line5Site;

  @JsonProperty(value = "Line 5 Days")
  private String line5Days;

  @JsonProperty(value = "ICU Attending Participant")
  private String icuAttendingParticipant;

  @JsonProperty(value = "ICU Attending Period")
  private String icuAttendingPeriod;

  @JsonProperty(value = "Primary Care Team Attending Participant")
  private String primaryCareTeamAttendingParticipant;

  @JsonProperty(value = "Primary Care Team Attending Period")
  private String primaryCareTeamAttendingPeriod;

  @JsonProperty(value = "Nurse Participant")
  private String nurseParticipant;

  @JsonProperty(value = "Nurse Period")
  private String nursePeriod;

  @JsonProperty(value = "Patient Care Conference Note")
  private String patientCareConferenceNote;

  @JsonProperty(value = "AD/POLST")
  private String adPolst;

  @JsonProperty(value = "Code Status")
  private String codeStatus;

  @JsonProperty(value = "Anticoagulation Type")
  private String anticoagulationType;

  @JsonProperty(value = "Pharmacologic VTE Prophylaxis Type")
  private String pharmacologicVteProphylaxisType;

  @JsonProperty(value = "PT Mobility Score")
  private String ptMobilityScore;

  @JsonProperty(value = "OT Mobility Score")
  private String otMobilityScore;

  @JsonProperty(value = "RN Mobility Score")
  private String rnMobilityScore;

  @JsonProperty(value = "Overall Mobility Score")
  private String overallMobilityScore;
}
