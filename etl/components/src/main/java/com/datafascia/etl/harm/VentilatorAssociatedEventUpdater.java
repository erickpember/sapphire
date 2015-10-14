// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.emerge.harms.vae.VentilationModeImpl;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.VAE;
import com.datafascia.emerge.ucsf.VentilationMode;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import javax.inject.Inject;

/**
 * Updates ventilator associated event data for a patient.
 */
public class VentilatorAssociatedEventUpdater {

  @Inject
  private Clock clock;

  @Inject
  private VentilationModeImpl ventilationModeImpl;

  private static VAE getVAE(HarmEvidence harmEvidence) {
    MedicalData medicalData = harmEvidence.getMedicalData();
    VAE vae = medicalData.getVAE();
    if (vae == null) {
      vae = new VAE();
      medicalData.setVAE(vae);
    }

    return vae;
  }

  private static VentilationMode getVentilationMode(HarmEvidence harmEvidence) {
    VAE vae = getVAE(harmEvidence);
    VentilationMode ventilationMode = vae.getVentilationMode();
    if (ventilationMode == null) {
      ventilationMode = new VentilationMode();
      vae.setVentilationMode(ventilationMode);
    }

    return ventilationMode;
  }

  private void updateVentilationMode(HarmEvidence harmEvidence, String encounterId) {
    VentilationMode ventilationMode = getVentilationMode(harmEvidence);
    String value = ventilationModeImpl.getVentilationMode(encounterId);
    ventilationMode.setValue((value == null) ? null : VentilationMode.Value.fromValue(value));
    ventilationMode.setUpdateTime(Date.from(Instant.now(clock)));
  }

  /**
   * Updates ventilator associated event data.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void update(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();
    updateVentilationMode(harmEvidence, encounterId);
  }
}
