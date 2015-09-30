// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.emerge.ucsf.codes.FlagCodeEnum;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Tests {@link HL7MessageProcessor} saves Flag resources.
 */
@Test(singleThreaded = true)
public class FlagTest extends HL7MessageProcessorTestSupport {

  private boolean flagExists(FlagCodeEnum flagCode) {
    Id<UnitedStatesPatient> patientId = Id.of("97546762");
    return flagRepository.list(patientId)
        .stream()
        .anyMatch(flag -> flag.getCode().getCodingFirstRep().getCode().equals(flagCode.getCode()));
  }

  private void assertFlagExists(FlagCodeEnum flagCode) {
    assertTrue(flagExists(flagCode));
  }

  @Test
  public void should_create_flag_advance_directive() throws Exception {
    processMessage("flag-advance-directive.hl7");

    assertFlagExists(FlagCodeEnum.ADVANCE_DIRECTIVE);
  }

  @Test
  public void should_create_flag_physician_orders_for_life_sustaining_treatment() throws Exception {
    processMessage("flag-physician-orders-for-life-sustaining-treatment.hl7");

    assertFlagExists(FlagCodeEnum.PHYSICIAN_ORDERS_FOR_LIFE_SUSTAINING_TREATMENT);
  }

  @Test
  public void should_create_flag_patient_care_conference_note() throws Exception {
    processMessage("flag-patient-care-conference-note.hl7");

    assertFlagExists(FlagCodeEnum.PATIENT_CARE_CONFERENCE_NOTE);
  }
}
