// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact


package com.datafascia.api.services;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import java.util.List;

import static com.datafascia.api.services.ApiIT.client;

/**
 * Helper methods for API access.
 */
public class ApiUtil {
  /**
   * Extracts a bundle from a web API query
   *
   * @param bundle A result from the web API query.
   * @param resourceClass Type of resource we're extracting.
   * @return A list of resources that were in the bundle.
   */
  public static List<IResource> extractBundle(Bundle bundle, Class resourceClass) {
    List<IResource> resources = bundle.getResources(resourceClass);

    while (bundle.getLinkNext().isEmpty() == false) {
      bundle = client.loadPage().next(bundle).execute();
      resources.addAll(bundle.getResources(resourceClass));
    }
    return resources;
  }
}
