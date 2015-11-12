// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.emerge.harms.vte.AnticoagulationImpl;
import com.datafascia.emerge.harms.vte.LowerExtremitySCDsContraindicatedImpl;
import com.datafascia.emerge.harms.vte.PharmacologicVteProphylaxis;
import com.datafascia.emerge.harms.vte.PharmacologicVteProphylaxisAdministered;
import com.datafascia.emerge.harms.vte.ProphylaxisContraindicated;
import com.datafascia.emerge.harms.vte.SCDsInUse;
import com.datafascia.emerge.harms.vte.SCDsOrdered;
import com.datafascia.emerge.ucsf.Anticoagulation;
import com.datafascia.emerge.ucsf.Contraindicated;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.LowerExtremitySCDsContraindicated;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.PharmacologicVTEProphylaxis;
import com.datafascia.emerge.ucsf.SCDs;
import com.datafascia.emerge.ucsf.TimestampedBoolean;
import com.datafascia.emerge.ucsf.TimestampedString;
import com.datafascia.emerge.ucsf.Type;
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
  private AnticoagulationImpl anticoagulationImpl;

  @Inject
  private PharmacologicVteProphylaxis pharmacologicVteProphylaxis;

  @Inject
  private LowerExtremitySCDsContraindicatedImpl lowerExtremitySCDsContraindicatedImpl;

  @Inject
  private SCDsInUse scdsInUseImpl;

  @Inject
  private SCDsOrdered scdsOrderedImpl;

  @Inject
  private ProphylaxisContraindicated prophylaxisContraindicated;

  @Inject
  private PharmacologicVteProphylaxisAdministered pharmacologicVteProphylaxisAdministered;

  private static VTE getVTE(HarmEvidence harmEvidence) {
    MedicalData medicalData = harmEvidence.getMedicalData();
    VTE vte = medicalData.getVTE();
    if (vte == null) {
      vte = new VTE();
      medicalData.setVTE(vte);
    }

    return vte;
  }

  private static Anticoagulation getAnticoagulation(HarmEvidence harmEvidence) {
    VTE vte = getVTE(harmEvidence);
    Anticoagulation anticoagulation = vte.getAnticoagulation();
    if (anticoagulation == null) {
      anticoagulation = new Anticoagulation();
      vte.setAnticoagulation(anticoagulation);
    }

    return anticoagulation;
  }

  private static SCDs getSCDs(HarmEvidence harmEvidence) {
    VTE vte = getVTE(harmEvidence);
    SCDs scds = vte.getSCDs();
    if (scds == null) {
      scds = new SCDs();
      vte.setSCDs(scds);
    }

    return scds;
  }

  private static PharmacologicVTEProphylaxis getPharmacologicVTEProphylaxis(
      HarmEvidence harmEvidence) {

    VTE vte = getVTE(harmEvidence);
    PharmacologicVTEProphylaxis prophylaxis = vte.getPharmacologicVTEProphylaxis();
    if (prophylaxis == null) {
      prophylaxis = new PharmacologicVTEProphylaxis();
      vte.setPharmacologicVTEProphylaxis(prophylaxis);
    }

    return prophylaxis;
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
    Anticoagulation anticoagulation = getAnticoagulation(harmEvidence);

    String encounterId = encounter.getId().getIdPart();
    TimestampedBoolean anticoagulated = new TimestampedBoolean()
        .withValue(anticoagulationImpl.isAnticoagulated(encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));
    anticoagulation.setOnSystemicAnticoagulation(anticoagulated);
  }

  /**
   * Updates anticoagulation type.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateAnticoagulationType(HarmEvidence harmEvidence, Encounter encounter) {
    Anticoagulation anticoagulation = getAnticoagulation(harmEvidence);

    String encounterId = encounter.getId().getIdPart();
    anticoagulationImpl.getAnticoagulationType(encounterId)
        .ifPresent(value -> {
          Type anticoagulationType = new Type()
              .withValue(Type.Value.fromValue(value.getCode()))
              .withUpdateTime(Date.from(Instant.now(clock)));

          anticoagulation.setType(anticoagulationType);
        });
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

    SCDs scds = getSCDs(harmEvidence);

    String encounterId = encounter.getId().getIdPart();
    String reason = lowerExtremitySCDsContraindicatedImpl.getLowerExtremitySCDsContraindicated(
        encounterId);

    LowerExtremitySCDsContraindicated lowerExtremitySCDsContraindicated =
        new LowerExtremitySCDsContraindicated()
        .withValue(reason != null)
        .withReason((reason != null)
            ? LowerExtremitySCDsContraindicated.Reason.fromValue(reason)
            : LowerExtremitySCDsContraindicated.Reason
                .NO_EHR_MECHANICAL_VTE_PROPHYLAXIS_CONTRAINDICATION)
        .withUpdateTime(Date.from(Instant.now(clock)));
    scds.setLowerExtremitySCDsContraindicated(lowerExtremitySCDsContraindicated);
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
    SCDs scds = getSCDs(harmEvidence);

    String encounterId = encounter.getId().getIdPart();
    TimestampedBoolean scdsInUse = new TimestampedBoolean()
        .withValue(scdsInUseImpl.isSCDsInUse(encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));
    scds.setInUse(scdsInUse);
  }

  /**
   * Updates SCDs ordered.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateScdsOrdered(HarmEvidence harmEvidence, Encounter encounter) {
    SCDs scds = getSCDs(harmEvidence);

    String encounterId = encounter.getId().getIdPart();
    TimestampedBoolean scdsOrdered = new TimestampedBoolean()
        .withValue(scdsOrderedImpl.isSCDsOrdered(encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));
    scds.setOrdered(scdsOrdered);
  }

  /**
   * Updates pharmacologic VTE prophylaxis type.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updatePharmacologicVTEProphylaxisType(
      HarmEvidence harmEvidence, Encounter encounter) {

    PharmacologicVTEProphylaxis prophylaxis = getPharmacologicVTEProphylaxis(harmEvidence);

    String encounterId = encounter.getId().getIdPart();
    pharmacologicVteProphylaxis.getPharmacologicVteProphylaxisType(encounterId)
        .ifPresent(value -> {
          TimestampedString pharmacologicVteProphylaxisType = new TimestampedString()
              .withValue(value)
              .withUpdateTime(Date.from(Instant.now(clock)));

          prophylaxis.setType(pharmacologicVteProphylaxisType);
        });
  }

  /**
   * Updates lower extremity SCDs contraindicated.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updatePharmacologicVTEProphylaxisContraindicated(
      HarmEvidence harmEvidence, Encounter encounter) {

    PharmacologicVTEProphylaxis prophylaxis = getPharmacologicVTEProphylaxis(harmEvidence);

    String encounterId = encounter.getId().getIdPart();
    String reason = prophylaxisContraindicated.getProphylaxisContraindicatedReason(encounterId);

    Contraindicated contraindicated = new Contraindicated()
        .withValue(reason != null)
        .withReason((reason != null)
            ? Contraindicated.Reason.fromValue(reason)
            : Contraindicated.Reason.NO_EHR_PHARMACOLOGIC_VTE_PROPHYLAXIS_CONTRAINDICATION)
        .withUpdateTime(Date.from(Instant.now(clock)));
    prophylaxis.setContraindicated(contraindicated);
  }

  /**
   * Updates pharmacologic VTE prophylaxis ordered.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updatePharmacologicVTEProphylaxisOrdered(
      HarmEvidence harmEvidence, Encounter encounter) {

    PharmacologicVTEProphylaxis prophylaxis = getPharmacologicVTEProphylaxis(harmEvidence);

    String encounterId = encounter.getId().getIdPart();
    TimestampedBoolean pharmacologicVteProphylaxisOrdered = new TimestampedBoolean()
        .withValue(pharmacologicVteProphylaxis.isPharmacologicVteProphylaxisOrdered(encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));
    prophylaxis.setOrdered(pharmacologicVteProphylaxisOrdered);
  }

  /**
   * Updates pharmacologic VTE prophylaxis administered.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updatePharmacologicVTEProphylaxisAdministered(
      HarmEvidence harmEvidence, Encounter encounter) {

    PharmacologicVTEProphylaxis prophylaxis = getPharmacologicVTEProphylaxis(harmEvidence);

    String encounterId = encounter.getId().getIdPart();
    TimestampedBoolean prophylaxisAdministered = new TimestampedBoolean()
        .withValue(
            pharmacologicVteProphylaxisAdministered.isPharmacologicVteProphylaxisAdministered(
                encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));
    prophylaxis.setAdministered(prophylaxisAdministered);
  }
}
