// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.datafascia.api.authenticator.User;
import com.datafascia.dao.EncounterDao;
import com.datafascia.dao.PatientDao;
import com.datafascia.models.Encounter;
import io.dropwizard.auth.Auth;
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
import org.apache.accumulo.core.client.Connector;

/**
 * Resource object to model encounter information
 */
@Slf4j @Path("/encounter") @Produces(MediaType.APPLICATION_JSON)
public class EncounterResource {

  private final Connector connect;
  private PatientDao patientDao;
  public final String authsString = "System";

  @Inject
  public EncounterResource(Connector connect, PatientDao patientDao) {
    this.connect = connect;
    this.patientDao = patientDao;
  }

  /**
   * @return Return a given encounter by ID.
   */
  @GET @Timed @Path("{id}")
  public Encounter encounter(@Auth User user, @PathParam("id") String id) {
    EncounterDao dao = new EncounterDao(connect);
    return dao.getEncounter(id, authsString);
  }

  /**
   * @return stream list of patients back
   */
  @GET @Timed @Path("last/{patientid}")
  public Encounter getlast(@Auth User user, @PathParam("patientid") String id) {
    Optional<String> lastVisit = patientDao.lastVisitId(id);
    if (!lastVisit.isPresent()) {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    EncounterDao dao = new EncounterDao(connect);
    return dao.getEncounter(lastVisit.get(), authsString);
  }
}
