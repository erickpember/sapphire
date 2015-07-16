// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.servlets;

import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import com.datafascia.api.resources.fhir.EncounterResourceProvider;
import com.datafascia.api.resources.fhir.LocationResourceProvider;
import com.datafascia.api.resources.fhir.ObservationResourceProvider;
import com.datafascia.api.resources.fhir.PatientResourceProvider;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;

/**
 * Front controller that forwards FHIR resource requests to providers.
 */
public class FhirServlet extends RestfulServer {
  /**
   * The initialize method is automatically called when the servlet is starting up, so it can be
   * used to configure the servlet to define resource providers, or set up configuration,
   * interceptors, etc.
   * @throws javax.servlet.ServletException
   */
  @Override
  protected void initialize() throws ServletException {
    /*
     * The servlet defines any number of resource providers, and
     * configures itself to use them by calling
     * setResourceProviders()
     */
    List<IResourceProvider> resourceProviders = new ArrayList<>();
    resourceProviders.add(new PatientResourceProvider());
    resourceProviders.add(new EncounterResourceProvider());
    resourceProviders.add(new ObservationResourceProvider());
    resourceProviders.add(new LocationResourceProvider());
    setResourceProviders(resourceProviders);
  }
}
