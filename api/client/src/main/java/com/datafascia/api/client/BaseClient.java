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
