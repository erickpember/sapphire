// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.model.dstu2.resource.Location;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.emerge.ucsf.DemographicData;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.persist.HarmEvidenceRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

/**
 * Updates harm evidence for a patient in response to an event.
 */
@Slf4j
public class HarmEvidenceUpdater {

  /**
   * Event type constants
   */
  public enum EventType {
    ADMIT_PATIENT,
    DISCHARGE_PATIENT,
    UPDATE_FLAG
  }

  /**
   * Fact passed to rules engine.
   */
  @AllArgsConstructor
  @Data
  public static class Event {
    private EventType type;
  }

  @Inject
  private HarmEvidenceRepository harmEvidenceRepository;

  @Inject
  private DemographicDataUpdater demographicDataUpdater;

  @Inject
  private AlignmentOfGoalsUpdater alignmentOfGoalsUpdater;

  private KieContainer container;

  /**
   * Constructor
   */
  public HarmEvidenceUpdater() {
    KieServices services = KieServices.Factory.get();
    container = services.getKieClasspathContainer();
  }

  private HarmEvidence getHarmEvidence(String inputPatientId) {
    Id<HarmEvidence> patientId = Id.of(inputPatientId);
    Optional<HarmEvidence> optionalHarmEvidence = harmEvidenceRepository.read(patientId);
    if (!optionalHarmEvidence.isPresent()) {
      return new HarmEvidence()
          .withDemographicData(
              new DemographicData()
                  .withMedicalRecordNumber(inputPatientId))
          .withMedicalData(
              new MedicalData());
    }

    HarmEvidence harmEvidence = optionalHarmEvidence.get();
    if (harmEvidence.getMedicalData() == null) {
      harmEvidence.setMedicalData(new MedicalData());
    }

    return harmEvidence;
  }

  private HarmEvidence execute(
      EventType eventType, Encounter encounter, Object... additionalFacts) {

    StatelessKieSession session = container.newStatelessKieSession("harmEvidence");
    session.setGlobal("demographicDataUpdater", demographicDataUpdater);
    session.setGlobal("alignmentOfGoalsUpdater", alignmentOfGoalsUpdater);

    String patientId = encounter.getPatient().getReference().getIdPart();
    HarmEvidence harmEvidence = getHarmEvidence(patientId);
    session.setGlobal("harmEvidence", harmEvidence);

    List<Object> facts = new ArrayList<>();
    facts.add(new Event(eventType));
    facts.add(encounter);
    facts.addAll(Arrays.asList(additionalFacts));

    session.execute(facts);
    return harmEvidence;
  }

  /**
   * Admits patient.
   *
   * @param patient
   *     patient
   * @param location
   *     location
   * @param encounter
   *     encounter
   */
  public void admitPatient(
      UnitedStatesPatient patient, Location location, Encounter encounter) {

    HarmEvidence harmEvidence = execute(EventType.ADMIT_PATIENT, encounter, patient, location);
    harmEvidenceRepository.save(harmEvidence);
  }

  /**
   * Discharges patient.
   *
   * @param encounter
   *     encounter
   */
  public void dischargePatient(Encounter encounter) {
    execute(EventType.DISCHARGE_PATIENT, encounter);

    String patientIdString = encounter.getPatient().getReference().getIdPart();
    Id<HarmEvidence> patientId = Id.of(patientIdString);
    harmEvidenceRepository.delete(patientId);
  }

  /**
   * Updates flag.
   *
   * @param flag
   *     flag
   * @param encounter
   *     encounter
   */
  public void updateFlag(Flag flag, Encounter encounter) {
    HarmEvidence harmEvidence = execute(EventType.UPDATE_FLAG, encounter, flag);
    harmEvidenceRepository.save(harmEvidence);
  }
}
