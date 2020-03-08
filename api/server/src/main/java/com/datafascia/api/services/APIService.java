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
package com.datafascia.api.services;

import com.datafascia.api.bundle.AtmosphereBundle;
import com.datafascia.api.bundle.FhirBundle;
import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.api.health.AccumuloHealthCheck;
import com.datafascia.api.inject.ApplicationModule;
import com.datafascia.common.configuration.guice.ConfigureModule;
import com.datafascia.common.shiro.RealmInjectingEnvironmentLoaderListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Stage;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.TimeZone;
import javax.servlet.DispatcherType;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.servlet.ShiroFilter;

/**
 * The main application class for the Datafascia API end-point as required by the dropwizard
 * framework
 */
@Slf4j
public class APIService extends Application<APIConfiguration> {
  /** Package name for resources */
  public static final String RESOURCES_PKG = "com.datafascia.api.resources";

  private GuiceBundle<APIConfiguration> guiceBundle;

  /**
   * The main entry point for the application
   *
   * @param args the command line arguments
   *
   * @throws Exception generic exception to catch all underlying exception types
   */
  public static void main(String[] args) throws Exception {
    new APIService().run(args);
  }

  @Override
  public String getName() {
    return "datafascia-api";
  }

  @Override
  public void initialize(Bootstrap<APIConfiguration> bootstrap) {
    guiceBundle = GuiceBundle.<APIConfiguration>newBuilder()
        .addModule(new ConfigureModule())
        .addModule(new ApplicationModule())
        .enableAutoConfig(RESOURCES_PKG)
        .setConfigClass(APIConfiguration.class)
        .build(Stage.DEVELOPMENT);
    bootstrap.addBundle(guiceBundle);

    bootstrap.addBundle(new AtmosphereBundle());
    bootstrap.addBundle(guiceBundle.getInjector().getInstance(FhirBundle.class));
  }

  @Override
  public void run(APIConfiguration configuration, Environment environment) {
    // Setup Jackson
    setupJackson(environment.getObjectMapper());

    // Authenticator
    environment.servlets()
        .addServletListeners(
            guiceBundle.getInjector().getInstance(RealmInjectingEnvironmentLoaderListener.class));
    environment.servlets()
        .addFilter(ShiroFilter.class.getSimpleName(), new ShiroFilter())
        .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

    // Health checkers
    environment.healthChecks().register(
        "accumulo", guiceBundle.getInjector().getInstance(AccumuloHealthCheck.class));
  }

  /**
   * Configure application specific Jackson filters, rules etc.
   *
   * @param objectMapper
   *     Jackson object mapper used by application
   */
  private void setupJackson(ObjectMapper objectMapper) {
    objectMapper.findAndRegisterModules();

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    objectMapper.setDateFormat(dateFormat);
  }
}
