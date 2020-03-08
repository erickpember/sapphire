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
package com.datafascia.api.bundle;

import com.datafascia.api.configurations.APIConfiguration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import javax.servlet.ServletRegistration;
import lombok.extern.slf4j.Slf4j;
import org.atmosphere.guice.AtmosphereGuiceServlet;

/**
 * Initialize the websockets setup through Atmosphere
 */
@Slf4j
public class AtmosphereBundle implements ConfiguredBundle<APIConfiguration> {
  @Override
  public void run(final APIConfiguration configuration, final Environment environment)
      throws Exception {
    log.info("Initializing Atmosphere bundle for Dropwizard.");
    AtmosphereGuiceServlet atmosphereServlet = new AtmosphereGuiceServlet();
    final ServletRegistration.Dynamic websocket =
        environment.servlets().addServlet("websocket", atmosphereServlet);
    websocket.setAsyncSupported(true);
    websocket.addMapping("/websocket/*");
    websocket.setInitParameter("com.sun.jersey.config.property.packages",
        "com.datafascia.api.resources.socket");

    // Set limit on number of processing threads
    atmosphereServlet.framework()
        .addInitParameter("org.atmosphere.cpr.broadcaster.maxProcessingThreads", "10");
  }

  @Override
  public void initialize(final Bootstrap<?> bootstrap) {
  }
}
