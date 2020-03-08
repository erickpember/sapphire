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
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.emerge.harms.HarmsLookups;
import com.datafascia.emerge.ucsf.MedicationOrderUtils;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Pharmacologic VTE Prophylaxis implementation
 */
@Slf4j
public class PharmacologicVteProphylaxis {
  private static final BigDecimal NEGATIVE_ONE = new BigDecimal("-1");
  private static final BigDecimal ZERO_POINT_EIGHT_SIX = new BigDecimal("0.86");

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Gets pharmacologic VTE prophylaxis type
   *
   * @param encounterId
   *     encounter to search
   * @return optional pharmacologic VTE prophylaxis type, empty if not found
   */
  public Optional<String> getPharmacologicVteProphylaxisType(String encounterId) {
    List<MedicationOrder> medicationOrders = apiClient.getMedicationOrderClient()
        .search(encounterId);

    return getPharmacologicVteProphylaxisType(medicationOrders, encounterId, Instant.now(clock));
  }

  /**
   * Gets pharmacologic VTE prophylaxis type
   *
   * @param medicationOrders
   *     All medication orders for a specific encounter.
   * @param encounterId
   *     ID for the same encounter, to use for patient weight retrieval in case of Enoxaparin.
   * @param now
   *     The current time.
   * @return optional pharmacologic VTE prophylaxis type, empty if not found
   */
  public Optional<String> getPharmacologicVteProphylaxisType(List<MedicationOrder> medicationOrders,
      String encounterId, Instant now) {
    String type = null;

    for (MedicationOrder medicationOrder : medicationOrders) {
      if (MedicationOrderUtils.isActiveOrDraft(medicationOrder)) {

        for (IdentifierDt ident : MedicationOrderUtils.findIdentifiers(medicationOrder,
            CodingSystems.UCSF_MEDICATION_GROUP_NAME)) {
          for (PharmacologicVtePpxTypeEnum atEnum : PharmacologicVtePpxTypeEnum.values()) {
            if (atEnum.getCode().equals(ident.getValue()) &&
                !MedicationOrderUtils.isExpired(medicationOrder, Date.from(now))) {
              // Check dose ratio for Intermittent Enoxaparin SC
              if (ident.getValue().equals(PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN
                  .getCode())) {
                if (medicationOrder.getDosageInstructionFirstRep()
                    .getDose() instanceof QuantityDt) {
                  if (isEnoxaparinOrderUnderPoint86(medicationOrder, encounterId)) {
                    type = atEnum.getCode();
                  } else {
                    return Optional.empty();
                  }
                }
              } else {
                type = atEnum.getCode();
              }
            }
          }
        }
      }
    }

    return Optional.ofNullable(type);
  }

  /**
   * Checks if Pharmacologic VTE Prophylaxis was ordered
   *
   * @param encounterId
   *     encounter to search
   * @return true if pharmacologic VTE prophylaxis was ordered
   */
  public boolean isPharmacologicVteProphylaxisOrdered(String encounterId) {
    return getPharmacologicVteProphylaxisType(encounterId).isPresent();
  }

  private boolean isEnoxaparinOrderUnderPoint86(MedicationOrder order, String encounterId) {
    BigDecimal dose = ((QuantityDt) order.getDosageInstructionFirstRep().getDose()).getValue();
    String unit = ((QuantityDt) order.getDosageInstructionFirstRep().getDose()).getUnit();
    if (dose == null || unit == null) {
      log.error(
          "Retrieved null dosage for encounter [{}], affecting VTE Prophylaxis logic", unit,
          encounterId);
      return false;
    }

    if ("mg/kg".equals(unit)) {
      return (dose.compareTo(ZERO_POINT_EIGHT_SIX) < 0);
    } else if ("mg".equals(unit)) {
      BigDecimal weight = getPatientWeight(encounterId);
      if (weight.compareTo(NEGATIVE_ONE) == 0) {
        log.error(
            "Failed to retrieve patient weight for enoxaparin dosage for encounter [{}], "
            + "affecting VTE Prophylaxis logic", encounterId);
        return false;
      } else {
        return (dose.divide(weight, 10, BigDecimal.ROUND_HALF_UP)
            .compareTo(ZERO_POINT_EIGHT_SIX) < 0);
      }
    } else {
      log.error(
          "Retrieved unrecognized dosage unit [{}]] for encounter [{}], "
          + "affecting VTE Prophylaxis logic", unit, encounterId);
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
