// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.emerge.harms.vte.Anticoagulation;
import com.datafascia.emerge.harms.vte.LowerExtremitySCDsContraindicatedImpl;
import com.datafascia.emerge.harms.vte.SCDsInUse;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.LowerExtremitySCDsContraindicated;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.TimestampedBoolean;
import com.datafascia.emerge.ucsf.VTE;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Updates venous thromboembolism data for a patient.
 */
@Slf4j
public class VenousThromboembolismUpdater {

  @Inject
  private Clock clock;

  @Inject
  private Anticoagulation anticoagulation;

  @Inject
  private LowerExtremitySCDsContraindicatedImpl lowerExtremitySCDsContraindicatedImpl;

  @Inject
  private SCDsInUse scdsInUseImpl;

  private static VTE getVTE(HarmEvidence harmEvidence) {
    MedicalData medicalData = harmEvidence.getMedicalData();
    VTE vte = medicalData.getVTE();
    if (vte == null) {
      vte = new VTE();
      medicalData.setVTE(vte);
    }

    return vte;
  }

  /**
   * Updates on systemic anticoagulation.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateOnSystemicAnticoagulation(HarmEvidence harmEvidence, Encounter encounter) {
    VTE vte = getVTE(harmEvidence);

    String encounterId = encounter.getId().getIdPart();
    TimestampedBoolean anticoagulated = new TimestampedBoolean()
        .withValue(anticoagulation.isAnticoagulated(encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));
    vte.setOnSystemicAnticoagulation(anticoagulated);
  }

  /**
   * Updates lower extremity SCDs contraindicated.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateLowerExtremitySCDsContraindicated(
      HarmEvidence harmEvidence, Encounter encounter) {

    VTE vte = getVTE(harmEvidence);

    String encounterId = encounter.getId().getIdPart();
    String reason = lowerExtremitySCDsContraindicatedImpl.getLowerExtremitySCDsContraindicated(
        encounterId);

    LowerExtremitySCDsContraindicated lowerExtremitySCDsContraindicated =
        new LowerExtremitySCDsContraindicated()
        .withValue(reason != null)
        .withReason(
            (reason != null) ? LowerExtremitySCDsContraindicated.Reason.fromValue(reason) : null)
        .withUpdateTime(Date.from(Instant.now(clock)));
    vte.setLowerExtremitySCDsContraindicated(lowerExtremitySCDsContraindicated);
  }

  /**
   * Updates SCDs in use.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateScdsInUse(HarmEvidence harmEvidence, Encounter encounter) {
    VTE vte = getVTE(harmEvidence);

    String encounterId = encounter.getId().getIdPart();
    TimestampedBoolean scdsInUse = new TimestampedBoolean()
        .withValue(scdsInUseImpl.isSCDsInUse(encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));
    vte.setSCDsInUse(scdsInUse);
  }
}
