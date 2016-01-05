// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.valueset.EncounterLocationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.common.configuration.ConfigurationNode;
import com.datafascia.common.configuration.Configure;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.LocationRepository;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceUpdater;
import com.datafascia.etl.hl7.EncounterStatusTransition;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Admits, transfers, updates patient.
 */
@ConfigurationNode("UpdateHarmEvidence")
@Slf4j
public class AdmitPatient {

  private static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults();

  @Configure
  @Inject
  private String pointOfCare;

  private Set<String> icuPointsOfCare;

  @Inject
  private EncounterStatusTransition encounterStatusTransition;

  @Inject
  private PatientRepository patientRepository;

  @Inject
  private LocationRepository locationRepository;

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private HarmEvidenceUpdater harmEvidenceUpdater;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if trigger event is admit or transfer.
   *
   * @param triggerEvent
   *     trigger event
   * @return true if trigger event is admit or transfer
   */
  public static boolean isAdmitOrTransfer(String triggerEvent) {
    return "A01".equals(triggerEvent) || "A02".equals(triggerEvent);
  }

  private Optional<String> getPointOfCare(Location location) {
    String[] locationParts = location.getIdentifierFirstRep().getValue().split("\\^");
    if (locationParts.length > 0) {
      String pointOfCare = locationParts[0];
      return Optional.of(pointOfCare);
    } else {
      return Optional.empty();
    }
  }

  private synchronized Set<String> getIcuPointsOfCare() {
    if (icuPointsOfCare == null) {
      icuPointsOfCare = new HashSet<>();
      COMMA_SPLITTER.split(pointOfCare)
          .forEach(p -> icuPointsOfCare.add(p));
    }

    return icuPointsOfCare;
  }

  private boolean isIcu(Location location) {
    Optional<String> pointOfCare = getPointOfCare(location);
    if (!pointOfCare.isPresent()) {
      return false;
    }

    return getIcuPointsOfCare().contains(pointOfCare.get());
  }

  private Location getIcuLocation() {
    final String identifier = "ANY-ICU";
    Location location = new Location();
    location.setId(identifier);
    location.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_LOCATION)
        .setValue(identifier);
    return location;
  }

  /**
   * Admits patient.
   *
   * @param triggerEvent
   *     HL7 message trigger event
   * @param messageDateTime
   *     HL7 message date time
   * @param patient
   *     patient
   * @param location
   *     location
   * @param newEncounter
   *     encounter
   */
  public void accept(
      String triggerEvent,
      DateTimeDt messageDateTime,
      UnitedStatesPatient patient,
      Location location,
      Encounter newEncounter) {

    patientRepository.save(patient);

    if (Strings.isNullOrEmpty(location.getIdentifierFirstRep().getValue())) {
      log.error("Discarded location with missing identifier for encounter ID {}, trigger event {}",
          newEncounter.getIdentifierFirstRep().getValue(), triggerEvent);
    } else {
      locationRepository.save(location);
    }

    newEncounter
        .setPatient(new ResourceReferenceDt(patient))
        .addLocation()
            .setLocation(new ResourceReferenceDt(location))
            .setStatus(EncounterLocationStatusEnum.PRESENT);

    Id<Encounter> encounterId = EncounterRepository.generateId(newEncounter);
    Optional<Encounter> currentEncounter = encounterRepository.read(encounterId);

    encounterStatusTransition.updateEncounterStatus(triggerEvent, currentEncounter, newEncounter);

    if (currentEncounter.isPresent() && currentEncounter.get().getLocation().size() > 1) {
      // Preserve ICU admit time from current encounter.
      newEncounter.addLocation(currentEncounter.get().getLocation().get(1));
    } else if (isIcu(location)) {
      // The first time the patient is admitted or transferred into an ICU, add a dummy location to
      // the encounter. Store the ICU admit time in the location period start.
      PeriodDt period = new PeriodDt()
          .setStart(messageDateTime);
      newEncounter.addLocation()
          .setLocation(new ResourceReferenceDt(getIcuLocation()))
          .setPeriod(period);
    }

    encounterRepository.save(newEncounter);
    apiClient.invalidateEncounter(newEncounter.getId().getIdPart());

    if (isAdmitOrTransfer(triggerEvent)) {
      harmEvidenceUpdater.admitPatient(patient, location, newEncounter);
    } else {
      harmEvidenceUpdater.updatePatient(patient, location, newEncounter);
    }
  }
}
