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
 * This class is used to hold the Daily Process data for Emerge and can be serialized, deserialized
 * to and from a CSV file.
 *
 * There are 112 fields here. 14 headers expected by Emerge contain trailing spaces, but no headers
 * produced by this template will have those. Variable names are based on header names, with
 * non-alphanumeric characters stripped and converted to java variable camel case. Defaults exist
 * for some variables, expected by Emerge instead of nulls.
 */
@Slf4j @NoArgsConstructor @EqualsAndHashCode
public class DailyProcess {
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
  private String jhedId = "Topaz";
  @Getter @Setter @JsonProperty(value = "Subject ID", index = 6)
  private String subjectId;
  @Getter @Setter @JsonProperty(value = "Subject Patcom", index = 7)
  private String subjectPatcom;
  @Getter @Setter @JsonProperty(value = "Room Number", index = 8)
  private String roomNumber;
  @Getter @Setter @JsonProperty(value = "Daily Goal Sheet Present", index = 9)
  private String dailyGoalSheetPresent = "Not Available";
  @Getter @Setter @JsonProperty(value = "Comfort Care Orders", index = 10)
  private String comfortCareOrders = "No";
  @Getter @Setter @JsonProperty(value = "Family Meeting Note", index = 11)
  private String familyMeetingNote = "No";
  @Getter @Setter @JsonProperty(value = "Palliative Care Note", index = 12)
  private String palliativeCareNote = "No";
  @Getter @Setter @JsonProperty(value = "Pastoral Care Note", index = 13)
  private String pastoralCareNote = "No";
  @Getter @Setter @JsonProperty(value = "Social Work Note", index = 14)
  private String socialWorkNote = "No";
  @Getter @Setter @JsonProperty(value = "Clinician Pain Goal", index = 15)
  private String clinicianPainGoal = "11";
  @Getter @Setter @JsonProperty(value = "Clinician RASS Goal", index = 16)
  private String clinicianRassGoal = "11";
  @Getter @Setter @JsonProperty(value = "Numerical Pain Level (Low)", index = 17)
  private String numericalPainLevelLow = "11";
  @Getter @Setter @JsonProperty(value = "Numerical Pain Level (High)", index = 18)
  private String numericalPainLevelHigh = "11";
  @Getter @Setter @JsonProperty(value = "Behavior Pain Level (Low)", index = 19)
  private String behaviorPainLevelLow = "11";
  @Getter @Setter @JsonProperty(value = "Behavior Pain Level (High)", index = 20)
  private String behaviorPainLevelHigh = "11";
  @Getter @Setter @JsonProperty(value = "RASS Level (Low)", index = 21)
  private String rassLevelLow = "11";
  @Getter @Setter @JsonProperty(value = "RASS Level (High)", index = 22)
  private String rassLevelHigh = "11";
  @Getter @Setter @JsonProperty(value = "CAM-ICU Result", index = 23)
  private String camIcuResult = "Not Completed";
  @Getter @Setter @JsonProperty(value = "Epidural", index = 24)
  private String epidural = "No";
  @Getter @Setter @JsonProperty(value = "Continuous Infusion Dexmedetomidine", index = 25)
  private String continuousInfusionDexmedetomidine = "No";
  @Getter @Setter
  @JsonProperty(value = "Please list the current Dexmedetomidine rate (mcg/kg/h)", index = 26)
  private String pleaseListTheCurrentDexmedetomidineRateMcgKgH;
  @Getter @Setter
  @JsonProperty(value = "Please list the highest Dexmedetomidine rate 24 hours (mcg/kg/h)",
                index = 27)
  private String pleaseListTheHighestDexmedetomidineRate24HoursMcgKgH;
  @Getter @Setter @JsonProperty(value = "Continuous Infusion Fentanyl", index = 28)
  private String continuousInfusionFentanyl = "No";
  @Getter @Setter @JsonProperty(value = "Please list the current Fentanyl rate (mcg/h)", index = 29)
  private String pleaseListTheCurrentFentanylRateMcgH;
  @Getter @Setter
  @JsonProperty(value = "Please list the highest Fentanyl rate 24 hours (mcg/h)", index = 30)
  private String pleaseListTheHighestFentanylRate24HoursMcgH;
  @Getter @Setter @JsonProperty(value = "Continuous Infusion Lorazepam", index = 31)
  private String continuousInfusionLorazepam = "No";
  @Getter @Setter @JsonProperty(value = "Please list the current Lorazepam rate (mg/h)", index = 32)
  private String pleaseListTheCurrentLorazepamRateMgH;
  @Getter @Setter
  @JsonProperty(value = "Please list the highest Lorazepam  rate 24 hours (mg/h)", index = 33)
  private String pleaseListTheHighestLorazepamRate24HoursMgH;
  @Getter @Setter @JsonProperty(value = "Continuous Infusion Midazolam", index = 34)
  private String continuousInfusionMidazolam = "No";
  @Getter @Setter @JsonProperty(value = "Please list the current Midazolam rate (mg/h)", index = 35)
  private String pleaseListTheCurrentMidazolamRateMgH;
  @Getter @Setter
  @JsonProperty(value = "Please list the highest Midazolam  rate 24 hours (mg/h)", index = 36)
  private String pleaseListTheHighestMidazolamRate24HoursMgH;
  @Getter @Setter @JsonProperty(value = "Continuous Infusion Morphine", index = 37)
  private String continuousInfusionMorphine = "No";
  @Getter @Setter @JsonProperty(value = "Please list the current Morphine rate (mg/h)", index = 38)
  private String pleaseListTheCurrentMorphineRateMgH;
  @Getter @Setter
  @JsonProperty(value = "Please list the highest Morphine rate 24 hours (mg/h)", index = 39)
  private String pleaseListTheHighestMorphineRate24HoursMgH;
  @Getter @Setter @JsonProperty(value = "Continuous Infusion Dilaudid", index = 40)
  private String continuousInfusionDilaudid = "No";
  @Getter @Setter @JsonProperty(value = "Please list the current Dilaudid rate (mg/h)", index = 41)
  private String pleaseListTheCurrentDilaudidRateMgH;
  @Getter @Setter
  @JsonProperty(value = "Please list the highest Dilaudid rate 24 hours (mg/h)", index = 42)
  private String pleaseListTheHighestDilaudidRate24HoursMgH;
  @Getter @Setter @JsonProperty(value = "Continuous Infusion Propofol", index = 43)
  private String continuousInfusionPropofol = "No";
  @Getter @Setter
  @JsonProperty(value = "Please list the current Propofol rate (mcg/kg/min)", index = 44)
  private String pleaseListTheCurrentPropofolRateMcgKgMin;
  @Getter @Setter
  @JsonProperty(value = "Please list the highest Propofol rate 24 hours (mcg/kg/min)", index = 45)
  private String pleaseListTheHighestPropofolRate24HoursMcgKgMin;
  @Getter @Setter @JsonProperty(value = "Daily Sedation Interruption Candidate", index = 46)
  private String dailySedationInterruptionCandidate = "No: Off Sedation";
  @Getter @Setter @JsonProperty(value = "Daily Sedation Interruption", index = 47)
  private String dailySedationInterruption = "Not Applicable";
  @Getter @Setter @JsonProperty(value = "Intermittent Chlordiazepoxide", index = 48)
  private String intermittentChlordiazepoxide = "No";
  @Getter @Setter
  @JsonProperty(value = "Please list the Chlordiazepoxide Dose (mg/day)", index = 49)
  private String pleaseListTheChlordiazepoxideDoseMgDay;
  @Getter @Setter @JsonProperty(value = "Intermittent Fentanyl IV", index = 50)
  private String intermittentFentanylIv = "No";
  @Getter @Setter @JsonProperty(value = "Please list the Fentanyl IV Dose (mcg/day)", index = 51)
  private String pleaseListTheFentanylIvDoseMcgDay;
  @Getter @Setter @JsonProperty(value = "Intermittent Haloperidol IV", index = 52)
  private String intermittentHaloperidolIv = "No";
  @Getter @Setter @JsonProperty(value = "Please list the Haloperidol IV Dose (mg/day)", index = 53)
  private String pleaseListTheHaloperidolIvDoseMgDay;
  @Getter @Setter @JsonProperty(value = "Intermittent Haloperidol PO", index = 54)
  private String intermittentHaloperidolPo = "No";
  @Getter @Setter @JsonProperty(value = "Please list the Haloperidol PO Dose (mg/day)", index = 55)
  private String pleaseListTheHaloperidolPoDoseMgDay;
  @Getter @Setter @JsonProperty(value = "Intermittent Hydromorphone IV", index = 56)
  private String intermittentHydromorphoneIv = "No";
  @Getter @Setter
  @JsonProperty(value = "Please list the Hydromorphone IV Dose (mg/day)", index = 57)
  private String pleaseListTheHydromorphoneIvDoseMgDay;
  @Getter @Setter @JsonProperty(value = "Intermittent Hydromorphone PO", index = 58)
  private String intermittentHydromorphonePo = "No";
  @Getter @Setter
  @JsonProperty(value = "Please list the Hydromorphone PO Dose (mg/day)", index = 59)
  private String pleaseListTheHydromorphonePoDoseMgDay;
  @Getter @Setter @JsonProperty(value = "Intermittent Lorazepam IV", index = 60)
  private String intermittentLorazepamIv = "No";
  @Getter @Setter @JsonProperty(value = "Please list the Lorazepam IV Dose (mg/day)", index = 61)
  private String pleaseListTheLorazepamIvDoseMgDay;
  @Getter @Setter @JsonProperty(value = "Intermittent Lorazepam PO", index = 62)
  private String intermittentLorazepamPo = "No";
  @Getter @Setter @JsonProperty(value = "Please list the Lorazepam PO Dose (mg/day)", index = 63)
  private String pleaseListTheLorazepamPoDoseMgDay;
  @Getter @Setter @JsonProperty(value = "Intermittent Midazolam IV", index = 64)
  private String intermittentMidazolamIv = "No";
  @Getter @Setter @JsonProperty(value = "Please list the Midazolam IV Dose (mg/day)", index = 65)
  private String pleaseListTheMidazolamIvDoseMgDay;
  @Getter @Setter @JsonProperty(value = "Intermittent Morphine IV", index = 66)
  private String intermittentMorphineIv = "No";
  @Getter @Setter @JsonProperty(value = "Please list the Morphine IV Dose (mg/day)", index = 67)
  private String pleaseListTheMorphineIvDoseMgDay;
  @Getter @Setter @JsonProperty(value = "Intermittent Morphine PO", index = 68)
  private String intermittentMorphinePo = "No";
  @Getter @Setter @JsonProperty(value = "Please list the Morphine PO Dose (mg/day)", index = 69)
  private String pleaseListTheMorphinePoDoseMgDay;
  @Getter @Setter @JsonProperty(value = "Intermittent Olanzapine PO", index = 70)
  private String intermittentOlanzapinePo = "No";
  @Getter @Setter @JsonProperty(value = "Please list the Olanzapine PO Dose (mg/day)", index = 71)
  private String pleaseListTheOlanzapinePoDoseMgDay;
  @Getter @Setter @JsonProperty(value = "Intermittent Oxycodone", index = 72)
  private String intermittentOxycodone = "No";
  @Getter @Setter @JsonProperty(value = "Please list the Oxycodone Dose (mg/day)", index = 73)
  private String pleaseListTheOxycodoneDoseMgDay;
  @Getter @Setter @JsonProperty(value = "Intermittent Quetiapine PO", index = 74)
  private String intermittentQuetiapinePo = "No";
  @Getter @Setter @JsonProperty(value = "Please list the Quetiapine Dose (mg/day)", index = 75)
  private String pleaseListTheQuetiapineDoseMgDay;
  @Getter @Setter @JsonProperty(value = "Highest Level Activity", index = 76)
  private String highestLevelActivity = "Not Documented";
  @Getter @Setter @JsonProperty(value = "Number of Assists", index = 77)
  private String numberOfAssists = "Not Documented";
  @Getter @Setter @JsonProperty(value = "Assist Devices", index = 78)
  private String assistDevices = "Not Documented";
  @Getter @Setter @JsonProperty(value = "HOB greater than or equal to 30 degrees", index = 79)
  private String hobGreaterThanOrEqualTo30Degrees = "No";
  @Getter @Setter @JsonProperty(value = "Proton Pump Inhibitor / H2 Blocker", index = 80)
  private String protonPumpInhibitorH2Blocker = "No";
  @Getter @Setter @JsonProperty(value = "Ventilated", index = 81)
  private String ventilated = "No";
  @Getter @Setter @JsonProperty(value = "Ventilation Mode", index = 82)
  private String ventilationMode = "0";
  @Getter @Setter @JsonProperty(value = "Current Tidal Volume (millimeters)", index = 83)
  private String currentTidalVolumeMillimeters = "0";
  @Getter @Setter @JsonProperty(value = "Tracheostomy", index = 84)
  private String tracheostomy = "No";
  @Getter @Setter @JsonProperty(value = "Sub-glottic Suction ETT", index = 85)
  private String subGlotticSuctionEtt = "No";
  @Getter @Setter @JsonProperty(value = "Oral Care", index = 86)
  private String oralCare = "No";
  @Getter @Setter @JsonProperty(value = "In-line Suction", index = 87)
  private String inLineSuction = "No";
  @Getter @Setter @JsonProperty(value = "Daily Spontaneous Breathing Trial", index = 88)
  private String dailySpontaneousBreathingTrial = "0";
  @Getter @Setter @JsonProperty(value = "Line 1 Type", index = 89)
  private String line1Type = "No Line";
  @Getter @Setter @JsonProperty(value = "Line 1 Site", index = 90)
  private String line1Site = "N/A";
  @Getter @Setter @JsonProperty(value = "Line 1 Days", index = 91)
  private String line1Days = "0";
  @Getter @Setter @JsonProperty(value = "Line 1 Checklist", index = 92)
  private String line1Checklist = "N/A";
  @Getter @Setter @JsonProperty(value = "Line 1 US Guided", index = 93)
  private String line1UsGuided = "N/A";
  @Getter @Setter @JsonProperty(value = "Line 1 Operating Room Insertion", index = 94)
  private String line1OperatingRoomInsertion = "N/A";
  @Getter @Setter @JsonProperty(value = "Daily Needs Assessment", index = 95)
  private String dailyNeedsAssessment = "No";
  @Getter @Setter @JsonProperty(value = "Line 2 Type", index = 96)
  private String line2Type = "No Line";
  @Getter @Setter @JsonProperty(value = "Line 2 Site", index = 97)
  private String line2Site = "N/A";
  @Getter @Setter @JsonProperty(value = "Line 2 Days", index = 98)
  private String line2Days = "0";
  @Getter @Setter @JsonProperty(value = "Line 2 Checklist", index = 99)
  private String line2Checklist = "N/A";
  @Getter @Setter @JsonProperty(value = "Line 2 US Guided", index = 100)
  private String line2UsGuided = "N/A";
  @Getter @Setter @JsonProperty(value = "Line 2 Operating Room Insertion", index = 101)
  private String line2OperatingRoomInsertion = "N/A";
  @Getter @Setter @JsonProperty(value = "Line 3 Type", index = 102)
  private String line3Type = "No Line";
  @Getter @Setter @JsonProperty(value = "Line 3 Site", index = 103)
  private String line3Site = "N/A";
  @Getter @Setter @JsonProperty(value = "Line 3 Days", index = 104)
  private String line3Days = "0";
  @Getter @Setter @JsonProperty(value = "Line 3 Checklist", index = 105)
  private String line3Checklist = "N/A";
  @Getter @Setter @JsonProperty(value = "Line 3 US Guided", index = 106)
  private String line3UsGuided = "N/A";
  @Getter @Setter @JsonProperty(value = "Line 3 Operating Room Insertion", index = 107)
  private String line3OperatingRoomInsertion = "N/A";
  @Getter @Setter @JsonProperty(value = "IVC Filter", index = 108)
  private String ivcFilter = "No";
  @Getter @Setter @JsonProperty(value = "Anti-Coagulated", index = 109)
  private String antiCoagulated = "No";
  @Getter @Setter @JsonProperty(value = "Mechanical", index = 110)
  private String mechanical = "No: Not Ordered";
  @Getter @Setter @JsonProperty(value = "Pharmacologic", index = 111)
  private String pharmacologic = "No";
}
