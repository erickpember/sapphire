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
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.emerge.ucsf.MedicationOrderUtils;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import com.datafascia.emerge.ucsf.codes.painAndDelerium.SedativeOrderDosageRouteEnum;
import com.datafascia.emerge.ucsf.codes.painAndDelerium.SedativeOrderDrugEnum;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a sedative order in the pain and delirium group for Emerge.
 */
@Slf4j
public class SedativeOrder {

  private static final Set<String> SEDATIVE_NAMES = ImmutableSet.of(
      SedativeOrderDrugEnum.LORAZEPAM.getCode(),
      SedativeOrderDrugEnum.MIDAZOLAM.getCode(),
      SedativeOrderDrugEnum.CLONAZEPAM.getCode(),
      SedativeOrderDrugEnum.DIAZEPAM.getCode(),
      SedativeOrderDrugEnum.CHLORADIAZEPOXIDE.getCode(),
      SedativeOrderDrugEnum.ALPRAZOLOM.getCode());

  private static final Set<String> SEDATIVE_MEDS_SETS = ImmutableSet.of(
      MedsSetEnum.CONTINUOUS_INFUSION_LORAZEPAM_IV.getCode(),
      MedsSetEnum.CONTINUOUS_INFUSION_MIDAZOLAM_IV.getCode(),
      MedsSetEnum.INTERMITTENT_ALPRAZALOM_ENTERAL.getCode(),
      MedsSetEnum.INTERMITTENT_CHLORADIAZEPOXIDE_ENTERAL.getCode(),
      MedsSetEnum.INTERMITTENT_CLONAZEPAM_ENTERAL.getCode(),
      MedsSetEnum.INTERMITTENT_DIAZEPAM_ENTERAL.getCode(),
      MedsSetEnum.INTERMITTENT_DIAZEPAM_IV.getCode(),
      MedsSetEnum.INTERMITTENT_LORAZEPAM_ENTERAL.getCode(),
      MedsSetEnum.INTERMITTENT_LORAZEPAM_IV.getCode(),
      MedsSetEnum.INTERMITTENT_MIDAZOLAM_IV.getCode());

  private static final Set<String> DOSAGE_ROUTES = ImmutableSet.of(
      SedativeOrderDosageRouteEnum.INTERMITTENT_IV.getCode(),
      SedativeOrderDosageRouteEnum.INTERMITTENT_ENTERAL.getCode(),
      SedativeOrderDosageRouteEnum.CONTINUOUS_INFUSION_IV.getCode());

  @Inject
  private ClientBuilder apiClient;

  /**
   * Result container for sedative orders.
   */
  @Data @Builder
  public static class OrderResult {
    private String dosageRoute;
    private String drug;
    private String status;
    private String orderId;
  }

  /**
   * Pulls Medication Orders for an encounter and returns all belonging to the sedative list.
   *
   * @param encounterId
   *     encounter to check.
   * @return
   *     A list of results with drug name, route and order status, empty list if not found.
   */
  public List<OrderResult> getAllSedativeOrders(String encounterId) {

    List<MedicationOrder> medOrders = apiClient.getMedicationOrderClient().search(encounterId,
        null, null);

    return processSedativeOrders(medOrders);
  }

  /**
   * Handles the client-independent logic of medication orders in, drug/dosage results out.
   * @param medOrders
   *     A list of medication orders whose meds set identifiers we will scan.
   * @return
   *     A list of results with drug name, route and order status, empty list if not found.
   */
  public List<OrderResult> processSedativeOrders(List<MedicationOrder> medOrders) {
    List<OrderResult> results = new ArrayList<>();
    for (MedicationOrder order : medOrders) {
      if (order.getStatus() == null ||
          (!order.getStatus().equals(MedicationOrderStatusEnum.ACTIVE.getCode()) &&
          !order.getStatus().equals(MedicationOrderStatusEnum.DRAFT.getCode()))) {
        continue;
      }

      OrderResult result = OrderResult.builder()
          .dosageRoute(null)
          .drug(null)
          .status(null)
          .orderId(null)
          .build();

      List<IdentifierDt> idents = MedicationOrderUtils.findIdentifiers(order,
          CodingSystems.UCSF_MEDICATION_GROUP_NAME);
      for (IdentifierDt ident : idents) {
        String medsSet = ident.getValue();

        if (SEDATIVE_MEDS_SETS.contains(medsSet)) {
          for (String sedativeName : SEDATIVE_NAMES) {
            if (medsSet.contains(sedativeName)) {
              result.setDrug(sedativeName);
              result.setStatus(order.getStatus());
              result.setOrderId(MedicationOrderUtils.findIdentifiers(order,
                  IdentifierSystems.INSTITUTION_MEDICATION_ORDER).get(0).getValue());
              if (medsSet.contains("Continuous")) {
                if (medsSet.contains(" IV")) {
                  result.setDosageRoute(SedativeOrderDosageRouteEnum.CONTINUOUS_INFUSION_IV
                      .getCode());
                }
              } else if (medsSet.contains("Intermittent")) {
                if (medsSet.contains(" IV")) {
                  result.setDosageRoute(SedativeOrderDosageRouteEnum.INTERMITTENT_IV.getCode());
                }
                if (medsSet.contains("Enteral")) {
                  result.setDosageRoute(
                      SedativeOrderDosageRouteEnum.INTERMITTENT_ENTERAL.getCode());
                }
              }
              if (resultIsValid(result)) {
                results.add(result);
              }
            }
          }
        }
      }
    }

    return results;
  }

  private boolean resultIsValid(OrderResult result) {
    if (!SEDATIVE_NAMES.contains(result.getDrug())) {
      log.warn("Unexpected pain and delirium sedative drug name found [{}], order id [{}]",
          result.getDrug(),
          result.getOrderId());
      return false;
    }
    if (!DOSAGE_ROUTES.contains(result.getDosageRoute())) {
      log.warn("Unexpected pain and delirium sedative dosage route found [{}], order id [{}]",
          result.getDosageRoute(),
          result.getOrderId());
      return false;
    }
    return true;
  }
}
