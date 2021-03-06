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
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.emerge.harms.HarmsLookups;
import com.datafascia.emerge.ucsf.MedicationAdministrationEffectiveTimeComparator;
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Harms logic for VTE anticoagulation.
 */
@Slf4j
public class AnticoagulationImpl {
  private static final BigDecimal NEGATIVE_ONE = new BigDecimal("-1");
  private static final BigDecimal ZERO_POINT_EIGHT_SIX = new BigDecimal("0.86");
  private static final Long ACTIVELY_INFUSING_LOOKBACK = 4l;

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

  /**
   * Gets type of anticoagulant in use for an encounter.
   *
   * @param encounterId
   *     encounter to search
   * @return optional anticoagulant type, or empty if none.
   */
  public Optional<AnticoagulationTypeEnum> getAnticoagulationType(String encounterId) {
    List<MedicationAdministration> administrations = apiClient.getMedicationAdministrationClient()
        .search(encounterId);
    Observations observations = apiClient.getObservationClient().list(encounterId);
    return getAnticoagulationType(administrations, observations, encounterId, Instant.now(clock),
        apiClient);
  }

  /**
   * Gets type of anticoagulant in use for an encounter.
   *
   * @param administrations
   *     All medication administrations for an encounter.
   * @param observations
   *     All observations for this encounter.
   * @param encounterId
   *     encounter to search
   * @param now
   *     The current time.
   * @param client
   *     The API client.
   * @return optional anticoagulant type, or empty if none.
   */
  public Optional<AnticoagulationTypeEnum> getAnticoagulationType(
      Collection<MedicationAdministration> administrations,
      Observations observations,
      String encounterId, Instant now,
      ClientBuilder client) {
    administrations = MedicationAdministrationUtils.freshestOfAllOrders(administrations).values();

    // Get a sorted list of anticoagulation administrations
    List<MedicationAdministration> sortedAdmins =
        MedicationAdministrationUtils.freshestOfAllOrders(administrations).values().stream()
        .filter(admin -> !admin.getEffectiveTime().isEmpty())
        .sorted(new MedicationAdministrationEffectiveTimeComparator())
        .collect(Collectors.toList());

    for (MedicationAdministration admin : sortedAdmins) {
      if (admin.getEffectiveTime() == null) {
        log.warn("Ignoring admin [{}] as it lacks an effective time.",
            admin.getIdentifierFirstRep().getValue());
        continue;
      }

      for (IdentifierDt ident : MedicationAdministrationUtils.findIdentifiers(admin,
          CodingSystems.UCSF_MEDICATION_GROUP_NAME)) {
        String medsSet = ident.getValue();
        for (AnticoagulationTypeEnum antiType : AnticoagulationTypeEnum.values()) {
          if (antiType.getCode().equals(medsSet)) {

            // Get duration for evaluating completed admins.
            Long period = HarmsLookups.EFFICACY_LIST.get(medsSet);
            Long lookbackHours = ACTIVELY_INFUSING_LOOKBACK + TimeUnit.MILLISECONDS.toHours(period);
            DateTimeDt timeTaken = (DateTimeDt) admin.getEffectiveTime();

            if (medsSet.contains("Continuous")) {
              if (admin.getReasonNotGiven().isEmpty()
                  && MedicationAdministrationUtils.isActivelyInfusing(
                      admin,
                      medsSet,
                      now,
                      lookbackHours,
                      client,
                      encounterId)) {
                return Optional.of(antiType);
              }
            } else if (medsSet.contains("Intermittent")) {
              if (medsSet.equals(AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN
                  .getCode())) {
                if (isEnoxaparinOverPoint86(admin, encounterId)) {
                  if (MedicationAdministrationStatusEnum.IN_PROGRESS
                      .equals(admin.getStatusElement().getValueAsEnum())) {
                    return Optional.of(antiType);
                  } else if (MedicationAdministrationStatusEnum.COMPLETED
                      .equals(admin.getStatusElement().getValueAsEnum())) {
                    if (HarmsLookups.withinDrugPeriod(timeTaken.getValue(), period, now)) {
                      return Optional.of(antiType);
                    }
                  }
                }
              } else if (medsSet.equals(AnticoagulationTypeEnum.INTERMITTENT_WARFARIN_ENTERAL
                  .getCode())) {
                // Special case for Warfarin: "Been administered within 3 days + INR > 1.5"
                if (!HarmsLookups.inrOver1point5(observations, null)) {
                  return Optional.empty();
                }

                if (MedicationAdministrationStatusEnum.IN_PROGRESS
                    .equals(admin.getStatusElement().getValueAsEnum())) {
                  return Optional.of(antiType);
                } else if (MedicationAdministrationStatusEnum.COMPLETED
                    .equals(admin.getStatusElement().getValueAsEnum())) {
                  if (HarmsLookups.withinDrugPeriod(timeTaken.getValue(), period, now)) {
                    return Optional.of(antiType);
                  }
                } // end if admin is in progress or completed
              } else {
                // Intermittent but not Enoxaparin or Warfarin
                if (MedicationAdministrationStatusEnum.IN_PROGRESS
                    .equals(admin.getStatusElement().getValueAsEnum())) {
                  return Optional.of(antiType);
                } else if (MedicationAdministrationStatusEnum.COMPLETED
                    .equals(admin.getStatusElement().getValueAsEnum())) {
                  if (HarmsLookups.withinDrugPeriod(timeTaken.getValue(), period, now)) {
                    return Optional.of(antiType);
                  }
                } // end if admin is in progress or completed
              } // end if intermittent is or isn't enoxaparin or warfarin
            } // end if is or isn't intermittent
          } // end if meds set matches an anticoagulation type
        }
      }
    }

    return Optional.empty();
  }

  private boolean isEnoxaparinOverPoint86(MedicationAdministration admin, String encounterId) {
    BigDecimal dose = admin.getDosage().getQuantity().getValue();
    String unit = admin.getDosage().getQuantity().getUnit();

    if (dose == null || unit == null) {
      log.warn(
          "Retrieved null dosage in enoxaparin administration for encounter [{}],"
          + " affecting anticoagulation logic", encounterId);
      return false;
    }

    if ("mg/kg".equals(unit)) {
      return (dose.compareTo(ZERO_POINT_EIGHT_SIX) >= 0);
    } else if ("mg".equals(unit)) {
      BigDecimal weight = getPatientWeight(encounterId);
      if (weight.compareTo(NEGATIVE_ONE) == 0) {
        log.warn(
            "Failed to retrieve patient weight for enoxaparin dosage for encounter [{}], "
            + "affecting anticoagulation logic", encounterId);
        return false;
      } else {
        return (dose.divide(weight, 10, BigDecimal.ROUND_HALF_UP)
            .compareTo(ZERO_POINT_EIGHT_SIX) >= 0);
      }
    } else {
      log.warn(
          "Retrieved unrecognized dosage unit [{}]] for encounter [{}], "
          + "affecting anticoagulation logic", unit, encounterId);
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
