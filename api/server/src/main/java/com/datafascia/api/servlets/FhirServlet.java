// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.servlets;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.EncodingEnum;
import ca.uhn.fhir.rest.server.RestfulServer;
import com.datafascia.api.resources.fhir.EncounterResourceProvider;
import com.datafascia.api.resources.fhir.LocationResourceProvider;
import com.datafascia.api.resources.fhir.MedicationAdministrationResourceProvider;
import com.datafascia.api.resources.fhir.MedicationPrescriptionResourceProvider;
import com.datafascia.api.resources.fhir.MedicationResourceProvider;
import com.datafascia.api.resources.fhir.ObservationResourceProvider;
import com.datafascia.api.resources.fhir.PatientResourceProvider;
import java.util.Arrays;
import javax.servlet.ServletException;

/**
 * Front controller that forwards FHIR resource requests to providers.
 */
public class FhirServlet extends RestfulServer {
  /**
   * Constructor
   */
  public FhirServlet() {
    super(FhirContext.forDstu2());
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
    // Configure resource providers.
    setResourceProviders(Arrays.asList(
        new EncounterResourceProvider(),
        new LocationResourceProvider(),
        new MedicationResourceProvider(),
        new MedicationAdministrationResourceProvider(),
        new MedicationPrescriptionResourceProvider(),
        new ObservationResourceProvider(),
        new PatientResourceProvider()));

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
