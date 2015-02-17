// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import com.datafascia.models.Encounter;
import com.datafascia.models.Observation;
import com.datafascia.models.Patient;
import com.datafascia.models.Version;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Base interface for interacting with the dataFascia API.
 */
public interface DatafasciaApi {
  /**
   * @return list of all patients.
   *
   * @param active boolean indicating type of patient to return
   */
  @GET("/patient")
  List<Patient> patients(@Query("active") boolean active);

  /**
   * @param encounterId ID of the encounter
   * @return An encounter for the id given.
   *
   * @param id the unique identifier for the encounter
   */
  @GET("/encounter/{encounterId}")
  Encounter encounter(@Path("encounterId") String encounterId);

  /**
   * @param patientId ID of the patient.
   * @return The last encounter for the patient
   *
   * @param patientId the identity of the patient
   */
  @GET("/encounter/last/{patientId}")
  Encounter lastvisit(@Path("patientId") String patientId);

  /**
   * @param packageName Specify the name of the package.
   * @return Version information for a package on the server.
   */
  @GET("/version")
  Version version(@Query("package") String packageName);

  /**
   * @return A list of schemas used by the API.
   */
  @GET("/schema")
  List<JsonSchema> schemas();

  /**
   * @param modelName Name of the model to produce a schema for.
   * @return A schema used by the API.
   */
  @GET("/schema/{modelName}")
  JsonSchema schema(@Path("modelName") String modelName);

  /**
   * @param patientId ID of the patient to load observations for.
   * @param startCaptureTimeString The starting ISO_DATE_TIME bound for the query. (inclusive)
   * @param endCaptureTimeString The ending ISO_DATE_TIME bound for the query. (exclusive)
   * @return A list of observations
   */
  @GET("/observation")
  List<Observation> findObservations(
      @Query("patientId") String patientId,
      @Query("startCaptureTime") String startCaptureTimeString,
      @Query("endCaptureTime") String endCaptureTimeString);
}
