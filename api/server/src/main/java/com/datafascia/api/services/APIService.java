// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.datafascia.api.bundle.AtmosphereBundle;
import com.datafascia.api.bundle.FhirBundle;
import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.api.health.AccumuloHealthCheck;
import com.datafascia.api.inject.ApplicationModule;
import com.datafascia.common.configuration.guice.ConfigureModule;
import com.datafascia.common.shiro.RealmInjectingEnvironmentLoaderListener;
import com.datafascia.common.urn.URNMap;
import com.datafascia.domain.model.Version;
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
  /** Package name for models */
  private static final String MODELS_PKG = Version.class.getPackage().getName();

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
    bootstrap.addBundle(new FhirBundle(guiceBundle.getInjector()));
  }

  @Override
  public void run(APIConfiguration configuration, Environment environment) {
    URNMap.idNSMapping(MODELS_PKG);

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
