// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.datafascia.dao.EncounterDao;
import com.datafascia.dao.PatientDao;
import com.datafascia.models.Encounter;
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

  private PatientDao patientDao;
  private EncounterDao encounterDao;

  /**
   * Construct resource with associated data access objects
   *
   * @param patientDao the patient data access object
   * @param encounterDao the encounter data access object
   */
  @Inject
  public EncounterResource(PatientDao patientDao, EncounterDao encounterDao) {
    this.patientDao = patientDao;
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
   * @param id the patient identifier
   *
   * @return stream list of patients back
   */
  @GET @Timed @Path("last/{patientid}")
  public Encounter getlast(@PathParam("patientid") String id) {
    Optional<String> lastVisit = patientDao.getLastVisitId(id);
    if (!lastVisit.isPresent()) {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    return encounterDao.getEncounter(lastVisit.get());
  }
}
