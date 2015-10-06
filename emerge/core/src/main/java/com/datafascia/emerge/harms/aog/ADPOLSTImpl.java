// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.aog;

import ca.uhn.fhir.model.dstu2.resource.Flag;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ADPOLST;
import com.datafascia.emerge.ucsf.FlagUtils;
import com.datafascia.emerge.ucsf.codes.FlagCodeEnum;
import java.util.List;
import java.util.Optional;

/**
 * AD/POLST Implementation
 */
public class ADPOLSTImpl {

  /**
   * Alignment of Goals AD/POLST Implementation
   *
   * @param client
   *     API client
   * @param patientId
   *     patient to search
   * @return
   *     ADPOLST object if either active AD flag or active POLST flag exists, otherwise empty
   */
  public static Optional<ADPOLST> adPolst(ClientBuilder client, String patientId) {
    ADPOLST adPolst = new ADPOLST();

    List<Flag> ads = client.getFlagClient().searchFlag(
        patientId, FlagCodeEnum.ADVANCE_DIRECTIVE.getCode(), null);
    if (!ads.isEmpty()) {
      adPolst.setAdValue(FlagUtils.isActive(FlagUtils.findFreshestFlag(ads)));
    }

    List<Flag> polsts = client.getFlagClient().searchFlag(
        patientId, FlagCodeEnum.PHYSICIAN_ORDERS_FOR_LIFE_SUSTAINING_TREATMENT.getCode(), null);
    if (!polsts.isEmpty()) {
      adPolst.setPolstValue(FlagUtils.isActive(FlagUtils.findFreshestFlag(polsts)));
    }

    return (ads.isEmpty() && polsts.isEmpty()) ? Optional.empty() : Optional.of(adPolst);
  }
}
