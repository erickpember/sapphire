// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.datafascia.api.responses.IteratorResponse;
import com.datafascia.dao.PatientDao;
import com.datafascia.models.Patient;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import lombok.extern.slf4j.Slf4j;

/**
 * Resource object to model patient information
 */
@Slf4j @Path("/patient") @Produces(MediaType.APPLICATION_JSON)
public class PatientResource {

  private PatientDao patientDao;

  /**
   * Construct resource with the relevant data access object
   *
   * @param patientDao the patient data access object (can be injected)
   */
  @Inject
  public PatientResource(PatientDao patientDao) {
    this.patientDao = patientDao;
  }

  /**
   * @param active the type of patient to return
   *
   * @return stream list of patients back
   */
  @GET @Timed
  public IteratorResponse<Patient> patients(
      @DefaultValue("true") @QueryParam("active") boolean active) {
    return new IteratorResponse<Patient>(patientDao.patients(active));
  }
}
