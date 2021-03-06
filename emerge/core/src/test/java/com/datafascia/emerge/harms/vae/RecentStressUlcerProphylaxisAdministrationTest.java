// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.testUtils.TestResources;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests Stress Ulcer Prophylaxis Administration without the API.
 */
public class RecentStressUlcerProphylaxisAdministrationTest
    extends RecentStressUlcerProphylaxisAdministration {

  public RecentStressUlcerProphylaxisAdministrationTest() {
  }

  /**
   * Test of test method, of class RecentStressUlcerProphylaxisAdministration.
   */
  @Test
  public void testTest_4args() {
    ClientBuilder apiClient = TestResources.createMockClient();

    MedicationAdministration supPassActiveOrder = TestResources.createMedicationAdministration(
        "id",
        Arrays.asList(MedsSetEnum.STRESS_ULCER_PROPHYLACTICS.getCode()),
        MedicationAdministrationStatusEnum.IN_PROGRESS,
        1,
        "mg/kg",
        new DateTimeDt(Date.from(Instant.now().minus(20, ChronoUnit.HOURS))),
        "activeOrder");

    assertTrue(test(Arrays.asList(supPassActiveOrder), "encounterId", Instant.now(),
        apiClient));

    MedicationAdministration supFailOldCompletedOrder = TestResources
        .createMedicationAdministration(
            "id",
            Arrays.asList(MedsSetEnum.STRESS_ULCER_PROPHYLACTICS.getCode()),
            MedicationAdministrationStatusEnum.IN_PROGRESS,
            1,
            "mg/kg",
            new DateTimeDt(Date.from(Instant.now().minus(30, ChronoUnit.HOURS))),
            "completedOrder");

    assertFalse(test(Arrays.asList(supFailOldCompletedOrder), "encounterId", Instant.now(),
        apiClient));

    MedicationAdministration supFailReasonNotGiven = TestResources.createMedicationAdministration(
        "id",
        Arrays.asList(MedsSetEnum.STRESS_ULCER_PROPHYLACTICS.getCode()),
        MedicationAdministrationStatusEnum.IN_PROGRESS,
        1,
        "mg/kg",
        new DateTimeDt(Date.from(Instant.now().minus(2, ChronoUnit.HOURS))),
        "completedOrder");
    supFailReasonNotGiven.setReasonNotGiven(Arrays.asList(new CodeableConceptDt("whatever man",
        "i didn't feel like it")));

    assertFalse(test(Arrays.asList(supFailReasonNotGiven), "encounterId", Instant.now(),
        apiClient));

    MedicationAdministration supPassNewAdmin = TestResources.createMedicationAdministration(
        "id",
        Arrays.asList(MedsSetEnum.STRESS_ULCER_PROPHYLACTICS.getCode()),
        MedicationAdministrationStatusEnum.IN_PROGRESS,
        1,
        "mg/kg",
        new DateTimeDt(Date.from(Instant.now().minus(3, ChronoUnit.HOURS))),
        "completedOrder");

    assertTrue(test(Arrays.asList(supPassNewAdmin), "encounterId", Instant.now(),
        apiClient));

    assertTrue(test(Arrays.asList(supPassNewAdmin, supFailReasonNotGiven),
        "encounterId", Instant.now(), apiClient));
  }
}
