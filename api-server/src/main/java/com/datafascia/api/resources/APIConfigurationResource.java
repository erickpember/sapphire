// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.datafascia.api.configurations.APIConfiguration;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

/**
 * Resource object to model API server configuration
 */
@Slf4j @Path("/server/configuration") @Produces(MediaType.APPLICATION_JSON)
public class APIConfigurationResource {
  private final APIConfiguration config;

  /**
   * Default constructor for server configuration resource
   *
   * @param config the API server configuration
   */
  @Inject
  public APIConfigurationResource(APIConfiguration config) {
    this.config = config;
  }

  /**
   * @return the server configuration
   */
  @GET @Timed
  public APIConfiguration configuration() {
    return config;
  }
}
