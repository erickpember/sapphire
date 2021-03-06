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
package com.datafascia.dropwizard.testing;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.cli.ServerCommand;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.argparse4j.inf.Namespace;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;

/**
 * A container for starting and stopping your application at the start and end of a test class.
 *
 * <p>
 * By default, the {@link Application} will be constructed using reflection to invoke the nullary
 * constructor. If your application does not provide a public nullary constructor, you will need to
 * override the {@link #newApplication()} method to provide your application instance(s).
 * </p>
 *
 * @param <C> the configuration type
 */
@Slf4j
public class DropwizardTestApp<C extends Configuration> {
  private final Class<? extends Application<C>> applicationClass;
  private final String configPath;
  private C configuration;
  private Application<C> application;
  private Environment environment;
  private Server jetty;

  /**
   * Default constructor
   *
   * @param applicationClass the application class to test
   * @param configPath the path to application configuration file
   * @param configOverrides configuration parameter overrides
   */
  public DropwizardTestApp(Class<? extends Application<C>> applicationClass,
      @Nullable String configPath, ConfigOverride... configOverrides) {
    this.applicationClass = applicationClass;
    this.configPath = configPath;
    for (ConfigOverride configOverride : configOverrides) {
      configOverride.override();
    }
  }

  /**
   * Start the application server
   *
   * @throws Exception for underlying problems
   */
  public void start() throws Exception {
    if (jetty != null) {
      log.warn("Ignoring restart of Dropwizard application instance: " + application.getName());
      return;
    }

    application = newApplication();
    final Bootstrap<C> bootstrap = new Bootstrap<C>(application) {
      @Override
      public void run(C configuration, Environment environment) throws Exception {
        environment.lifecycle().addServerLifecycleListener(new ServerLifecycleListener() {
          @Override
          public void serverStarted(Server server) {
            jetty = server;
          }
        });
        DropwizardTestApp.this.configuration = configuration;
        DropwizardTestApp.this.environment = environment;
        super.run(configuration, environment);
      }
    };

    application.initialize(bootstrap);
    final ServerCommand<C> command = new ServerCommand<>(application);
    ImmutableMap.Builder<String, Object> file = ImmutableMap.builder();
    if (!Strings.isNullOrEmpty(configPath)) {
      file.put("file", configPath);
    }
    final Namespace namespace = new Namespace(file.build());
    log.info("Starting Dropwizard application instance: " + application.getName());
    command.run(bootstrap, namespace);
  }

  /**
   * Stop the application server
   *
   * @throws Exception by the underlying Jetty server
   */
  public void stop() throws Exception {
    if (jetty == null) {
      log.warn("Ignoring stop of uninitialized application instance.");
      return;
    }

    try {
      jetty.stop();
    } finally {
      jetty = null;
      configuration = null;
      application = null;
      environment = null;
      ConfigOverride.reset();
    }
  }

  /**
   * @return the application configuration
   */
  public C getConfiguration() {
    return configuration;
  }

  /**
   * @return actual port the server is listening on, or
   * -1 if it has not been opened, or -2 if it has been closed.
   */
  public int getLocalPort() {
    return ((NetworkConnector) jetty.getConnectors()[0]).getLocalPort();
  }

  /**
   * @return the administrative port for the server
   */
  public int getAdminPort() {
    return ((NetworkConnector) jetty.getConnectors()[1]).getLocalPort();
  }

  /**
   * @return new instance of the application
   */
  public Application<C> newApplication() {
    try {
      return applicationClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @param <A> the application type
   *
   * @return the underlying running application
   */
  @SuppressWarnings("unchecked")
  public <A extends Application<C>> A getApplication() {
    return (A) application;
  }

  /**
   * @return the application environment
   */
  public Environment getEnvironment() {
    return environment;
  }
}
