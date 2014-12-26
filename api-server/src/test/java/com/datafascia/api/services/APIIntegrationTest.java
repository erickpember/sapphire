// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.dropwizard.testing.DropwizardTestApp;
import com.datafascia.models.Version;
import com.google.common.base.Optional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Integration test for the various API resources
 */
@Slf4j
public class APIIntegrationTest {
  DropwizardTestApp<APIConfiguration> app;

  @BeforeSuite
  public void before() throws Exception {
    app = new DropwizardTestApp<APIConfiguration>(APIService.class, "api.yml");
    app.start();
  }

  @AfterSuite
  public void after() throws Exception {
    app.stop();
  }

  @Test
  public void testVersion() {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target("http://localhost:" + app.getLocalPort() + "/version");
    final Optional<String> packageName = Optional.fromNullable("com.datafascia.models");
    Version version = target.queryParam("package", packageName.get()).request().get(Version.class);
    assertEquals(version.getId(), 1);
    assertEquals(version.getVendor(), "dataFascia Corporation");
  }
}
