// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.datafascia.models.Encounter;
import com.datafascia.models.Observation;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

/**
 * Test for Encounter DAO
 */
@Slf4j
public class EncounterDaoIT extends DaoIT {

  @Test
  public void encounters() throws Exception {
    log.info("Getting test encounters");
    EncounterDao dao = new EncounterDao(queryTemplate);

    SimpleDateFormat adminDateFormatter = new SimpleDateFormat("yyyyMMddHHmmssX");
    Encounter enco1 = dao.getEncounter("UCSF | SICU | a5096a63-09b2-4758-9eaf-6581e6541a06");
    assertValues(enco1, new BigDecimal("1.8"), new BigDecimal("15.75"), adminDateFormatter.parse("20150102145600-00"));

    Encounter enco2 = dao.getEncounter("UCSF | SICU | a5b5961c-5149-4dd6-8bdf-300cc51eb319");
    assertValues(enco2, new BigDecimal("68.04"), new BigDecimal("63"), adminDateFormatter.parse("20140211101700-00"));

    Encounter enco3 = dao.getEncounter("UCSF | SICU | a7042d31-7781-41ab-94b3-ad71de9179ab");
    assertValues(enco3, new BigDecimal("85.475"), new BigDecimal("66"), adminDateFormatter.parse("20141022110300-00"));
  }

  public void assertValues(Encounter enco, BigDecimal weight, BigDecimal height, Date date) {
    assertEquals(date, enco.getHospitalisation().getPeriod().getStart());

    boolean foundHeight = false;
    boolean foundWeight = false;
    for (Observation ob : enco.getObservations()) {
      if (ob.getName().getCode().equals(EncounterDao.WEIGHT)) {
        foundWeight = true;
        assertEquals(ob.getValues().getQuantity().getValue(), weight);
        assertEquals(ob.getValues().getQuantity().getUnits(), "kg");
      } else if (ob.getName().getCode().equals(EncounterDao.HEIGHT)) {
        foundHeight = true;
        assertEquals(ob.getValues().getQuantity().getValue(), height);
        assertEquals(ob.getValues().getQuantity().getUnits(), "in");
      }
    }

    assertTrue(foundHeight);
    assertTrue(foundWeight);
  }
}
