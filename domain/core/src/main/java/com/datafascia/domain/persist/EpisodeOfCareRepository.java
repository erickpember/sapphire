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

import ca.uhn.fhir.model.dstu2.resource.EpisodeOfCare;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.FhirEntityStore;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * EpisodeOfCare data access.
 */
@Slf4j
public class EpisodeOfCareRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore entity store
   */
  @Inject
  public EpisodeOfCareRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  /**
   * Converts encounter ID to entity ID.
   *
   * @param encounterId encounter ID
   * @return entity ID
   */
  public static EntityId toEntityId(Id<EpisodeOfCare> encounterId) {
    return new EntityId(EpisodeOfCare.class, encounterId);
  }

  /**
   * Generates primary key from institution encounter identifier.
   *
   * @param encounter encounter to read property from
   * @return primary key
   */
  public static Id<EpisodeOfCare> generateId(EpisodeOfCare encounter) {
    String identifierValue = encounter.getIdentifierFirstRep().getValue();
    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param episodeOfCare to save
   */
  public void save(EpisodeOfCare episodeOfCare) {
    Id<EpisodeOfCare> episodeOfCareId = generateId(episodeOfCare);
    episodeOfCare.setId(new IdDt(EpisodeOfCare.class.getSimpleName(), episodeOfCareId.toString()));

    entityStore.save(toEntityId(episodeOfCareId), episodeOfCare);
  }

  /**
   * Reads EpisodeOfCare.
   *
   * @param episodeOfCareId EpisodeOfCare ID
   * @return optional entity, empty if not found
   */
  public Optional<EpisodeOfCare> read(Id<EpisodeOfCare> episodeOfCareId) {
    return entityStore.read(toEntityId(episodeOfCareId));
  }

  /**
   * Deletes EpisodeOfCare and all of its children.
   *
   * @param episodeOfCareId EpisodeOfCare ID
   */
  public void delete(Id<EpisodeOfCare> episodeOfCareId) {
    entityStore.delete(toEntityId(episodeOfCareId));
  }
}

