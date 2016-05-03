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
import java.time.Instant;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Pharmacologic VTE Prophylaxis Administered implementation
 */
@Slf4j
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
    List<MedicationAdministration> administrations = apiClient.getMedicationAdministrationClient()
        .search(encounterId);

    return isPharmacologicVteProphylaxisAdministered(administrations, encounterId,
        Instant.now(clock));
  }

  /**
   * Checks if pharmacologic VTE prophylaxis was administered
   *
   * @param administrations
   *     All medication administrations for a specific encounter.
   * @param encounterId
   *     ID for the same encounter, to use for patient weight retrieval in case of Enoxaparin.
   * @param now
   *     The current time.
   * @return true if VTE prophylaxis was administered
   */
  public boolean isPharmacologicVteProphylaxisAdministered(
      List<MedicationAdministration> administrations, String encounterId, Instant now) {
    boolean administered = false;

    // Check if any recent VTE prophylactic administrations have been made.
    for (MedicationAdministration administration : administrations) {
      if (administration.getEffectiveTime() == null) {
        log.warn("Ignoring admin [{}] as it lacks an effective time.",
            administration.getIdentifierFirstRep().getValue());
        continue;
      }

      for (IdentifierDt ident : MedicationAdministrationUtils.findIdentifiers(administration,
          CodingSystems.UCSF_MEDICATION_GROUP_NAME)) {
        String medsSet = ident.getValue();

        for (PharmacologicVtePpxTypeEnum vtePpx : PharmacologicVtePpxTypeEnum.values()) {
          DateTimeDt timeTaken = (DateTimeDt) administration.getEffectiveTime();
          Long period = HarmsLookups.efficacyList.get(medsSet);
          if (vtePpx.getCode().equals(medsSet)
              && HarmsLookups.withinDrugPeriod(timeTaken.getValue(), period, now)) {

            // Check dose ratio for Enoxaparin SC
            if (vtePpx.getCode().equals(
                PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode())) {
              administered = isEnoxaparinUnderPoint86(administration, encounterId);
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

  private boolean isEnoxaparinUnderPoint86(MedicationAdministration admin, String encounterId) {
    BigDecimal dose = admin.getDosage().getQuantity().getValue();
    String unit = admin.getDosage().getQuantity().getUnit();

    if (dose == null || unit == null) {
      log.error(
          "Retrieved null dosage for Enoxaparin administration [{}] in encounter [{}],"
          + " affecting VTE Prophylaxis Administered logic",
          admin.getIdentifierFirstRep().getValue(), encounterId);
      return false;
    }

    if ("mg/kg".equals(unit)) {
      return (dose.compareTo(ZERO_POINT_EIGHT_SIX) < 0);
    } else if ("mg".equals(unit)) {
      BigDecimal weight = getPatientWeight(encounterId);
      if (weight.compareTo(NEGATIVE_ONE) == 0) {
        log.error(
            "Failed to retrieve patient weight for enoxaparin dosage for encounter [{}], "
            + "affecting VTE Prophylaxis Administered logic", encounterId);
        return false;
      } else {
        return (dose.divide(weight, 10, BigDecimal.ROUND_HALF_UP)
            .compareTo(ZERO_POINT_EIGHT_SIX) < 0);
      }
    } else {
      log.error(
          "Retrieved unrecognized dosage unit [{}]] for encounter [{}], "
          + "affecting VTE Prophylaxis Administered logic", unit, encounterId);
      return false;
    }
  }

  /**
   * Wraps HarmsLookups patient weight method to facilitate unit testing.
   *
   * @param encounterId
   *     Encounter of the patient whose weight we want.
   * @return
   *     Patient weight in kg.
   */
  public BigDecimal getPatientWeight(String encounterId) {
    return HarmsLookups.getPatientWeight(apiClient, encounterId);
  }
}
