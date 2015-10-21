// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.MedicationOrderDateWrittenComparator;
import com.datafascia.emerge.ucsf.MedicationOrderUtils;
import com.datafascia.emerge.ucsf.codes.painAndDelerium.SedativeOrderDosageRouteEnum;
import com.datafascia.emerge.ucsf.codes.painAndDelerium.SedativeOrderDrugEnum;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.time.ZonedDateTime;
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
      SedativeOrderDrugEnum.ALPRAZOLOM.getCode());

  private static final Set<String> DOSAGE_ROUTES = ImmutableSet.of(
      SedativeOrderDosageRouteEnum.INTERMITTENT_IV.getCode(),
      SedativeOrderDosageRouteEnum.INTERMITTENT_ENTERAL.getCode(),
      SedativeOrderDosageRouteEnum.CONTINUOUS_INFUSION_IV.getCode());

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private static Clock clock;

  /**
   * Result container for sedative orders.
   */
  @Data @Builder
  public static class OrderResult {
    private String dosageRoute;
    private String drug;
    private String status;
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
    ZonedDateTime now = ZonedDateTime.now(clock);
    OrderResult result = OrderResult.builder()
        .dosageRoute(null)
        .drug(null)
        .status(null)
        .build();

    List<MedicationOrder> medOrders = MedicationOrderUtils
        .findActiveOrDraftOrderForMedicationBeforeTime(
            apiClient,
            encounterId,
            medication,
            Date.from(now.toInstant()));
    medOrders.sort(new MedicationOrderDateWrittenComparator().reversed());

    for (MedicationOrder order : medOrders) {
      String[] identifierParts = order.getIdentifierFirstRep().getValue().split(" ");
      if (order.getIdentifierFirstRep().getValue().contains("Continuous")) {
        result.setDosageRoute(identifierParts[1] + " " + identifierParts[2] + " "
            + identifierParts[4]);
        result.setDosageRoute(identifierParts[3]);
        result.setStatus(order.getStatus());

        return verifyResult(result);
      } else if (order.getIdentifierFirstRep().getValue().contains("Intermittent")) {
        result.setDosageRoute(identifierParts[1] + " " + identifierParts[3]);
        result.setDosageRoute(identifierParts[2]);
        result.setStatus(order.getStatus());
        return verifyResult(result);
      }
    }

    return Optional.empty();
  }

  private Optional<OrderResult> verifyResult(OrderResult result) {
    if (!SEDATIVE_NAMES.contains(result.getDrug())) {
      log.warn("Unexpected pain and delerium sedative drug name found [{}]", result.getDrug());
      return Optional.empty();
    }
    if (!DOSAGE_ROUTES.contains(result.getDosageRoute())) {
      log.warn("Unexpected pain and delerium sedative dosage route found [{}]", result
          .getDosageRoute());
      return Optional.empty();
    }
    return Optional.of(result);
  }
}
