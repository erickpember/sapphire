// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.emerge.ucsf.DemographicData;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.persist.HarmEvidenceRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    TIMER,
    UPDATE_FLAG,
    UPDATE_OBSERVATIONS,
    UPDATE_PROCEDURE
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
  private CentralLineAssociatedBloodStreamInfectionUpdater clabsiUpdater;

  @Inject
  private AlignmentOfGoalsUpdater alignmentOfGoalsUpdater;

  @Inject
  private VenousThromboembolismUpdater venousThromboembolismUpdater;

  @Inject
  private VentilatorAssociatedEventUpdater ventilatorAssociatedEventUpdater;

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

  private HarmEvidence executeWithObservations(
      EventType eventType,
      Encounter encounter,
      List<Observation> observations,
      Object... additionalFacts) {

    StatelessKieSession session = container.newStatelessKieSession("harmEvidence");
    session.setGlobal("demographicDataUpdater", demographicDataUpdater);
    session.setGlobal("centralLineAssociatedBloodStreamInfectionUpdater", clabsiUpdater);
    session.setGlobal("alignmentOfGoalsUpdater", alignmentOfGoalsUpdater);
    session.setGlobal("venousThromboembolismUpdater", venousThromboembolismUpdater);
    session.setGlobal("ventilatorAssociatedEventUpdater", ventilatorAssociatedEventUpdater);

    String patientId = encounter.getPatient().getReference().getIdPart();
    HarmEvidence harmEvidence = getHarmEvidence(patientId);
    session.setGlobal("harmEvidence", harmEvidence);

    List<Object> facts = new ArrayList<>();
    facts.add(new Event(eventType));
    facts.add(encounter);
    facts.addAll(observations);
    facts.addAll(Arrays.asList(additionalFacts));

    session.execute(facts);
    return harmEvidence;
  }

  private HarmEvidence execute(
      EventType eventType, Encounter encounter, Object... additionalFacts) {

    return executeWithObservations(eventType, encounter, Collections.emptyList(), additionalFacts);
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
   * Performs logic in response to timer event.
   *
   * @param encounter
   *     encounter
   */
  public void processTimer(Encounter encounter) {
    HarmEvidence harmEvidence = execute(EventType.TIMER, encounter);
    harmEvidenceRepository.save(harmEvidence);
  }

  /**
   * Updates observations.
   *
   * @param observations
   *     observations
   * @param encounter
   *     encounter
   */
  public void updateObservations(List<Observation> observations, Encounter encounter) {
    HarmEvidence harmEvidence = executeWithObservations(
        EventType.UPDATE_OBSERVATIONS, encounter, observations);
    harmEvidenceRepository.save(harmEvidence);
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

  /**
   * Updates procedure.
   *
   * @param procedure
   *     procedure
   * @param encounter
   *     encounter
   */
  public void updateProcedure(Procedure procedure, Encounter encounter) {
    HarmEvidence harmEvidence = execute(EventType.UPDATE_PROCEDURE, encounter, procedure);
    harmEvidenceRepository.save(harmEvidence);
  }
}
