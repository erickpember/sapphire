// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.emerge.harms.HarmsLookups;
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;
import javax.inject.Inject;

/**
 * Pharmacologic VTE Prophylaxis Administered implementation
 */
public class PharmacologicVteProphylaxisAdministered {
  private static final BigDecimal ZERO_POINT_EIGHT_SIX = new BigDecimal("0.86");
  private static final BigDecimal NEGATIVE_ONE = new BigDecimal("-1");

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

  /**
   * Checks if pharmacologic VTE prophylaxis was administered
   *
   * @param encounterId
   *     encounter to search
   * @return true if VTE prophylaxis was administered
   */
  public boolean isPharmacologicVteProphylaxisAdministered(String encounterId) {
    boolean administered = false;

    List<MedicationAdministration> administrations = apiClient.getMedicationAdministrationClient()
        .search(encounterId);

    // Check if any recent VTE prophylactic administrations have been made.
    for (MedicationAdministration administration : administrations) {
      for (IdentifierDt ident : MedicationAdministrationUtils.findIdentifiers(administration,
          CodingSystems.UCSF_MEDICATION_GROUP_NAME)) {
        String medsSet = ident.getValue();

        for (PharmacologicVtePpxTypeEnum vtePpx : PharmacologicVtePpxTypeEnum.values()) {
          DateTimeDt timeTaken = (DateTimeDt) administration.getEffectiveTime();
          Long period = HarmsLookups.efficacyList.get(medsSet);
          if (vtePpx.getCode().equals(medsSet)
              && HarmsLookups.withinDrugPeriod(timeTaken.getValue(), period, clock)) {

            // Check dose ratio for Enoxaparin SC
            if (vtePpx.getCode().equals(
                PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode())) {
              BigDecimal dose = administration.getDosage().getQuantity().getValue();
              BigDecimal weight = HarmsLookups.getPatientWeight(apiClient, encounterId);
              administered = dose != null
                  && !weight.equals(NEGATIVE_ONE)
                  && dose.divide(weight, 10, BigDecimal.ROUND_HALF_UP)
                  .compareTo(ZERO_POINT_EIGHT_SIX) < 0;
              break;
            } else {
              administered = true;
              break;
            }
          }
        }
      }
    }

    return administered;
  }
}
