// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.fhir;

import ca.uhn.fhir.rest.server.IResourceProvider;
import com.datafascia.common.inject.Injectors;

/**
 * Resource provider that will set its fields by dependency injection. To use,
 * extend this class and define fields annotated with {@code @Inject} which will
 * be set with the dependencies.
 */
public abstract class DependencyInjectingResourceProvider implements IResourceProvider {

  /**
   * Constructor
   */
  protected DependencyInjectingResourceProvider() {
    Injectors.getInjector().injectMembers(this);
    onInjected();
  }

  /**
   * Subclasses may override this method to execute logic after dependencies are
   * injected. The default implementation does nothing.
   */
  protected void onInjected() {
  }
}
