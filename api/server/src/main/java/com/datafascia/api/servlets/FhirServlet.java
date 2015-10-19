// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.servlets;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.EncodingEnum;
import ca.uhn.fhir.rest.server.RestfulServer;
import com.datafascia.api.resources.fhir.EncounterResourceProvider;
import com.datafascia.api.resources.fhir.FlagResourceProvider;
import com.datafascia.api.resources.fhir.LocationResourceProvider;
import com.datafascia.api.resources.fhir.MedicationAdministrationResourceProvider;
import com.datafascia.api.resources.fhir.MedicationOrderResourceProvider;
import com.datafascia.api.resources.fhir.MedicationResourceProvider;
import com.datafascia.api.resources.fhir.ObservationResourceProvider;
import com.datafascia.api.resources.fhir.PatientResourceProvider;
import com.datafascia.api.resources.fhir.PractitionerResourceProvider;
import com.datafascia.api.resources.fhir.ProcedureRequestResourceProvider;
import com.datafascia.api.resources.fhir.ProcedureResourceProvider;
import com.google.inject.Injector;
import java.util.Arrays;
import javax.servlet.ServletException;

/**
 * Front controller that forwards FHIR resource requests to providers.
 */
public class FhirServlet extends RestfulServer {

  private Injector injector;

  /**
   * Constructor
   *
   * @param injector
   *     Guice injector
   */
  public FhirServlet(Injector injector) {
    super(injector.getInstance(FhirContext.class));

    this.injector = injector;
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
        injector.getInstance(EncounterResourceProvider.class),
        injector.getInstance(FlagResourceProvider.class),
        injector.getInstance(LocationResourceProvider.class),
        injector.getInstance(MedicationAdministrationResourceProvider.class),
        injector.getInstance(MedicationOrderResourceProvider.class),
        injector.getInstance(MedicationResourceProvider.class),
        injector.getInstance(ObservationResourceProvider.class),
        injector.getInstance(ProcedureResourceProvider.class),
        injector.getInstance(ProcedureRequestResourceProvider.class),
        injector.getInstance(PatientResourceProvider.class),
        injector.getInstance(PractitionerResourceProvider.class)));

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
