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
