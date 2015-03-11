// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.models;

import com.datafascia.common.jackson.CSVMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for Daily Process model object
 */
@Slf4j
public class DailyProcessTest {
  String header = "Entry #,Date Created,Date Updated,IP Address,Data Collection Date,JHED ID,Subjec"
          + "t ID,Subject Patcom,Room Number,Daily Goal Sheet Present,Comfort Care Orders,Family Me"
          + "eting Note,Palliative Care Note,Pastoral Care Note,Social Work Note,Clinician Pain Goa"
          + "l,Clinician RASS Goal,Numerical Pain Level (Low),Numerical Pain Level (High),Behavior "
          + "Pain Level (Low),Behavior Pain Level (High),RASS Level (Low),RASS Level (High),CAM-ICU"
          + " Result,Epidural,Continuous Infusion Dexmedetomidine,Please list the current Dexmedeto"
          + "midine rate (mcg/kg/h),Please list the highest Dexmedetomidine rate 24 hours (mcg/kg/h"
          + "),Continuous Infusion Fentanyl,Please list the current Fentanyl rate (mcg/h),Please li"
          + "st the highest Fentanyl rate 24 hours (mcg/h),Continuous Infusion Lorazepam,Please lis"
          + "t the current Lorazepam rate (mg/h),Please list the highest Lorazepam rate 24 hours (m"
          + "g/h),Continuous Infusion Midazolam,Please list the current Midazolam rate (mg/h),Pleas"
          + "e list the highest Midazolam rate 24 hours (mg/h),Continuous Infusion Morphine,Please "
          + "list the current Morphine rate (mg/h),Please list the highest Morphine rate 24 hours ("
          + "mg/h),Continuous Infusion Dilaudid,Please list the current Dilaudid rate (mg/h),Please"
          + " list the highest Dilaudid rate 24 hours (mg/h),Continuous Infusion Propofol,Please li"
          + "st the current Propofol rate (mcg/kg/min),Please list the highest Propofol rate 24 hou"
          + "rs (mcg/kg/min),Daily Sedation Interruption Candidate,Daily Sedation Interruption,Inte"
          + "rmittent Chlordiazepoxide,Please list the Chlordiazepoxide Dose (mg/day),Intermittent "
          + "Fentanyl IV,Please list the Fentanyl IV Dose (mcg/day),Intermittent Haloperidol IV,Ple"
          + "ase list the Haloperidol IV Dose (mg/day),Intermittent Haloperidol PO,Please list the "
          + "Haloperidol PO Dose (mg/day),Intermittent Hydromorphone IV,Please list the Hydromorpho"
          + "ne IV Dose (mg/day),Intermittent Hydromorphone PO,Please list the Hydromorphone PO Dos"
          + "e (mg/day),Intermittent Lorazepam IV,Please list the Lorazepam IV Dose (mg/day),Interm"
          + "ittent Lorazepam PO,Please list the Lorazepam PO Dose (mg/day),Intermittent Midazolam "
          + "IV,Please list the Midazolam IV Dose (mg/day),Intermittent Morphine IV,Please list the"
          + " Morphine IV Dose (mg/day),Intermittent Morphine PO,Please list the Morphine PO Dose ("
          + "mg/day),Intermittent Olanzapine PO,Please list the Olanzapine PO Dose (mg/day),Intermi"
          + "ttent Oxycodone,Please list the Oxycodone Dose (mg/day),Intermittent Quetiapine PO,Ple"
          + "ase list the Quetiapine Dose (mg/day),Highest Level Activity,Number of Assists,Assist "
          + "Devices,HOB greater than or equal to 30 degrees,Proton Pump Inhibitor / H2 Blocker,Ven"
          + "tilated,Ventilation Mode,Current Tidal Volume (millimeters),Tracheostomy,Sub-glottic S"
          + "uction ETT,Oral Care,In-line Suction,Daily Spontaneous Breathing Trial,Line 1 Type,Lin"
          + "e 1 Site,Line 1 Days,Line 1 Checklist,Line 1 US Guided,Line 1 Operating Room Insertion"
          + ",Daily Needs Assessment,Line 2 Type,Line 2 Site,Line 2 Days,Line 2 Checklist,Line 2 US"
          + " Guided,Line 2 Operating Room Insertion,Line 3 Type,Line 3 Site,Line 3 Days,Line 3 Che"
          + "cklist,Line 3 US Guided,Line 3 Operating Room Insertion,IVC Filter,Anti-Coagulated,Mec"
          + "hanical,Pharmacologic";
// Test data: these lines only differ by the spurious1 entry
  String testLine1 = "99000,2014-02-26 18:23:55,2014-02-26 18:23:55,255.255.255.255,2525-02-31,spur"
          + "ious1,9000,P-Male76 kg,FAKE-050-A,Not Available,No,No,No,No,No,11,11,0,0,11,11,0,0,Pos"
          + "itive,No,No,0,0,Yes,300,300,No,0,0,No,0,0,No,0,0,No,0,0,Yes,30,30,Yes,Yes,No,0,Yes,175"
          + ",No,0,No,0,No,0,No,0,No,0,No,0,No,0,No,0,No,0,No,0,No,0,No,0,Lying in bed,2,Reposition"
          + "ing Aid,No: Spine Precautions,Yes,Yes,Synchronous Intermittent Mandatory Ventilation ("
          + "SIMV),400,No,Yes,Yes,Yes,Not Given,Non-tunneled Catheter,Subclavian  (Left),2,Yes,Not "
          + "Documented,No,N/A,No Line,N/A,0,N/A,N/A,N/A,No Line,N/A,0,N/A,N/A,N/A,No,No,Yes,Contra"
          + "indicated: Active Bleeding";
  String testLine2 = "99000,2014-02-26 18:23:55,2014-02-26 18:23:55,255.255.255.255,2525-02-31,\"Sp"
          + "ur,ious1\",9000,P-Male76 kg,FAKE-050-A,Not Available,No,No,No,No,No,11,11,0,0,11,11,0,"
          + "0,Positive,No,No,0,0,Yes,300,300,No,0,0,No,0,0,No,0,0,No,0,0,Yes,30,30,Yes,Yes,No,0,Ye"
          + "s,175,No,0,No,0,No,0,No,0,No,0,No,0,No,0,No,0,No,0,No,0,No,0,No,0,Lying in bed,2,Repos"
          + "itioning Aid,No: Spine Precautions,Yes,Yes,Synchronous Intermittent Mandatory Ventilat"
          + "ion (SIMV),400,No,Yes,Yes,Yes,Not Given,Non-tunneled Catheter,Subclavian  (Left),2,Yes"
          + ",Not Documented,No,N/A,No Line,N/A,0,N/A,N/A,N/A,No Line,N/A,0,N/A,N/A,N/A,No,No,Yes,C"
          + "ontraindicated: Active Bleeding";
  String testLine3 = "99000,2014-02-26 18:23:55,2014-02-26 18:23:55,255.255.255.255,2525-02-31,\"\""
          + "\"Spur,ious1\"\"\",9000,P-Male76 kg,FAKE-050-A,Not Available,No,No,No,No,No,11,11,0,0,"
          + "11,11,0,0,Positive,No,No,0,0,Yes,300,300,No,0,0,No,0,0,No,0,0,No,0,0,Yes,30,30,Yes,Yes"
          + ",No,0,Yes,175,No,0,No,0,No,0,No,0,No,0,No,0,No,0,No,0,No,0,No,0,No,0,No,0,Lying in bed"
          + ",2,Repositioning Aid,No: Spine Precautions,Yes,Yes,Synchronous Intermittent Mandatory "
          + "Ventilation (SIMV),400,No,Yes,Yes,Yes,Not Given,Non-tunneled Catheter,Subclavian  (Lef"
          + "t),2,Yes,Not Documented,No,N/A,No Line,N/A,0,N/A,N/A,N/A,No Line,N/A,0,N/A,N/A,N/A,No,"
          + "No,Yes,Contraindicated: Active Bleeding";

  CSVMapper<DailyProcess> mapper;

  @BeforeClass
  public void setup() {
    mapper = new CSVMapper<>(DailyProcess.class);
  }

  @Test
  public void deserialisation() throws IOException {
    assertEquals(mapper.fromCSV(testLine1), getDailyProcess());
  }

  @Test
  public void quoteDeserialisation() throws IOException {
    DailyProcess dpInstance = getDailyProcess();
    dpInstance.setJhedId("\"Spur,ious1\"");
    assertEquals(mapper.fromCSV(testLine3), dpInstance);
  }

  @Test
  public void commaDeserialisation() throws IOException {
    DailyProcess dpInstance = getDailyProcess();
    dpInstance.setJhedId("Spur,ious1");
    assertEquals(mapper.fromCSV(testLine2), dpInstance);
  }

  @Test
  public void header() throws JsonProcessingException {
    String prefix = "";
    StringBuilder sb = new StringBuilder();
    for (String s : mapper.getHeaders()) {
      sb.append(prefix);
      sb.append(s);
      prefix = ",";
    }
    assertEquals(sb.toString(), header);
  }

  @Test
  public void quoteSerialisation() throws JsonProcessingException {
    DailyProcess dpInstance = getDailyProcess();
    dpInstance.setJhedId("\"Spur,ious1\"");
    assertEquals(mapper.asCSV(dpInstance), testLine3);
  }

  @Test
  public void commaSerialisation() throws JsonProcessingException {
    DailyProcess dpInstance = getDailyProcess();
    dpInstance.setJhedId("Spur,ious1");
    assertEquals(mapper.asCSV(dpInstance), testLine2);
  }

  @Test
  public void serialisation() throws JsonProcessingException {
    assertEquals(mapper.asCSV(getDailyProcess()), testLine1);
  }

  /**
   * Create test daily process object. same data as testLine1.
   */
  private DailyProcess getDailyProcess() {
    DailyProcess dpInstance = new DailyProcess();
    dpInstance.setEntry("99000");
    dpInstance.setDateCreated("2014-02-26 18:23:55");
    dpInstance.setDateUpdated("2014-02-26 18:23:55");
    dpInstance.setIpAddress("255.255.255.255");
    dpInstance.setDataCollectionDate("2525-02-31");
    dpInstance.setJhedId("spurious1");
    dpInstance.setSubjectId("9000");
    dpInstance.setSubjectPatcom("P-Male76 kg");
    dpInstance.setRoomNumber("FAKE-050-A");
    dpInstance.setDailyGoalSheetPresent("Not Available");
    dpInstance.setComfortCareOrders("No");
    dpInstance.setFamilyMeetingNote("No");
    dpInstance.setPalliativeCareNote("No");
    dpInstance.setPastoralCareNote("No");
    dpInstance.setSocialWorkNote("No");
    dpInstance.setClinicianPainGoal("11");
    dpInstance.setClinicianRassGoal("11");
    dpInstance.setNumericalPainLevelLow("0");
    dpInstance.setNumericalPainLevelHigh("0");
    dpInstance.setBehaviorPainLevelLow("11");
    dpInstance.setBehaviorPainLevelHigh("11");
    dpInstance.setRassLevelLow("0");
    dpInstance.setRassLevelHigh("0");
    dpInstance.setCamIcuResult("Positive");
    dpInstance.setEpidural("No");
    dpInstance.setContinuousInfusionDexmedetomidine("No");
    dpInstance.setPleaseListTheCurrentDexmedetomidineRateMcgKgH("0");
    dpInstance.setPleaseListTheHighestDexmedetomidineRate24HoursMcgKgH("0");
    dpInstance.setContinuousInfusionFentanyl("Yes");
    dpInstance.setPleaseListTheCurrentFentanylRateMcgH("300");
    dpInstance.setPleaseListTheHighestFentanylRate24HoursMcgH("300");
    dpInstance.setContinuousInfusionLorazepam("No");
    dpInstance.setPleaseListTheCurrentLorazepamRateMgH("0");
    dpInstance.setPleaseListTheHighestLorazepamRate24HoursMgH("0");
    dpInstance.setContinuousInfusionMidazolam("No");
    dpInstance.setPleaseListTheCurrentMidazolamRateMgH("0");
    dpInstance.setPleaseListTheHighestMidazolamRate24HoursMgH("0");
    dpInstance.setContinuousInfusionMorphine("No");
    dpInstance.setPleaseListTheCurrentMorphineRateMgH("0");
    dpInstance.setPleaseListTheHighestMorphineRate24HoursMgH("0");
    dpInstance.setContinuousInfusionDilaudid("No");
    dpInstance.setPleaseListTheCurrentDilaudidRateMgH("0");
    dpInstance.setPleaseListTheHighestDilaudidRate24HoursMgH("0");
    dpInstance.setContinuousInfusionPropofol("Yes");
    dpInstance.setPleaseListTheCurrentPropofolRateMcgKgMin("30");
    dpInstance.setPleaseListTheHighestPropofolRate24HoursMcgKgMin("30");
    dpInstance.setDailySedationInterruptionCandidate("Yes");
    dpInstance.setDailySedationInterruption("Yes");
    dpInstance.setIntermittentChlordiazepoxide("No");
    dpInstance.setPleaseListTheChlordiazepoxideDoseMgDay("0");
    dpInstance.setIntermittentFentanylIv("Yes");
    dpInstance.setPleaseListTheFentanylIvDoseMcgDay("175");
    dpInstance.setIntermittentHaloperidolIv("No");
    dpInstance.setPleaseListTheHaloperidolIvDoseMgDay("0");
    dpInstance.setIntermittentHaloperidolPo("No");
    dpInstance.setPleaseListTheHaloperidolPoDoseMgDay("0");
    dpInstance.setIntermittentHydromorphoneIv("No");
    dpInstance.setPleaseListTheHydromorphoneIvDoseMgDay("0");
    dpInstance.setIntermittentHydromorphonePo("No");
    dpInstance.setPleaseListTheHydromorphonePoDoseMgDay("0");
    dpInstance.setIntermittentLorazepamIv("No");
    dpInstance.setPleaseListTheLorazepamIvDoseMgDay("0");
    dpInstance.setIntermittentLorazepamPo("No");
    dpInstance.setPleaseListTheLorazepamPoDoseMgDay("0");
    dpInstance.setIntermittentMidazolamIv("No");
    dpInstance.setPleaseListTheMidazolamIvDoseMgDay("0");
    dpInstance.setIntermittentMorphineIv("No");
    dpInstance.setPleaseListTheMorphineIvDoseMgDay("0");
    dpInstance.setIntermittentMorphinePo("No");
    dpInstance.setPleaseListTheMorphinePoDoseMgDay("0");
    dpInstance.setIntermittentOlanzapinePo("No");
    dpInstance.setPleaseListTheOlanzapinePoDoseMgDay("0");
    dpInstance.setIntermittentOxycodone("No");
    dpInstance.setPleaseListTheOxycodoneDoseMgDay("0");
    dpInstance.setIntermittentQuetiapinePo("No");
    dpInstance.setPleaseListTheQuetiapineDoseMgDay("0");
    dpInstance.setHighestLevelActivity("Lying in bed");
    dpInstance.setNumberOfAssists("2");
    dpInstance.setAssistDevices("Repositioning Aid");
    dpInstance.setHobGreaterThanOrEqualTo30Degrees("No: Spine Precautions");
    dpInstance.setProtonPumpInhibitorH2Blocker("Yes");
    dpInstance.setVentilated("Yes");
    dpInstance.setVentilationMode("Synchronous Intermittent Mandatory Ventilation (SIMV)");
    dpInstance.setCurrentTidalVolumeMillimeters("400");
    dpInstance.setTracheostomy("No");
    dpInstance.setSubGlotticSuctionEtt("Yes");
    dpInstance.setOralCare("Yes");
    dpInstance.setInLineSuction("Yes");
    dpInstance.setDailySpontaneousBreathingTrial("Not Given");
    dpInstance.setLine1Type("Non-tunneled Catheter");
    dpInstance.setLine1Site("Subclavian  (Left)");
    dpInstance.setLine1Days("2");
    dpInstance.setLine1Checklist("Yes");
    dpInstance.setLine1UsGuided("Not Documented");
    dpInstance.setLine1OperatingRoomInsertion("No");
    dpInstance.setDailyNeedsAssessment("N/A");
    dpInstance.setLine2Type("No Line");
    dpInstance.setLine2Site("N/A");
    dpInstance.setLine2Days("0");
    dpInstance.setLine2Checklist("N/A");
    dpInstance.setLine2UsGuided("N/A");
    dpInstance.setLine2OperatingRoomInsertion("N/A");
    dpInstance.setLine3Type("No Line");
    dpInstance.setLine3Site("N/A");
    dpInstance.setLine3Days("0");
    dpInstance.setLine3Checklist("N/A");
    dpInstance.setLine3UsGuided("N/A");
    dpInstance.setLine3OperatingRoomInsertion("N/A");
    dpInstance.setIvcFilter("No");
    dpInstance.setAntiCoagulated("No");
    dpInstance.setMechanical("Yes");
    dpInstance.setPharmacologic("Contraindicated: Active Bleeding");

    return dpInstance;
  }

  /**
   * Create test daily process object with default values.
   */
  private DailyProcess getDailyProcessWithDefaults() {
    return new DailyProcess();
  }

  @Test
  public void defaults() {
    DailyProcess dpInstance = getDailyProcessWithDefaults();

    assertEquals(dpInstance.getDailyGoalSheetPresent(), "Not Available");
    assertEquals(dpInstance.getComfortCareOrders(), "No");
    assertEquals(dpInstance.getFamilyMeetingNote(), "No");
    assertEquals(dpInstance.getPalliativeCareNote(), "No");
    assertEquals(dpInstance.getPastoralCareNote(), "No");
    assertEquals(dpInstance.getSocialWorkNote(), "No");
    assertEquals(dpInstance.getClinicianPainGoal(), "11");
    assertEquals(dpInstance.getClinicianRassGoal(), "11");
    assertEquals(dpInstance.getNumericalPainLevelLow(), "11");
    assertEquals(dpInstance.getNumericalPainLevelHigh(), "11");
    assertEquals(dpInstance.getBehaviorPainLevelLow(), "11");
    assertEquals(dpInstance.getBehaviorPainLevelHigh(), "11");
    assertEquals(dpInstance.getRassLevelLow(), "11");
    assertEquals(dpInstance.getRassLevelHigh(), "11");
    assertEquals(dpInstance.getCamIcuResult(), "Not Completed");
    assertEquals(dpInstance.getEpidural(), "No");
    assertEquals(dpInstance.getContinuousInfusionDexmedetomidine(), "No");
    assertEquals(dpInstance.getContinuousInfusionFentanyl(), "No");
    assertEquals(dpInstance.getContinuousInfusionLorazepam(), "No");
    assertEquals(dpInstance.getContinuousInfusionMidazolam(), "No");
    assertEquals(dpInstance.getContinuousInfusionMorphine(), "No");
    assertEquals(dpInstance.getContinuousInfusionDilaudid(), "No");
    assertEquals(dpInstance.getContinuousInfusionPropofol(), "No");
    assertEquals(dpInstance.getDailySedationInterruptionCandidate(), "No: Off Sedation");
    assertEquals(dpInstance.getDailySedationInterruption(), "Not Applicable");
    assertEquals(dpInstance.getIntermittentChlordiazepoxide(), "No");
    assertEquals(dpInstance.getIntermittentFentanylIv(), "No");
    assertEquals(dpInstance.getIntermittentHaloperidolIv(), "No");
    assertEquals(dpInstance.getIntermittentHaloperidolPo(), "No");
    assertEquals(dpInstance.getIntermittentHydromorphoneIv(), "No");
    assertEquals(dpInstance.getIntermittentHydromorphonePo(), "No");
    assertEquals(dpInstance.getIntermittentLorazepamIv(), "No");
    assertEquals(dpInstance.getIntermittentLorazepamPo(), "No");
    assertEquals(dpInstance.getIntermittentMidazolamIv(), "No");
    assertEquals(dpInstance.getIntermittentMorphineIv(), "No");
    assertEquals(dpInstance.getIntermittentMorphinePo(), "No");
    assertEquals(dpInstance.getIntermittentOlanzapinePo(), "No");
    assertEquals(dpInstance.getIntermittentOxycodone(), "No");
    assertEquals(dpInstance.getIntermittentQuetiapinePo(), "No");
    assertEquals(dpInstance.getHighestLevelActivity(), "Not Documented");
    assertEquals(dpInstance.getNumberOfAssists(), "Not Documented");
    assertEquals(dpInstance.getAssistDevices(), "Not Documented");
    assertEquals(dpInstance.getHobGreaterThanOrEqualTo30Degrees(), "No");
    assertEquals(dpInstance.getProtonPumpInhibitorH2Blocker(), "No");
    assertEquals(dpInstance.getVentilated(), "No");
    assertEquals(dpInstance.getVentilationMode(), "0");
    assertEquals(dpInstance.getCurrentTidalVolumeMillimeters(), "0");
    assertEquals(dpInstance.getTracheostomy(), "No");
    assertEquals(dpInstance.getSubGlotticSuctionEtt(), "No");
    assertEquals(dpInstance.getOralCare(), "No");
    assertEquals(dpInstance.getInLineSuction(), "No");
    assertEquals(dpInstance.getDailySpontaneousBreathingTrial(), "0");
    assertEquals(dpInstance.getLine1Type(), "No Line");
    assertEquals(dpInstance.getLine1Site(), "N/A");
    assertEquals(dpInstance.getLine1Days(), "0");
    assertEquals(dpInstance.getLine1Checklist(), "N/A");
    assertEquals(dpInstance.getLine1UsGuided(), "N/A");
    assertEquals(dpInstance.getLine1OperatingRoomInsertion(), "N/A");
    assertEquals(dpInstance.getDailyNeedsAssessment(), "No");
    assertEquals(dpInstance.getLine2Type(), "No Line");
    assertEquals(dpInstance.getLine2Site(), "N/A");
    assertEquals(dpInstance.getLine2Days(), "0");
    assertEquals(dpInstance.getLine2Checklist(), "N/A");
    assertEquals(dpInstance.getLine2UsGuided(), "N/A");
    assertEquals(dpInstance.getLine2OperatingRoomInsertion(), "N/A");
    assertEquals(dpInstance.getLine3Type(), "No Line");
    assertEquals(dpInstance.getLine3Site(), "N/A");
    assertEquals(dpInstance.getLine3Days(), "0");
    assertEquals(dpInstance.getLine3Checklist(), "N/A");
    assertEquals(dpInstance.getLine3UsGuided(), "N/A");
    assertEquals(dpInstance.getLine3OperatingRoomInsertion(), "N/A");
    assertEquals(dpInstance.getIvcFilter(), "No");
    assertEquals(dpInstance.getAntiCoagulated(), "No");
    assertEquals(dpInstance.getMechanical(), "No: Not Ordered");
    assertEquals(dpInstance.getPharmacologic(), "No");
  }
}
