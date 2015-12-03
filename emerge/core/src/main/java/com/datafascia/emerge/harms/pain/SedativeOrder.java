// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.emerge.ucsf.MedicationOrderDateWrittenComparator;
import com.datafascia.emerge.ucsf.MedicationOrderUtils;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import com.datafascia.emerge.ucsf.codes.painAndDelerium.SedativeOrderDosageRouteEnum;
import com.datafascia.emerge.ucsf.codes.painAndDelerium.SedativeOrderDrugEnum;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
      SedativeOrderDrugEnum.ALPRAZOLOM.getCode(),
      SedativeOrderDrugEnum.DEXMETOMIDINE.getCode(),
      SedativeOrderDrugEnum.PROPOFOL.getCode());

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
      MedsSetEnum.INTERMITTENT_MIDAZOLAM_IV.getCode(),
      MedsSetEnum.CONTINUOUS_INFUSION_DEXMEDETOMIDINE_IV.getCode(),
      MedsSetEnum.CONTINUOUS_INFUSION_PROPOFOL_IV.getCode());

  private static final Set<String> DOSAGE_ROUTES = ImmutableSet.of(
      SedativeOrderDosageRouteEnum.INTERMITTENT_IV.getCode(),
      SedativeOrderDosageRouteEnum.INTERMITTENT_ENTERAL.getCode(),
      SedativeOrderDosageRouteEnum.CONTINUOUS_INFUSION_IV.getCode());

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

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
   * Represents a medication order.
   *
   * @param encounterId
   *     encounter to check.
   * @param medication
   *     Medication identifier, AKA MedsSet.
   * @return Drug name, route and order status, empty if not found.
   */
  public Optional<OrderResult> getValue(String encounterId, String medication) {
    Date now = Date.from(Instant.now(clock));

    List<MedicationOrder> medOrders = MedicationOrderUtils
        .findMedicationBefore(
            apiClient,
            encounterId,
            medication,
            now);
    medOrders.sort(new MedicationOrderDateWrittenComparator().reversed());

    return processOrders(medOrders);
  }

  /**
   * Handles the client-independent logic of medication orders in, drug/dosage results out.
   * @param medOrders
   *     A list of medication orders whose meds set identifiers we will scan.
   * @return
   *     Drug name, route and order status, empty if not found.
   */
  public Optional<OrderResult> processOrders(List<MedicationOrder> medOrders) {
    for (MedicationOrder order : medOrders) {
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
              return verifyResult(result);
            }
          }
        }
      }
    }

    return Optional.empty();
  }

  private Optional<OrderResult> verifyResult(OrderResult result) {
    if (!SEDATIVE_NAMES.contains(result.getDrug())) {
      log.warn("Unexpected pain and delirium sedative drug name found [{}], order id [{}]",
          result.getDrug(),
          result.getOrderId());
      return Optional.empty();
    }
    if (!DOSAGE_ROUTES.contains(result.getDosageRoute())) {
      log.warn("Unexpected pain and delirium sedative dosage route found [{}], order id [{}]",
          result.getDosageRoute(),
          result.getOrderId());
      return Optional.empty();
    }
    return Optional.of(result);
  }
}
