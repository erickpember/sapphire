// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import com.datafascia.common.api.ApiParams;
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
   * @param active boolean indicating type of patient to return
   *
   * @return list of all patients.
   */
  @GET("/patient")
  List<Patient> patients(@Query(ApiParams.ACTIVE) boolean active);

  /**
   * @param encounterId the unique identifier for the encounter
   *
   * @return encounter associated with the identifier
   */
  @GET("/encounter/{encounterId}")
  Encounter encounter(@Path("encounterId") String encounterId);

  /**
   * @param patientId the unique identifier for the patient
   *
   * @return last encounter for the patient
   */
  @GET("/encounter/last/{patientId}")
  Encounter lastvisit(@Path("patientId") String patientId);

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
