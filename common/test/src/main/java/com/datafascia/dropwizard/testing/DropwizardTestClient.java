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

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Environment;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;

/**
 * Test your HTTP client code by writing a JAX-RS test double class and let this rule start and
 * stop a Dropwizard application containing your doubles.
 * <p>
 * Example:
 * <pre><code>
    {@literal @}Path("/ping")
    public static class PingResource {
        {@literal @}GET
        public String ping() {
            return "pong";
        }
    }

    {@literal @}ClassRule
    public static DropwizardTestClient dropwizard = new DropwizardTestClient(new PingResource());

    {@literal @}Test
    public void shouldPing() throws IOException {
        URL url = new URL(dropwizard.baseUri() + "/ping");
        String response = new BufferedReader(new InputStreamReader(url.openStream())).readLine();
        assertEquals("pong", response);
    }
</code></pre>
 * Of course, you'd use your http client, not {@link URL#openStream()}.
 * </p>
 * <p>
 * The {@link DropwizardTestClient} takes care of:
 * <ul>
 * <li>Creating a simple default configuration.</li>
 * <li>Creating a simplistic application.</li>
 * <li>Adding a dummy health check to the application to suppress the startup warning.</li>
 * <li>Adding your resources to the application.</li>
 * <li>Choosing a free random port number.</li>
 * <li>Starting the application.</li>
 * <li>Stopping the application.</li>
 * </ul>
 * </p>
 */
@Slf4j
public class DropwizardTestClient {
  private final Object[] resources;
  private final DropwizardTestApp<Configuration> testApp;

  /**
   * Construct client for resources
   *
   * @param resources the list of resources
   */
  public DropwizardTestClient(Object... resources) {
    testApp = new DropwizardTestApp<Configuration>(null, null) {
      @Override
      public Application<Configuration> newApplication() {
        return new FakeApplication();
      }
    };
    this.resources = resources;
  }

  /**
   * @return the basic URI for the server
   */
  public URI baseUri() {
    return URI.create("http://localhost:" + testApp.getLocalPort() + "/application");
  }

  /**
   * Start the client
   *
   * @throws Exception any error by underlying server
   */
  public void start() throws Exception {
    testApp.start();
  }

  /**
   * Stop the client
   *
   * @throws Exception any error by underlying server
   */
  public void stop() throws Exception {
    testApp.stop();
  }

  private static class DummyHealthCheck extends HealthCheck {
    @Override
    protected Result check() {
      return Result.healthy();
    }
  }

  private class FakeApplication extends Application<Configuration> {
    @Override
    public void run(Configuration configuration, Environment environment) {
      final SimpleServerFactory serverConfig = new SimpleServerFactory();
      configuration.setServerFactory(serverConfig);
      final HttpConnectorFactory connectorConfig =
          (HttpConnectorFactory) serverConfig.getConnector();
      connectorConfig.setPort(0);

      environment.healthChecks().register("dummy", new DummyHealthCheck());
      for (Object resource : resources) {
        environment.jersey().register(resource);
      }
    }
  }
}
