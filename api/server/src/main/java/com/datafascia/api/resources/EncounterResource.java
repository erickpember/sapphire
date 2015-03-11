// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.domain.persist.opal.EncounterDao;
import com.datafascia.models.Encounter;
import com.datafascia.models.Patient;
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
 * Resource object to model encounter information
 */
@Slf4j @Path("/encounter") @Produces(MediaType.APPLICATION_JSON)
public class EncounterResource {

  private final PatientRepository patientRepository;
  private final EncounterRepository encounterRepository;
  private final EncounterDao encounterDao;

  /**
   * Construct resource with associated data access objects
   *
   * @param patientRepository
   *     patient repository
   * @param encounterRepository
   *     encounter repository
   * @param encounterDao
   *     encounter data access object
   */
  @Inject
  public EncounterResource(
      PatientRepository patientRepository,
      EncounterRepository encounterRepository,
      EncounterDao encounterDao) {

    this.patientRepository = patientRepository;
    this.encounterRepository = encounterRepository;
    this.encounterDao = encounterDao;
  }

  /**
   * @param id the encounter identifier
   *
   * @return Return a given encounter by ID.
   */
  @GET @Timed @Path("{id}")
  public Encounter encounter(@PathParam("id") String id) {
    return encounterDao.getEncounter(id);
  }

  /**
   * Gets last encounter for a patient.
   *
   * @param aPatientId
   *     patient ID
   * @return entity
   */
  @GET @Timed @Path("last/{patientId}")
  public Encounter getLast(@PathParam("patientId") String aPatientId) {
    Id<Patient> patientId = Id.of(aPatientId);
    Optional<Patient> optionalPatient = patientRepository.read(patientId);
    if (!optionalPatient.isPresent()) {
      throw new WebApplicationException("Patient not found", Response.Status.NOT_FOUND);
    }

    Patient patient = optionalPatient.get();
    Optional<Encounter> optionalEncounter =
        encounterRepository.read(patientId, patient.getLastEncounterId());
    if (!optionalPatient.isPresent()) {
      throw new WebApplicationException("Last encounter not found", Response.Status.NOT_FOUND);
    }

    return optionalEncounter.get();
  }
}
