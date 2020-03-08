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

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.FhirEntityIndex;
import com.datafascia.common.persist.entity.FhirEntityStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Encounter data access.
 */
@Slf4j
public class EncounterRepository extends FhirEntityStoreRepository {

  private FhirEntityIndex<Encounter> statusIndex;

  /**
   * Constructor
   *
   * @param entityStore entity store
   */
  @Inject
  public EncounterRepository(FhirEntityStore entityStore) {
    super(entityStore);

    statusIndex = entityStore.getIndex("EncounterStatus", Encounter::getStatus);
  }

  private void populateStatusIndex() {
    if (statusIndex.isEmpty()) {
      entityStore.stream(Encounter.class)
          .forEach(encounter -> {
            EntityId entityId = toEntityId(encounter);
            statusIndex.save(entityId, null, encounter);
          });
    }
  }

  /**
   * Converts encounter ID to entity ID.
   *
   * @param encounterId encounter ID
   * @return entity ID
   */
  public static EntityId toEntityId(Id<Encounter> encounterId) {
    return new EntityId(Encounter.class, encounterId);
  }

  private static EntityId toEntityId(Encounter encounter) {
    Id<Encounter> encounterId = Id.of(encounter.getId().getIdPart());
    return toEntityId(encounterId);
  }

  /**
   * Generates primary key from institution encounter identifier.
   *
   * @param encounter encounter to read property from
   * @return primary key
   */
  public static Id<Encounter> generateId(Encounter encounter) {
    String identifierValue = encounter.getIdentifierFirstRep().getValue();
    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param encounter to save
   */
  public void save(Encounter encounter) {
    Id<Encounter> encounterId = generateId(encounter);
    encounter.setId(new IdDt(Encounter.class.getSimpleName(), encounterId.toString()));

    EntityId entityId = toEntityId(encounterId);
    Optional<Encounter> oldEncounter = entityStore.read(entityId);
    entityStore.save(entityId, encounter);

    populateStatusIndex();
    statusIndex.save(entityId, oldEncounter.orElse(null), encounter);
  }

  /**
   * Reads encounter.
   *
   * @param encounterId encounter ID
   * @return optional entity, empty if not found
   */
  public Optional<Encounter> read(Id<Encounter> encounterId) {
    return entityStore.read(toEntityId(encounterId));
  }

  /**
   * Finds all encounters, or a set filtered by status.
   *
   * @param optStatus Status of encounter, as an optional search filter.
   * @return encounters
   */
  public List<Encounter> list(Optional<EncounterStateEnum> optStatus) {
    if (optStatus.isPresent()) {
      List<Encounter> encounters = new ArrayList<>();
      populateStatusIndex();
      statusIndex.search(optStatus.get().getCode())
          .forEach(entityRowId ->
              entityStore.read(entityRowId, Encounter.class)
                  .ifPresent(encounter -> encounters.add(encounter))
        );
      return encounters;
    }

    return entityStore.stream(Encounter.class)
        .collect(Collectors.toList());
  }

  /**
   * Deletes encounter and all of its children.
   *
   * @param encounter
   *     to delete
   */
  public void delete(Encounter encounter) {
    EntityId entityId = toEntityId(encounter);
    entityStore.delete(entityId);

    populateStatusIndex();
    statusIndex.delete(entityId, encounter);
  }
}
