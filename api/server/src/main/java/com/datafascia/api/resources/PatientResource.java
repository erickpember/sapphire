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
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * Patient resource endpoint
 */
@Slf4j @Path("/patient") @Produces(MediaType.APPLICATION_JSON)
public class PatientResource {

  private final PatientRepository patientRepository;

  @Context
  private UriInfo uriInfo;

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
   * Creates patient.
   *
   * @param patient
   *     patient values
   * @return HTTP response. On success, status code is 201 and Location header
   * contains created resource URI.
   */
  @Consumes(MediaType.APPLICATION_JSON) @POST
  public Response create(Patient patient) {
    // Check if patient already exists.
    Id<Patient> patientId = PatientRepository.getEntityId(patient);
    Optional<Patient> optionalPatient = patientRepository.read(patientId);
    if (optionalPatient.isPresent()) {
      throw new WebApplicationException(
          String.format("Patient ID [%s] already exists", patientId),
          Response.Status.BAD_REQUEST);
    }

    patientRepository.save(patient);

    URI createdUri = uriInfo.getAbsolutePathBuilder()
        .path(patient.getId().toString())
        .build();
    return Response.created(createdUri).build();
  }

  /**
   * Completely replaces the content of the patient resource with the content
   * given in the request.
   *
   * @param patientId
   *     patient ID
   * @param patient
   *     new patient values
   * @return patient
   */
  @Consumes(MediaType.APPLICATION_JSON) @Path("/{patientId}") @PUT
  public Patient update(@PathParam("patientId") Id<Patient> patientId, Patient patient) {
    patientRepository.save(patient);
    return patient;
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
  public void delete(@PathParam("patientId") Id<Patient> patientId) {
    patientRepository.delete(patientId);
  }
}
