// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import com.google.common.base.Strings;

/**
 * A builder for resource clients.
 */
public class ClientBuilder {
  private static final FhirContext ctx = FhirContext.forDstu2();
  private final IGenericClient client;

  /**
   * Constructs a ClientBuilder.
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

  public MedicationAdministrationClient getMedicationAdministrationClient() {
    return new MedicationAdministrationClient(client);
  }

  public MedicationPrescriptionClient getMedicationPrescriptionClient() {
    return new MedicationPrescriptionClient(client);
  }

  public ProcedureRequestClient getProcedureRequestClient() {
    return new ProcedureRequestClient(client);
  }

  public MedicationClient getMedicationClient() {
    return new MedicationClient(client);
  }
}
