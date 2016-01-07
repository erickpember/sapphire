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
  private MedicationAdministrationClient medicationAdministrationClient;
  private MedicationOrderClient medicationOrderClient;
  private ObservationClient observationClient;
  private PractitionerClient practitionerClient;
  private ProcedureClient procedureClient;
  private ProcedureRequestClient procedureRequestClient;

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

  /**
   * @return observation client
   */
  public synchronized ObservationClient getObservationClient() {
    if (observationClient == null) {
      observationClient = new ObservationClient(client);
    }
    return observationClient;
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
   * @return medicationAdministration client
   */
  public synchronized MedicationAdministrationClient getMedicationAdministrationClient() {
    if (medicationAdministrationClient == null) {
      medicationAdministrationClient = new MedicationAdministrationClient(client);
    }
    return medicationAdministrationClient;
  }

  /**
   * @return medicationOrder client
   */
  public synchronized MedicationOrderClient getMedicationOrderClient() {
    if (medicationOrderClient == null) {
      medicationOrderClient = new MedicationOrderClient(client);
    }
    return medicationOrderClient;
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

  /**
   * @return procedure client
   */
  public synchronized ProcedureClient getProcedureClient() {
    if (procedureClient == null) {
      procedureClient = new ProcedureClient(client);
    }
    return procedureClient;
  }

  /**
   * @return procedure request client
   */
  public synchronized ProcedureRequestClient getProcedureRequestClient() {
    if (procedureRequestClient == null) {
      procedureRequestClient = new ProcedureRequestClient(client);
    }
    return procedureRequestClient;
  }

  public SubstanceClient getSubstanceClient() {
    return new SubstanceClient(client);
  }

  /**
   * Invalidates cache entry for an encounter and encounter-linked resource search result caches.
   *
   * @param encounterId
   *     encounter ID
   */
  public void invalidateEncounter(String encounterId) {
    getEncounterClient().invalidate(encounterId);
    invalidateMedicationOrders(encounterId);
    invalidateMedicationAdministrations(encounterId);
    invalidateObservations(encounterId);
    invalidateProcedureRequests(encounterId);
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
   * Invalidates medication order cache entry for encounter-based lookups.
   *
   * @param encounterId
   *     encounter ID
   */
  public void invalidateMedicationOrders(String encounterId) {
    getMedicationOrderClient().invalidate(encounterId);
  }

  /**
   * Invalidates medication administration cache entry for encounter-based lookups.
   *
   * @param encounterId
   *     encounter ID
   */
  public void invalidateMedicationAdministrations(String encounterId) {
    getMedicationAdministrationClient().invalidate(encounterId);
  }

  /**
   * Invalidates observation cache entry for encounter-based lookups.
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
   * Invalidates procedure cache entry for encounter-based lookups.
   *
   * @param procedureId
   *     procedure ID
   */
  public void invalidateProcedures(String procedureId) {
    getProcedureClient().invalidate(procedureId);
  }

  /**
   * Invalidates procedure request cache entry for encounter-based lookups.
   *
   * @param encounterId
   *     encounter ID
   */
  public void invalidateProcedureRequests(String encounterId) {
    getProcedureRequestClient().invalidate(encounterId);
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
