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
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.TableNotFoundException;

/**
 * Resource object to model encounter information
 */
@Slf4j @Path("/encounter") @Produces(MediaType.APPLICATION_JSON)
public class EncounterResource {

  private final Connector connect;
  public final String authsString = "System";

  /**
   * Default constructor for patient resource
   *
   * @param connect the connection to Accumulo
   */
  @Inject
  public EncounterResource(Connector connect) {
    this.connect = connect;
  }

  /**
   * @return Return a given encounter by ID.
   */
  @GET @Timed @Path("{id}")
  public Encounter encounter(@Auth User user, @PathParam("id") String id) {
    try {
      EncounterDao dao = new EncounterDao(connect);
      return dao.getEncounter(id, authsString);
    } catch (TableNotFoundException ex) {
      log.error("Error connecting to table.", ex);
      return null;
    }
  }

  /**
   * @return stream list of patients back
   */
  @GET @Timed @Path("last/{patientid}")
  public Encounter getlast(@Auth User user, @PathParam("patientid") String id) {
    try {
      Optional<String> lastvisit = PatientDao.lastVisitId(connect, id);
      if (lastvisit.isPresent()) {
        EncounterDao dao = new EncounterDao(connect);
        return dao.getEncounter(lastvisit.get(), authsString);
      } else {
        return null;
      }
    } catch (TableNotFoundException ex) {
      log.error("Error connecting to table.", ex);
      return null;
    }
  }
}
