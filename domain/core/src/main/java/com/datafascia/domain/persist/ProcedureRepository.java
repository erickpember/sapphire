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
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.FhirEntityStore;
import com.datafascia.domain.fhir.Ids;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Procedure data access.
 */
@Slf4j
public class ProcedureRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public ProcedureRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  private static EntityId toEntityId(Id<Encounter> encounterId, Id<Procedure> procedureId) {
    return EntityId.builder()
        .path(EncounterRepository.toEntityId(encounterId))
        .path(Procedure.class, procedureId)
        .build();
  }

  /**
   * Generates primary key.
   *
   * @param procedure
   *      read property from
   * @return primary key
   */
  public static Id<Procedure> generateId(Procedure procedure) {
    String identifierValue = (!procedure.getId().isEmpty())
        ? procedure.getId().getIdPart()
        : procedure.getIdentifierFirstRep().getValue();
    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param procedure
   *     to save
   */
  public void save(Procedure procedure) {
    Id<Procedure> procedureId = generateId(procedure);
    procedure.setId(new IdDt(Procedure.class.getSimpleName(), procedureId.toString()));

    Id<Encounter> encounterId = Ids.toPrimaryKey(procedure.getEncounter().getReference());
    entityStore.save(toEntityId(encounterId, procedureId), procedure);
  }

  /**
   * Finds procedures for an encounter.
   *
   * @param encounterId
   *     encounter ID
   * @return procedures
   */
  public List<Procedure> list(Id<Encounter> encounterId) {
    return entityStore
        .stream(EncounterRepository.toEntityId(encounterId), Procedure.class)
        .collect(Collectors.toList());
  }
}
