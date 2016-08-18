// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import java.util.Comparator;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * MedicationAdministration effective comparator test
 */
public class MedicationAdministrationEffectiveTimeComparatorTest {

  private MedicationAdministration createMedicationAdministration(DateTimeDt date) {
    return new MedicationAdministration()
        .setEffectiveTime(date);
  }

  @Test
  public void testCompare() {
    MedicationAdministration stale =
        createMedicationAdministration(new DateTimeDt("2014-01-26T11:11:11"));
    MedicationAdministration fresh =
        createMedicationAdministration(new DateTimeDt("2014-01-26T12:12:12"));
    MedicationAdministration nullAdmin = null;
    MedicationAdministration emptyEffective = new MedicationAdministration()
        .setEffectiveTime(new DateTimeDt());

    Comparator<MedicationAdministration> comparator =
        MedicationAdministrationUtils.getEffectiveTimeComparator();

    assertEquals(comparator.compare(fresh, stale), 1);
    assertEquals(comparator.compare(fresh, fresh), 0);
    assertEquals(comparator.compare(stale, fresh), -1);
    assertEquals(comparator.compare(nullAdmin, nullAdmin), 0);
    assertEquals(comparator.compare(stale, nullAdmin), 1);
    assertEquals(comparator.compare(nullAdmin, fresh), -1);
    assertEquals(comparator.compare(emptyEffective, nullAdmin), 1);
    assertEquals(comparator.compare(stale, emptyEffective), 1);
    assertEquals(comparator.compare(emptyEffective, fresh), -1);
  }
}
