// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.explorer;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.util.ITestingUiClientFactory;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Supplies HTTP Basic authentication header in request.
 */
@Slf4j
public class AuthorizingTestingUiClientFactory implements ITestingUiClientFactory {

  @Override
  public IGenericClient newClient(
      FhirContext fhirContext, HttpServletRequest httpServletRequest, String serverBaseUrl) {

    log.debug("newClient, serverBaseUrl {}", serverBaseUrl);
    IGenericClient client = fhirContext.newRestfulGenericClient(serverBaseUrl);

    // Register an interceptor which adds credentials.
    client.registerInterceptor(new BasicAuthInterceptor("testuser", "supersecret"));

    return client;
  }
}
