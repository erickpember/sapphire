// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.codahale.metrics.MetricRegistry;
import com.datafascia.api.bundle.AtmosphereBundle;
import com.datafascia.api.bundle.FhirBundle;
import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.api.health.AccumuloHealthCheck;
import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.AuthorizationsSupplier;
import com.datafascia.common.accumulo.SubjectAuthorizationsSupplier;
import com.datafascia.common.inject.Injectors;
import com.datafascia.common.kafka.KafkaConfig;
import com.datafascia.common.reflect.PackageUtils;
import com.datafascia.common.shiro.FakeRealm;
import com.datafascia.common.shiro.RealmInjectingEnvironmentLoaderListener;
import com.datafascia.common.shiro.RoleExposingRealm;
import com.datafascia.common.urn.URNMap;
import com.datafascia.domain.model.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
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
  private static final String MODELS_PKG = Version.class.getPackage().getName();

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
    bootstrap.addBundle(new FhirBundle());
  }

  @Override
  public void run(APIConfiguration configuration, Environment environment) {
    URNMap.idNSMapping(MODELS_PKG);
    Injector injector = createInjector(configuration, environment);

    // Setup Jackson
    setupJackson(environment.getObjectMapper());

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
   * @param config
   *     application configuration
   * @param environment
   *     Dropwizard environment
   */
  private Injector createInjector(APIConfiguration config, Environment environment) {
    final RoleExposingRealm realm = new FakeRealm();

    Injectors.overrideWith(
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(APIConfiguration.class).toInstance(config);
            bind(AccumuloConfiguration.class).toInstance(config.getAccumuloConfiguration());
            bind(AuthorizationsSupplier.class).to(SubjectAuthorizationsSupplier.class);
            bind(KafkaConfig.class).toInstance(config.getKafkaConfig());
            bind(MetricRegistry.class).toInstance(environment.metrics());
            bind(ObjectMapper.class).toInstance(environment.getObjectMapper());
            bind(Realm.class).toInstance(realm);
            bind(RoleExposingRealm.class).toInstance(realm);
          }
        });
    return Injectors.getInjector();
  }

  /**
   * Setup application specific Jackson filters, rules etc.
   *
   * @param mapper the Jackson object mapper used by application
   */
  private void setupJackson(ObjectMapper mapper) {
    mapper.findAndRegisterModules();
  }
}
