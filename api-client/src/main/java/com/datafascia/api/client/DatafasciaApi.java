// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import com.datafascia.models.Patient;
import com.datafascia.models.Version;
import java.util.List;
import retrofit.http.GET;

/**
 * Base interface for interacting with the dataFascia API.
 */
public interface DatafasciaApi {
  /**
   * @return list of all patients.
   */
  @GET("/patient")
  List<Patient> patients();

  /**
   * @return Version information for a package on the server.
   */
  @GET("/version")
  Version version();
}
