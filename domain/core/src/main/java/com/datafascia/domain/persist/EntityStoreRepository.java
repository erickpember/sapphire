// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.persist.entity.ReflectEntityStore;
import lombok.extern.slf4j.Slf4j;

/**
 * Implements common data access methods.
 */
@Slf4j
public abstract class EntityStoreRepository {

  protected ReflectEntityStore entityStore;

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  public EntityStoreRepository(ReflectEntityStore entityStore) {
    this.entityStore = entityStore;
  }
}
