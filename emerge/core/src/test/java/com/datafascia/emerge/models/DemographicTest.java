// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.models;

import com.datafascia.csv.CSVMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for Demographic model object
 */
@Slf4j
public class DemographicTest {
  String header = "Entry #,Date Created,Date Updated,IP Address,Data Collection Date,JHED ID,"
          + "Subject Patient ID,Subject Patcom,Patient Name,SICU Admission Date,"
          + "Readmission,Patient Date of Birth,Gender,Race,Patient Admission Weight (kg),"
          + "Patient Admission Height (cm),Prior to Hospital Stay,Highest-level Activity,"
          + "Screening Tool Used,IVC Filter";
  // Test data
  String testLine1 = "565,2014-02-18 07:37:46,2014-05-20 12:24:48,10.21.1.59,2014-02-18,dmcneli1,"
          + "Male170 cm109 kg,P-Male170 cm109 kg,Jud Joe Johnson,2014-05-13,No,"
          + "1969-01-02,Male,Black/African Am,109 kg,170 cm,NULL,NULL,Yes,No";
  String testLine2 = "565,2014-02-18 07:37:46,2014-05-20 12:24:48,10.21.1.59,2014-02-18,dmcneli1,"
          + "Male170 cm109 kg,P-Male170 cm109 kg,\"Johnson, Jud\",2014-05-13,No,"
          + "1969-01-02,Male,Black/African Am,109 kg,170 cm,NULL,NULL,Yes,No";
  String testLine3 = "565,2014-02-18 07:37:46,2014-05-20 12:24:48,10.21.1.59,2014-02-18,dmcneli1,"
          + "Male170 cm109 kg,P-Male170 cm109 kg,\"\"\"Johnson, Jud\"\"\",2014-05-13,No,"
          + "1969-01-02,Male,Black/African Am,109 kg,170 cm,NULL,NULL,Yes,No";

  CSVMapper<Demographic> mapper;

  @BeforeClass
  public void setup() {
    mapper = new CSVMapper<>(Demographic.class);
  }

  @Test
  public void deserialisation() throws IOException {
    assertEquals(mapper.fromCSV(testLine1), getDemographic());
  }

  @Test
  public void quoteDeserialisation() throws IOException {
    Demographic dmg = getDemographic();
    dmg.setPatientName("\"Johnson, Jud\"");
    assertEquals(mapper.fromCSV(testLine3), dmg);
  }

  @Test
  public void commaDeserialisation() throws IOException {
    Demographic dmg = getDemographic();
    dmg.setPatientName("Johnson, Jud");
    assertEquals(mapper.fromCSV(testLine2), dmg);
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
    Demographic dmg = getDemographic();
    dmg.setPatientName("\"Johnson, Jud\"");
    assertEquals(mapper.asCSV(dmg), testLine3);
  }

  @Test
  public void commaSerialisation() throws JsonProcessingException {
    Demographic dmg = getDemographic();
    dmg.setPatientName("Johnson, Jud");
    assertEquals(mapper.asCSV(dmg), testLine2);
  }

  @Test
  public void serialisation() throws JsonProcessingException {
    assertEquals(mapper.asCSV(getDemographic()), testLine1);
  }

  /**
   * Create test demographic object
   */
  private Demographic getDemographic() {
    Demographic dmg = new Demographic();
    dmg.setEntry("565");
    dmg.setDateCreated("2014-02-18 07:37:46");
    dmg.setDateUpdated("2014-05-20 12:24:48");
    dmg.setIpAddress("10.21.1.59");
    dmg.setDataCollectionDate("2014-02-18");
    dmg.setJhedId("dmcneli1");
    dmg.setSubjectPatientId("Male170 cm109 kg");
    dmg.setSubjectPatcom("P-Male170 cm109 kg");
    dmg.setPatientName("Jud Joe Johnson");
    dmg.setSicuAdmissionDate("2014-05-13");
    dmg.setReadmission("No");
    dmg.setPatientDateOfBirth("1969-01-02");
    dmg.setGender("Male");
    dmg.setRace("Black/African Am");
    dmg.setPatientAdmissionWeightKg("109 kg");
    dmg.setPatientAdmissionHeightCm("170 cm");
    dmg.setPriorToHospitalStay("NULL");
    dmg.setHighestLevelActivity("NULL");
    dmg.setScreeningToolUsed("Yes");
    dmg.setIvcFilter("No");

    return dmg;
  }
}
