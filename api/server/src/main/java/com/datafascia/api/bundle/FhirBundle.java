// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.bundle;

import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.api.resources.fhir.FhirServlet;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import javax.inject.Inject;
import javax.servlet.ServletRegistration;

/**
 * Enables the FHIR servlet.
 */
public class FhirBundle implements ConfiguredBundle<APIConfiguration> {

  @Inject
  private FhirServlet fhirServlet;

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
  }

  @Override
  public void run(APIConfiguration configuration, Environment environment) {
    final ServletRegistration.Dynamic servletRegistration =
        environment.servlets().addServlet("fhir", fhirServlet);
    servletRegistration.setAsyncSupported(true);
    servletRegistration.addMapping("/fhir/*");
    servletRegistration.setInitParameter(
        "com.sun.jersey.config.property.packages", FhirServlet.class.getPackage().getName());
  }
}
