// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.EncodingEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
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
    setUseBrowserFriendlyContentTypes(true);

    setDefaultResponseEncoding(EncodingEnum.JSON);
    setDefaultPrettyPrint(true);
  }
}
