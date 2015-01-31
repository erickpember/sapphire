// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.codahale.metrics.MetricRegistry;
import com.datafascia.accumulo.AccumuloConfig;
import com.datafascia.accumulo.AccumuloConnector;
import com.datafascia.api.bundle.AtmosphereBundle;
import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.api.health.AccumuloHealthCheck;
import com.datafascia.common.shiro.FakeRealm;
import com.datafascia.common.shiro.RealmInjectingEnvironmentLoaderListener;
import com.datafascia.common.shiro.RoleExposingRealm;
import com.datafascia.kafka.KafkaConfig;
import com.datafascia.reflections.PackageUtils;
import com.datafascia.urn.URNMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.realm.Realm;
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
  public static final String MODELS_PKG = "com.datafascia.models";

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
    bootstrap.addBundle(new AtmosphereBundle());
  }

  @Override
  public void run(APIConfiguration configuration, Environment environment) {
    URNMap.idNSMapping(MODELS_PKG);
    Injector injector = createInjector(configuration, environment);

    // Register Guice injector
    environment.servlets().addServletListeners(new GuiceServletContextListener() {
      @Override
      protected Injector getInjector() {
        return injector;
      }
    });

    // Authenticator
    environment.servlets()
        .addServletListeners(injector.getInstance(RealmInjectingEnvironmentLoaderListener.class));
    environment.servlets()
        .addFilter(ShiroFilter.class.getSimpleName(), new ShiroFilter())
        .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

    // Resources
    for (Class<?> resClass : PackageUtils.classes(RESOURCES_PKG)) {
      environment.jersey().register(injector.getInstance(resClass));
      log.info("Registering resource class: " + resClass.getCanonicalName());
    }

    // Health checkers
    environment.healthChecks().register(
        "accumulo", injector.getInstance(AccumuloHealthCheck.class));
  }

  /**
   * Create Guice injector with all modules registered
   *
   * @param config the API configuration
   */
  private Injector createInjector(APIConfiguration config, Environment environment) {
    final RoleExposingRealm realm = new FakeRealm();

    return Guice.createInjector(
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(APIConfiguration.class).toInstance(config);
            bind(AccumuloConfig.class).toInstance(config.getAccumuloConfig());
            bind(KafkaConfig.class).toInstance(config.getKafkaConfig());
            bind(MetricRegistry.class).toInstance(environment.metrics());
            bind(Realm.class).toInstance(realm);
            bind(RoleExposingRealm.class).toInstance(realm);
          }
        },
        new AccumuloConnector());
  }
}
