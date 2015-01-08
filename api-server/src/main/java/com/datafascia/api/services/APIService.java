// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.datafascia.api.bundle.AtmosphereBundle;
import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.api.health.PackageHealthCheck;
import com.datafascia.api.resources.VersionResource;
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
    final VersionResource resource = new VersionResource(configuration.getDefaultPackage());
    final PackageHealthCheck healthCheck =
      new PackageHealthCheck(configuration.getDefaultPackage());

    environment.healthChecks().register("packageName", healthCheck);
    environment.jersey().register(resource);
  }
}
