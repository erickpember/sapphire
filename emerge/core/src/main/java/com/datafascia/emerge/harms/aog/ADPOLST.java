// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.aog;

import ca.uhn.fhir.model.dstu2.resource.Flag;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.FlagUtils;
import com.datafascia.emerge.ucsf.codes.FlagCodeEnum;
import java.util.List;

/**
 * AD/POLST Implementation
 */
public class ADPOLST {

  /**
   * Alignment of Goals AD/POLST Implementation
   *
   * @param client
   *     API client
   * @param patientId
   *     patient to search
   * @return
   *     true if active AD or POLST flag, otherwise false
   **/
  public static boolean adPolst(ClientBuilder client,
      String patientId) {
    List<Flag> ads = client.getFlagClient().searchFlag(patientId,
        FlagCodeEnum.ADVANCE_DIRECTIVE.getCode(), null);
    List<Flag> polsts = client.getFlagClient().searchFlag(patientId,
        FlagCodeEnum.PHYSICIAN_ORDERS_FOR_LIFE_SUSTAINING_TREATMENT.getCode(), null);

    Flag freshestAD = FlagUtils.findFreshestFlag(ads);
    Flag freshestPOLST = FlagUtils.findFreshestFlag(polsts);

    return (FlagUtils.isActive(freshestAD) || FlagUtils.isActive(freshestPOLST));
  }
}
