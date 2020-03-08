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
