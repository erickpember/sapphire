// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
