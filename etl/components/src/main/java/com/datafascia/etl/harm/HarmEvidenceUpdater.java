// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
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
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

/**
 * Updates harm evidence for a patient in response to an event.
 */
public class HarmEvidenceUpdater {

  /**
   * Event type constants
   */
  public enum EventType {
    ADMIT_PATIENT,
    DISCHARGE_PATIENT
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

  private StatelessKieSession session;

  /**
   * Constructor
   */
  public HarmEvidenceUpdater() {
    KieServices services = KieServices.Factory.get();
    KieContainer container = services.getKieClasspathContainer();
    session = container.newStatelessKieSession("harmEvidence");
  }

  private HarmEvidence getHarmEvidence(String inputPatientId) {
    Id<HarmEvidence> patientId = Id.of(inputPatientId);
    Optional<HarmEvidence> optionalHarmEvidence = harmEvidenceRepository.read(patientId);
    if (!optionalHarmEvidence.isPresent()) {
      return new HarmEvidence()
          .withDemographicData(
              new DemographicData())
          .withMedicalData(
              new MedicalData());
    }
    return optionalHarmEvidence.get();
  }

  private HarmEvidence execute(
      EventType eventType, Encounter encounter, Object... additionalFacts) {

    session.setGlobal("demographicDataUpdater", demographicDataUpdater);

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
}
