// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.datafascia.api.authenticator.User;
import com.datafascia.api.responses.IteratorResponse;
import com.datafascia.dao.PatientDao;
import com.datafascia.models.Patient;
import io.dropwizard.auth.Auth;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;

/**
 * Resource object to model patient information
 */
@Slf4j @Path("/patient") @Produces(MediaType.APPLICATION_JSON)
public class PatientResource {
  private final Connector connect;

  /**
   * Default constructor for patient resource
   *
   * @param connect the connection to Accumulo
   */
  @Inject
  public PatientResource(Connector connect) {
    this.connect = connect;
  }

  /**
   * @return stream list of patients back
   */
  @GET @Timed
  public IteratorResponse<Patient> patients(@Auth User user) {
    return new IteratorResponse<Patient>(PatientDao.patients(connect));
  }
}
