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
package com.datafascia.emerge.ucsf.persist;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.ReflectEntityStore;
import com.datafascia.common.persist.entity.ReflectEntityStoreRepository;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.emerge.ucsf.HarmEvidence;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link HarmEvidence} data access. Assumes an encounter has at most one harm evidence record.
 */
@Slf4j
public class HarmEvidenceRepository extends ReflectEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public HarmEvidenceRepository(ReflectEntityStore entityStore) {
    super(entityStore);
  }

  private static EntityId toEntityId(Id<Encounter> encounterId) {
    Id<HarmEvidence> recordId = Id.of(encounterId.toString());
    return EntityId.builder()
        .path(EncounterRepository.toEntityId(encounterId))
        .path(HarmEvidence.class, recordId)
        .build();
  }

  /**
   * Saves entity.
   *
   * @param record
   *     to save
   */
  public void save(HarmEvidence record) {
    Id<Encounter> encounterId = Id.of(record.getEncounterID());
    entityStore.save(toEntityId(encounterId), record);
  }

  /**
   * Reads entity.
   *
   * @param encounterId
   *     encounter ID
   * @return optional entity, empty if not found
   */
  public Optional<HarmEvidence> read(Id<Encounter> encounterId) {
    return entityStore.read(toEntityId(encounterId));
  }

  /**
   * Deletes entity.
   *
   * @param encounterId
   *     encounter ID
   */
  public void delete(Id<Encounter> encounterId) {
    entityStore.delete(toEntityId(encounterId));
  }
}
