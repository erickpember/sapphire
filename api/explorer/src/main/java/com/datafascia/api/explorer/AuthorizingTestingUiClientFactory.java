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
