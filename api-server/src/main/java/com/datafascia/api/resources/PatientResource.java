// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.datafascia.common.api.ApiParams;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.models.Patient;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

/**
 * Resource object to model patient information
 */
@Slf4j @Path("/patient") @Produces(MediaType.APPLICATION_JSON)
public class PatientResource {

  private final PatientRepository patientRepository;

  /**
   * Construct resource with the relevant data access object
   *
   * @param patientRepository the patient data access object (can be injected)
   */
  @Inject
  public PatientResource(PatientRepository patientRepository) {
    this.patientRepository = patientRepository;
  }

  /**
   * @param active the type of patient to return
   *
   * @return stream list of patients back
   */
  @GET @Timed
  public List<Patient> list(@DefaultValue("true") @QueryParam(ApiParams.ACTIVE) boolean active) {
    return patientRepository.list(Optional.of(active));
  }
}
