// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.bundle;

import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.api.servlets.FhirServlet;
import com.google.inject.Injector;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import javax.servlet.ServletRegistration;
import lombok.extern.slf4j.Slf4j;

/**
 * Initialize the FHIR web API setup
 */
@Slf4j
public class FhirBundle implements ConfiguredBundle<APIConfiguration> {

  private Injector injector;

  /**
   * Constructor
   *
   * @param injector
   *     Guice injector
   */
  public FhirBundle(Injector injector) {
    this.injector = injector;
  }

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
  }

  @Override
  public void run(APIConfiguration configuration, Environment environment) {
    log.info("Initializing Hapi Fhir bundle for Dropwizard.");
    FhirServlet fhirServlet = new FhirServlet(injector);
    final ServletRegistration.Dynamic fhirServletRegistratration =
        environment.servlets().addServlet("fhir", fhirServlet);
    fhirServletRegistratration.setAsyncSupported(true);
    fhirServletRegistratration.addMapping("/fhir/*");
    fhirServletRegistratration.setInitParameter("com.sun.jersey.config.property.packages",
        "com.datafascia.api.resources.fhir");
  }
}
