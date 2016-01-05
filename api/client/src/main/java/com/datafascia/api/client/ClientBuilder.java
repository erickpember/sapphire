// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import com.datafascia.common.configuration.ConfigurationNode;
import com.datafascia.common.configuration.Configure;
import com.google.common.base.Strings;
import javax.inject.Inject;

/**
 * A builder for resource clients.
 */
public class ClientBuilder {
  private final IGenericClient client;
  private EncounterClient encounterClient;
  private MedicationClient medicationClient;
  private ObservationClient observationClient;
  private PractitionerClient practitionerClient;

  /**
   * Constructor
   *
   * @param fhirContext
   *     FHIR context
   * @param config
   *     API client configuration
   */
  @Inject
  public ClientBuilder(FhirContext fhirContext, ClientConfiguration config) {
    client = fhirContext.newRestfulGenericClient(config.endpoint);
    if (!Strings.isNullOrEmpty(config.username) && !Strings.isNullOrEmpty(config.password)) {
      client.registerInterceptor(new BasicAuthInterceptor(config.username, config.password));
    }
  }

  /**
   * @return encounter client
   */
  public synchronized EncounterClient getEncounterClient() {
    if (encounterClient == null) {
      encounterClient = new EncounterClient(client);
    }
    return encounterClient;
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

  /**
   * @return medication client
   */
  public synchronized MedicationClient getMedicationClient() {
    if (medicationClient == null) {
      medicationClient = new MedicationClient(client);
    }
    return medicationClient;
  }

  /**
   * @return practitioner client
   */
  public synchronized PractitionerClient getPractitionerClient() {
    if (practitionerClient == null) {
      practitionerClient = new PractitionerClient(client);
    }
    return practitionerClient;
  }

  public SubstanceClient getSubstanceClient() {
    return new SubstanceClient(client);
  }

  /**
   * Invalidates cache entry for an encounter.
   *
   * @param encounterId
   *     encounter ID
   */
  public void invalidateEncounter(String encounterId) {
    getEncounterClient().invalidate(encounterId);
  }

  /**
   * Invalidates cache entry for a medication
   *
   * @param medicationId
   *     medication ID
   */
  public void invalidateMedication(String medicationId) {
    getMedicationClient().invalidate(medicationId);
  }

  /**
   * Invalidates observation cache entry for an encounter.
   *
   * @param encounterId
   *     encounter ID
   */
  public void invalidateObservations(String encounterId) {
    getObservationClient().invalidate(encounterId);
  }

  /**
   * Invalidates cache entry for a practitioner.
   *
   * @param practitionerId
   *     practitioner ID
   */
  public void invalidatePractitioner(String practitionerId) {
    getPractitionerClient().invalidate(practitionerId);
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
