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
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.EncodingEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

/**
 * Front controller that forwards FHIR resource requests to providers.
 */
@Slf4j
public class FhirServlet extends RestfulServer {

  @Inject
  private FhirContext fhirContext;

  @Inject
  private Injector injector;

  private List<IResourceProvider> findResourceProviders() {
    List<IResourceProvider> providers = new ArrayList<>();

    String basePackage = getClass().getPackage().getName();
    Reflections reflections = new Reflections(basePackage);
    Set<Class<? extends IResourceProvider>> providerClasses =
        reflections.getSubTypesOf(IResourceProvider.class);
    for (Class<? extends IResourceProvider> providerClass : providerClasses) {
      log.info("Creating resource provider {}", providerClass);
      providers.add(injector.getInstance(providerClass));
    }

    return providers;
  }

  /**
   * The initialize method is automatically called when the servlet is starting up, so it can be
   * used to configure the servlet to define resource providers, or set up configuration,
   * interceptors, etc.
   *
   * @throws javax.servlet.ServletException if error occurred
   */
  @Override
  protected void initialize() throws ServletException {
    setFhirContext(fhirContext);

    // Configure resource providers.
    setResourceProviders(findResourceProviders());

    /*
     * Tells HAPI to use content types which are not technically FHIR compliant when a browser is
     * detected as the requesting client. This prevents browsers from trying to download resource
     * responses instead of displaying them inline which can be handy for troubleshooting.
     */
    ResponseHighlighterInterceptor interceptor = new ResponseHighlighterInterceptor();
    registerInterceptor(interceptor);

    setDefaultResponseEncoding(EncodingEnum.JSON);
    setDefaultPrettyPrint(true);
  }
}
