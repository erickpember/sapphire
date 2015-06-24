// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.persist.entity.FhirEntityStore;
import lombok.extern.slf4j.Slf4j;

/**
 * Implements common data access methods.
 */
@Slf4j
public abstract class FhirEntityStoreRepository {

  protected FhirEntityStore entityStore;

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  public FhirEntityStoreRepository(FhirEntityStore entityStore) {
    this.entityStore = entityStore;
  }
}
