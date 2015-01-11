// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.datafascia.accumulo.AccumuloConfig;
import com.datafascia.accumulo.AccumuloConnector;
import com.datafascia.api.bundle.AtmosphereBundle;
import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.api.health.AccumuloHealthCheck;
import com.datafascia.api.resources.PatientResource;
import com.datafascia.api.resources.VersionResource;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

/**
 * The main application class for the Datafascia API end-point as required by the dropwizard
 * framework
 */
@Slf4j
public class APIService extends Application<APIConfiguration> {
  /**
   * The main entry point for the application
   *
   * @param args the command line arguments
   *
   * @throws generic exception to catch all underlying exception types
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
    bootstrap.addBundle(new AssetsBundle("/assets", "/events", "chat.html"));
    bootstrap.addBundle(new AtmosphereBundle());
  }

  @Override
  public void run(APIConfiguration configuration, Environment environment) {
    Injector injector = createInjector(configuration);

    environment.jersey().register(injector.getInstance(PatientResource.class));
    environment.jersey().register(new VersionResource(configuration.getDefaultPackage()));

    // Health checkers
    environment.healthChecks().register("accumulo", injector.getInstance(AccumuloHealthCheck.class));
  }

  /**
   * Create Guice injector with all modules registered
   *
   * @param config the API configuration
   */
  private Injector createInjector(APIConfiguration config) {
    return Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(APIConfiguration.class).toInstance(config);
        bind(AccumuloConfig.class).toInstance(config.getAccumuloConfig());
      }
    }, new AccumuloConnector());
  }
}
