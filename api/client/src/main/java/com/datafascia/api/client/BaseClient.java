// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.rest.client.IGenericClient;
import java.util.ArrayList;
import java.util.List;

/**
 * Common functionality for single-resource API clients.
 * @param <T> Resource type the client handles.
 */
public abstract class BaseClient<T extends IResource> {
  protected IGenericClient client;

  /**
   * Builds a Client
   *
   * @param client The FHIR client to use.
   */
  public BaseClient(IGenericClient client) {
    this.client = client;
  }

  /**
   * Extracts a bundle from a web API query
   *
   * @param bundle        A result from the web API query.
   * @param resourceClass Type of resource we're extracting.
   * @return Resource list extracted from bundle.
   */
  public List<T> extractBundle(Bundle bundle, Class resourceClass) {
    List<T> resources = new ArrayList<>();
    for (Bundle.Entry entry : bundle.getEntry()) {
      IResource resource = entry.getResource();
      if (resource.getClass().isAssignableFrom(resourceClass)) {
        resources.add((T) resource);
      }
    }
    return resources;
  }
}
