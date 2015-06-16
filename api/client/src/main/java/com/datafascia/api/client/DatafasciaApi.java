// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import com.datafascia.common.api.ApiParams;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Observation;
import com.datafascia.domain.model.PagedCollection;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.model.Version;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import java.util.List;
import java.util.Map;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Base interface for interacting with the dataFascia API.
 */
public interface DatafasciaApi {

  /**
   * Creates patient.
   *
   * @param patient
   *     patient values
   * @return HTTP response
   */
  @POST("/patient")
  Response createPatient(@Body Patient patient);

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
  @PUT("/patient/{patientId}")
  Patient updatePatient(@Path("patientId") String patientId, @Body Patient patient);

  /**
   * @param start the starting patient id
   * @param active boolean indicating type of patient to return
   * @param count maximum number of patients to return in page
   *
   * @return list of all patients.
   */
  @GET("/patient")
  PagedCollection<Patient> patients(
      @Query(ApiParams.START) String start,
      @Query(ApiParams.ACTIVE) boolean active,
      @Query(ApiParams.COUNT) int count);

  /**
   * @param params the query parameters as a map
   *
   * @return list of patients
   */
  @GET("/patient")
  PagedCollection<Patient> patients(@QueryMap Map<String, String> params);

  /**
   * Deletes patient.
   *
   * @param patientId
   *     patient ID
   * @return HTTP response
   */
  @DELETE("/patient/{patientId}")
  Response deletePatient(@Path("patientId") String patientId);

  /**
   * @param patientId the unique identifier for the patient
   *
   * @return last encounter for the patient
   */
  @GET("/patient/{patientId}/encounter/last")
  Encounter lastvisit(@Path("patientId") String patientId);

  /**
   * Deletes encounters and all of their children for a patient.
   *
   * @param patientId
   *     patient ID
   * @return HTTP response
   */
  @DELETE("/patient/{patientId}/encounter")
  Response deleteEncounters(@Path("patientId") String patientId);

  /**
   * @param packageName Specify the name of the package.
   *
   * @return version information for the package on the server.
   */
  @GET("/version")
  Version version(@Query(ApiParams.PACKAGE) String packageName);

  /**
   * @return A list of schemas provided by the API.
   */
  @GET("/schema")
  List<JsonSchema> schemas();

  /**
   * @param modelName Name of the model to produce a schema for.
   *
   * @return schema associated with the model
   */
  @GET("/schema/{modelName}")
  JsonSchema schema(@Path("modelName") String modelName);

  /**
   * @param patientId Id of the patient to load observations for.
   * @param startTime starting ISO_DATE_TIME bound for the query. (inclusive)
   * @param endTime ending ISO_DATE_TIME bound for the query. (exclusive)
   *
   * @return A list of observations
   */
  @GET("/observation")
  List<Observation> findObservations(
      @Query(ApiParams.PATIENT_ID) String patientId,
      @Query(ApiParams.START_TIME) String startTime,
      @Query(ApiParams.END_TIME) String endTime);
}
