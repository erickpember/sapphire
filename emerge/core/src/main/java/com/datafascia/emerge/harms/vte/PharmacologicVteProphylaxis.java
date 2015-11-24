// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.emerge.harms.HarmsLookups;
import com.datafascia.emerge.ucsf.MedicationOrderUtils;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Pharmacologic VTE Prophylaxis implementation
 */
public class PharmacologicVteProphylaxis {
  private static final BigDecimal NEGATIVE_ONE = new BigDecimal("-1");
  private static final BigDecimal ZERO_POINT_EIGHT_SIX = new BigDecimal("0.86");

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
    String type = null;

    List<MedicationOrder> medicationOrders = apiClient.getMedicationOrderClient()
        .search(encounterId);
    for (MedicationOrder medicationOrder : medicationOrders) {
      if (MedicationOrderUtils.isActiveOrDraft(medicationOrder)) {

        for (IdentifierDt ident : MedicationOrderUtils.findIdentifiers(medicationOrder,
            CodingSystems.UCSF_MEDICATION_GROUP_NAME)) {
          for (AnticoagulationTypeEnum atEnum : AnticoagulationTypeEnum.values()) {
            if (atEnum.getCode().equals(ident.getValue())) {

              // Check dose ratio for Intermittent Enoxaparin SC
              if (ident.equals(AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN_SC.getCode())) {
                MedicationOrder.DosageInstruction dosage =
                    medicationOrder.getDosageInstructionFirstRep();
                IDatatype dose = dosage.getDose();
                if (dose instanceof QuantityDt) {
                  QuantityDt quantity = (QuantityDt) dosage.getDose();
                  BigDecimal weight = HarmsLookups.getPatientWeight(apiClient, encounterId);
                  if (weight.compareTo(NEGATIVE_ONE) == 0) {
                    return Optional.empty();
                  } else {
                    if (quantity.getValue().divide(weight, 10, BigDecimal.ROUND_HALF_UP)
                        .compareTo(ZERO_POINT_EIGHT_SIX) < 0) {
                      type = atEnum.getCode();
                    }
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
}
