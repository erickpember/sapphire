// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import com.datafascia.common.configuration.ConfigurationNode;
import com.datafascia.common.configuration.Configure;
import com.datafascia.common.configuration.guice.ConfigureModule;
import com.google.common.base.Strings;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * A builder for resource clients.
 */
public class ClientBuilder {
  private static final FhirContext ctx = FhirContext.forDstu2();
  private final IGenericClient client;
  private static Injector injector;
  private ObservationClient observationClient;

  /**
   * Constructs a ClientBuilder using local config.
   */
  public ClientBuilder() {
    injector = Guice.createInjector(
        new ConfigureModule() {
        }
    );
    ClientConfiguration config = injector.getInstance(ClientConfiguration.class);

    client = ctx.newRestfulGenericClient(config.endpoint);
    if (!Strings.isNullOrEmpty(config.username) && !Strings.isNullOrEmpty(config.password)) {
      client.registerInterceptor(new BasicAuthInterceptor(config.username, config.password));
    }
  }

  /**
   * Constructs a ClientBuilder.
   *
   * @param apiEndpoint The FHIR API endpoint to connect to.
   * @param username The username to authenticate with.
   * @param password The password to authenticate with.
   */
  public ClientBuilder(String apiEndpoint, String username, String password) {
    client = ctx.newRestfulGenericClient(apiEndpoint);
    if (!Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(password)) {
      client.registerInterceptor(new BasicAuthInterceptor(username, password));
    }
  }

  public EncounterClient getEncounterClient() {
    return new EncounterClient(client);
  }

  public FlagClient getFlagClient() {
    return new FlagClient(client);
  }

  public MedicationAdministrationClient getMedicationAdministrationClient() {
    return new MedicationAdministrationClient(client);
  }

  public MedicationOrderClient getMedicationOrderClient() {
    return new MedicationOrderClient(client);
  }

  /**
   * @return observation client
   */
  public synchronized ObservationClient getObservationClient() {
    if (observationClient == null) {
      observationClient = new ObservationClient(client);
    }
    return observationClient;
  }

  public ProcedureRequestClient getProcedureRequestClient() {
    return new ProcedureRequestClient(client);
  }

  public ProcedureClient getProcedureClient() {
    return new ProcedureClient(client);
  }

  public MedicationClient getMedicationClient() {
    return new MedicationClient(client);
  }

  public PractitionerClient getPractitionerClient() {
    return new PractitionerClient(client);
  }

  public SubstanceClient getSubstanceClient() {
    return new SubstanceClient(client);
  }

  /**
   * Invalidates cache entry for encounter.
   *
   * @param encounterId
   *     encounter ID
   */
  public void invalidate(String encounterId) {
    getObservationClient().invalidate(encounterId);
  }

  /**
   * Configuration for API client.
   */
  @ConfigurationNode("df-api")
  public static class ClientConfiguration {
    @Configure
    private String endpoint;
    @Configure
    private String username;
    @Configure
    private String password;
  }
}
