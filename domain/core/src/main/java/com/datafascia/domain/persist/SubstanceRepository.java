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

import ca.uhn.fhir.model.dstu2.resource.Substance;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.FhirEntityStore;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Substance data access.
 */
@Slf4j
public class SubstanceRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore entity store
   */
  @Inject
  public SubstanceRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  /**
   * Converts substance ID to entity ID.
   *
   * @param substanceId substance ID
   * @return entity ID
   */
  static EntityId toEntityId(Id<Substance> substanceId) {
    return new EntityId(Substance.class, substanceId);
  }

  /**
   * Generates primary key from substance ID.
   *
   * @param substance substance to read property from
   * @return primary key
   */
  public static Id<Substance> generateId(Substance substance) {
    String identifierValue = substance.getIdentifierFirstRep().getValue();
    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param substance to save
   */
  public void save(Substance substance) {
    Id<Substance> substanceId = generateId(substance);
    substance.setId(new IdDt(Substance.class.getSimpleName(), substanceId.toString()));

    entityStore.save(toEntityId(substanceId), substance);
  }

  /**
   * Reads substance.
   *
   * @param substanceId substance ID
   * @return optional entity, empty if not found
   */
  public Optional<Substance> read(Id<Substance> substanceId) {
    return entityStore.read(toEntityId(substanceId));
  }

  /**
   * Finds substances.
   *
   * @param optStartSubstanceId if present, start the scan from this substance ID
   * @param limit maximum number of items to return in list
   * @return found substances
   */
  public List<Substance> list(
      Optional<Id<Substance>> optStartSubstanceId, int limit) {

    Stream<Substance> stream;
    if (optStartSubstanceId.isPresent()) {
      stream = entityStore.stream(toEntityId(optStartSubstanceId.get()));
    } else {
      stream = entityStore.stream(Substance.class);
    }

    return stream.limit(limit)
        .collect(Collectors.toList());
  }
}
