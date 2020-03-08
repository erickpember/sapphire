// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
