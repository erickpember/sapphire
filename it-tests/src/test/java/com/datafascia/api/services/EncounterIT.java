// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.datafascia.models.Encounter;
import com.datafascia.models.Observation;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Integration tests for encounter resources
 */
@Slf4j
public class EncounterIT extends ApiIT {
  /**
   * Fetch a few encounters and validate them.
   *
   * @throws Exception
   */
  @Test
  public void testEncounter() throws Exception {
    // Test direct encounter query.
    validateEncounter(api.encounter("UCSF |  | 039ae46a-20a1-4bcd-abb9-68e38d4222c0"),
        new BigDecimal("4.3"), "kg", new BigDecimal("20.98"), "in",
        LocalDateTime.parse("2014-11-19T10:00:00Z", dateFormat).toInstant(ZoneOffset.UTC));

    validateEncounter(api.encounter("UCSF |  | 0728eb62-2f16-484f-8628-a320e99c635d"),
        new BigDecimal("72.576"), "kg", new BigDecimal("62.99"), "in",
        LocalDateTime.parse("2014-11-24T11:39:07Z", dateFormat).toInstant(ZoneOffset.UTC));

    // Test getting the last encounter for a patient.
    validateEncounter(api.lastvisit("97540012"),
        new BigDecimal("4.99"), "kg", new BigDecimal("23"), "in",
        LocalDateTime.parse("2014-08-04T10:51:00Z", dateFormat).toInstant(ZoneOffset.UTC));
  }

  /**
   * Validate an encounter against expected values.
   * @param admitDate admit date+time for encounter
   */
  public void validateEncounter(Encounter enco, BigDecimal weight, String weightUnits,
      BigDecimal height, String heightUnits, Instant admitDate) {
    boolean foundHeight = false;
    boolean foundWeight = false;

    for (Observation ob : enco.getObservations()) {
      switch (ob.getName().getCode()) {
        case "Weight":
          foundWeight = true;
          assertEquals(ob.getValues().getQuantity().getValue(), weight);
          assertEquals(ob.getValues().getQuantity().getUnits(), "kg");
          break;
        case "Height":
          foundHeight = true;
          assertEquals(ob.getValues().getQuantity().getValue(), height);
          assertEquals(ob.getValues().getQuantity().getUnits(), "in");
          break;
      }
    }

    assertTrue(foundWeight);
    assertTrue(foundHeight);
    assertEquals(admitDate, enco.getHospitalisation().getPeriod().getStart());
  }

}
