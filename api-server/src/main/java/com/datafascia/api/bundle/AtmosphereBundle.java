// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
      environment.servlets().addServlet("socket", atmosphereServlet);
    websocket.setAsyncSupported(true);
    websocket.addMapping("/socket/*");
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
