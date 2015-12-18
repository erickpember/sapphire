// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.emerge.ucsf.EncounterMutex;
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
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.ConsequenceExceptionHandler;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.RuleRuntime;
import org.kie.internal.conf.ConsequenceExceptionHandlerOption;

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
    UPDATE_PARTICIPANT,
    UPDATE_PATIENT,
    UPDATE_PROCEDURE,
    UPDATE_PROCEDURE_REQUEST
  }

  /**
   * Fact passed to rules engine.
   */
  @AllArgsConstructor
  @Data
  public static class Event {
    private EventType type;
  }

  /**
   * Swallows exception throw in consequence
   */
  public static class MyConsequenceExceptionHandler implements ConsequenceExceptionHandler {
    @Override
    public void handleException(Match match, RuleRuntime workingMemory, Exception exception) {
      log.error("Exception in consequence of rule {}", match.getRule(), exception);
    }
  }

  @Inject
  private EncounterMutex encounterMutex;

  @Inject
  private HarmEvidenceRepository harmEvidenceRepository;

  @Inject
  private DemographicDataUpdater demographicDataUpdater;

  @Inject
  private CentralLineAssociatedBloodStreamInfectionUpdater clabsiUpdater;

  @Inject
  private IntensiveCareUnitAcquiredWeaknessUpdater icuAcquiredWeaknessUpdater;

  @Inject
  private AlignmentOfGoalsUpdater alignmentOfGoalsUpdater;

  @Inject
  private VenousThromboembolismUpdater venousThromboembolismUpdater;

  @Inject
  private VentilatorAssociatedEventUpdater ventilatorAssociatedEventUpdater;

  @Inject
  private RespectDignityUpdater respectDignityUpdater;

  @Inject
  private PainAndDeliriumUpdater painAndDeliriumUpdater;

  private KieBase base;

  /**
   * Constructor
   */
  public HarmEvidenceUpdater() {
    KieServices services = KieServices.Factory.get();
    KieContainer container = services.newKieClasspathContainer();

    KieBaseConfiguration baseConfiguration = services.newKieBaseConfiguration();
    baseConfiguration.setOption(
        ConsequenceExceptionHandlerOption.get(MyConsequenceExceptionHandler.class));

    base = container.newKieBase("harmEvidenceBase", baseConfiguration);
  }

  private HarmEvidence getHarmEvidence(Encounter encounter) {
    String encounterIdentifier = encounter.getIdentifierFirstRep().getValue();

    Id<Encounter> encounterId = Id.of(encounterIdentifier);
    Optional<HarmEvidence> optionalHarmEvidence = harmEvidenceRepository.read(encounterId);
    if (!optionalHarmEvidence.isPresent()) {
      return new HarmEvidence()
          .withEncounterID(
              encounterIdentifier)
          .withDemographicData(
              DemographicDataUpdater.createDemographicData(encounter))
          .withMedicalData(
              new MedicalData());
    }

    HarmEvidence harmEvidence = optionalHarmEvidence.get();
    if (harmEvidence.getEncounterID() == null) {
      harmEvidence.setEncounterID(encounterIdentifier);
    }
    if (harmEvidence.getMedicalData() == null) {
      harmEvidence.setMedicalData(new MedicalData());
    }

    return harmEvidence;
  }

  private void doExecuteWithObservations(
      EventType eventType,
      Encounter encounter,
      List<Observation> observations,
      Object... additionalFacts) {

    StatelessKieSession session = base.newStatelessKieSession();
    session.setGlobal("demographicDataUpdater", demographicDataUpdater);
    session.setGlobal("centralLineAssociatedBloodStreamInfectionUpdater", clabsiUpdater);
    session.setGlobal("alignmentOfGoalsUpdater", alignmentOfGoalsUpdater);
    session.setGlobal("venousThromboembolismUpdater", venousThromboembolismUpdater);
    session.setGlobal("ventilatorAssociatedEventUpdater", ventilatorAssociatedEventUpdater);
    session.setGlobal("respectDignityUpdater", respectDignityUpdater);
    session.setGlobal("intensiveCareUnitAcquiredWeaknessUpdater", icuAcquiredWeaknessUpdater);
    session.setGlobal("painAndDeliriumUpdater", painAndDeliriumUpdater);

    HarmEvidence harmEvidence = getHarmEvidence(encounter);
    session.setGlobal("harmEvidence", harmEvidence);

    List<Object> facts = new ArrayList<>();
    facts.add(new Event(eventType));
    facts.add(encounter);
    facts.addAll(observations);
    facts.addAll(Arrays.asList(additionalFacts));

    session.execute(facts);

    harmEvidenceRepository.save(harmEvidence);
  }

  private void executeWithObservations(
      EventType eventType,
      Encounter encounter,
      List<Observation> observations,
      Object... additionalFacts) {

    String encounterIdentifier = encounter.getIdentifierFirstRep().getValue();
    encounterMutex.acquire(encounterIdentifier);
    try {
      doExecuteWithObservations(eventType, encounter, observations, additionalFacts);
    } finally {
      encounterMutex.release(encounterIdentifier);
    }
  }

  private void execute(EventType eventType, Encounter encounter, Object... additionalFacts) {
    executeWithObservations(eventType, encounter, Collections.emptyList(), additionalFacts);
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

    execute(EventType.ADMIT_PATIENT, encounter, patient, location);
  }

  /**
   * Discharges patient.
   *
   * @param encounter
   *     encounter
   */
  public void dischargePatient(Encounter encounter) {
    execute(EventType.DISCHARGE_PATIENT, encounter);

    String encounterIdentifier = encounter.getIdentifierFirstRep().getValue();
    Id<Encounter> encounterId = Id.of(encounterIdentifier);
    harmEvidenceRepository.delete(encounterId);
  }

  /**
   * Performs logic in response to timer event.
   *
   * @param encounter
   *     encounter
   */
  public void processTimer(Encounter encounter) {
    execute(EventType.TIMER, encounter);
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
    executeWithObservations(EventType.UPDATE_OBSERVATIONS, encounter, observations);
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
    execute(EventType.UPDATE_FLAG, encounter, flag);
  }

  /**
   * Updates participant.
   *
   * @param practitioner
   *    practitioner
   * @param encounter
   *    encounter
   */
  public void updateParticipant(Practitioner practitioner, Encounter encounter) {
    execute(EventType.UPDATE_PARTICIPANT, encounter, practitioner);
  }

  /**
   * Updates patient.
   *
   * @param patient
   *     patient
   * @param location
   *     location
   * @param encounter
   *     encounter
   */
  public void updatePatient(UnitedStatesPatient patient, Location location, Encounter encounter) {
    execute(EventType.UPDATE_PATIENT, encounter, patient, location);
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
    execute(EventType.UPDATE_PROCEDURE, encounter, procedure);
  }

  /**
   * Updates procedure request.
   *
   * @param procedureRequest
   *     procedure request
   * @param encounter
   *     encounter
   */
  public void updateProcedureRequest(ProcedureRequest procedureRequest, Encounter encounter) {
    execute(EventType.UPDATE_PROCEDURE_REQUEST, encounter, procedureRequest);
  }
}
