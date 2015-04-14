// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.datafascia.common.api.ApiParams;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.model.PagedCollection;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.persist.PatientRepository;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
   *
   * @param start the starting identifier of the patient
   * @param active the type of patient to return
   * @param count the maximum number of patients to return
   *
   * @return stream list of patients back
   */
  @GET @Timed
  public PagedCollection<Patient> list(
      @QueryParam(ApiParams.START) String start,
      @QueryParam(ApiParams.ACTIVE) Boolean active,
      @DefaultValue("100") @QueryParam(ApiParams.COUNT) int count) {

    if (count < 0) {
      throw new WebApplicationException("Invalid count parameter", Response.Status.BAD_REQUEST);
    }
    Optional<Id<Patient>> optStart = (start == null) ? Optional.empty() : Optional.of(Id.of(start));
    Optional<Boolean> optActive = Optional.ofNullable(active);

    List<Patient> patients = patientRepository.list(optStart, optActive, count + 1);
    PagedCollection<Patient> response = new PagedCollection<>();
    if (patients.size() > count) {
      Map<String, String> params = ImmutableMap.of(
          ApiParams.ACTIVE, Boolean.toString(active),
          ApiParams.COUNT, Integer.toString(count),
          ApiParams.START, patients.get(count).getId().toString());
      response.setNext(params);
      patients.remove(count);
    }
    response.setCollection(patients);

    return response;
  }

  /**
   * Deletes patient.
   *
   * @param patientId
   *     patient ID
   */
  @DELETE @Path("/{patientId}")
  public void delete(@PathParam("patientId") String patientId) {
    patientRepository.delete(Id.of(patientId));
  }
}
