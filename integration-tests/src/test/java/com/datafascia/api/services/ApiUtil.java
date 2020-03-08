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
package com.datafascia.api.services;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import java.util.List;

import static com.datafascia.api.services.ApiTestSupport.client;

/**
 * Helper methods for API access.
 */
public class ApiUtil {
  /**
   * Extracts a bundle from a web API query
   *
   * @param bundle        A result from the web API query.
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
