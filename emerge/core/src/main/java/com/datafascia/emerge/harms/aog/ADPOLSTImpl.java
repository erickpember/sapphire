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
package com.datafascia.emerge.harms.aog;

import ca.uhn.fhir.model.dstu2.resource.Flag;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.FlagUtils;
import com.datafascia.emerge.ucsf.codes.FlagCodeEnum;
import java.util.List;
import javax.inject.Inject;

/**
 * AD/POLST Implementation
 */
public class ADPOLSTImpl {

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if an active advance directive exists for the patient.
   *
   * @param patientId
   *     patient to search
   * @return true if found
   */
  public boolean haveAdvanceDirective(String patientId) {
    List<Flag> ads = apiClient.getFlagClient().searchFlag(
        patientId, FlagCodeEnum.ADVANCE_DIRECTIVE.getCode(), null);
    if (!ads.isEmpty()) {
      return FlagUtils.isActive(FlagUtils.findFreshestFlag(ads));
    }
    return false;
  }

  /**
   * Checks if an active physician orders for life sustaining treatment exists for the patient.
   *
   * @param patientId
   *     patient to search
   * @return true if found
   */
  public boolean havePhysicianOrdersForLifeSustainingTreatment(String patientId) {
    List<Flag> polsts = apiClient.getFlagClient().searchFlag(
        patientId, FlagCodeEnum.PHYSICIAN_ORDERS_FOR_LIFE_SUSTAINING_TREATMENT.getCode(), null);
    if (!polsts.isEmpty()) {
      return FlagUtils.isActive(FlagUtils.findFreshestFlag(polsts));
    }
    return false;
  }
}
