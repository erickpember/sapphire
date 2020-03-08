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

import ca.uhn.fhir.model.dstu2.resource.Practitioner;
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
 * Practitioner data access.
 */
@Slf4j
public class PractitionerRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public PractitionerRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  /**
   * Converts practitioner ID to entity ID.
   *
   * @param practitionerId
   *     practitioner ID
   * @return entity ID
   */
  static EntityId toEntityId(Id<Practitioner> practitionerId) {
    return new EntityId(Practitioner.class, practitionerId);
  }

  /**
   * Generates primary key from practitioner identifier.
   *
   * @param practitioner
   *     practitioner to read property from
   * @return primary key
   */
  public static Id<Practitioner> generateId(Practitioner practitioner) {
    String identifierValue = practitioner.getIdentifierFirstRep().getValue();
    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param practitioner
   *     to save
   */
  public void save(Practitioner practitioner) {
    Id<Practitioner> practitionerId = generateId(practitioner);
    practitioner.setId(new IdDt(Practitioner.class.getSimpleName(), practitionerId.toString()));

    entityStore.save(toEntityId(practitionerId), practitioner);
  }

  /**
   * Reads entity.
   *
   * @param practitionerId
   *     practitioner ID
   * @return optional entity, empty if not found
   */
  public Optional<Practitioner> read(Id<Practitioner> practitionerId) {
    return entityStore.read(toEntityId(practitionerId));
  }

  /**
   * Finds all practitioners.
   *
   * @return found practitioners
   */
  public List<Practitioner> list() {
    Stream<Practitioner> stream = entityStore.stream(Practitioner.class);
    return stream.collect(Collectors.toList());
  }
}
