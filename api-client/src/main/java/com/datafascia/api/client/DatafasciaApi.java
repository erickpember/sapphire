// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import com.datafascia.models.Encounter;
import com.datafascia.models.Patient;
import com.datafascia.models.Version;
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
   */
  @GET("/patient")
  List<Patient> patients(@Query("active") boolean active);

  /**
   * @return An encounter for the id given.
   */
  @GET("/encounter/{id}")
  Encounter encounter(@Path("id") String id);

  /**
   * @return The last encounter for the patient
   */
  @GET("/encounter/last/{patientId}")
  Encounter lastvisit(@Path("patientId") String patientId);

  /**
   * @return Version information for a package on the server.
   */
  @GET("/version")
  Version version();
}
