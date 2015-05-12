// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.PatientRepository;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Encounter resource endpoint
 */
@Slf4j @Path("/patient/{patientId}/encounter") @Produces(MediaType.APPLICATION_JSON)
public class EncounterResource {

  private final PatientRepository patientRepository;
  private final EncounterRepository encounterRepository;

  /**
   * Construct resource with associated data access objects
   *
   * @param patientRepository
   *     patient repository
   * @param encounterRepository
   *     encounter repository
   */
  @Inject
  public EncounterResource(
      PatientRepository patientRepository,
      EncounterRepository encounterRepository) {

    this.patientRepository = patientRepository;
    this.encounterRepository = encounterRepository;
  }

  private Patient getPatient(Id<Patient> patientId) {
    Optional<Patient> optionalPatient = patientRepository.read(patientId);
    if (!optionalPatient.isPresent()) {
      throw new WebApplicationException(
          String.format("Patient ID [%s] not found", patientId),
          Response.Status.BAD_REQUEST);
    }

    return optionalPatient.get();
  }

  /**
   * Gets last encounter for a patient.
   *
   * @param patientId
   *     patient ID
   * @return entity
   */
  @GET @Path("/last") @Timed
  public Encounter getLast(@PathParam("patientId") Id<Patient> patientId) {
    Patient patient = getPatient(patientId);

    Optional<Encounter> optionalEncounter =
        encounterRepository.read(patientId, patient.getLastEncounterId());
    if (!optionalEncounter.isPresent()) {
      throw new WebApplicationException(
          "Last encounter not found for patient ID " + patientId, Response.Status.NOT_FOUND);
    }

    return optionalEncounter.get();
  }
}
