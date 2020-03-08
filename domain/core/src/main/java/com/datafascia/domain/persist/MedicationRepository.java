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

import ca.uhn.fhir.model.dstu2.resource.Medication;
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
 * Medication data access.
 */
@Slf4j
public class MedicationRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public MedicationRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  private static EntityId toEntityId(Id<Medication> medicationId) {
    return new EntityId(Medication.class, medicationId);
  }

  /**
   * Generates primary key from Medication instance.
   * Unlike other models, Medication uses Medication.code as the ID field.
   *
   * @param medication medication from which to read the identifier
   * @return primary key
   */
  public static Id<Medication> generateId(Medication medication) {
    String identifierValue = medication.getCode().getCodingFirstRep().getCode();
    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param medication
   *     instance to save
   */
  public void save(Medication medication) {
    Id<Medication> medicationId = generateId(medication);
    medication.setId(new IdDt(Medication.class.getSimpleName(), medicationId.toString()));

    entityStore.save(toEntityId(medicationId), medication);
  }

  /**
   * Reads medication.
   *
   * @param medicationId
   *     entity ID to read
   * @return optional entity, empty if not found
   */
  public Optional<Medication> read(Id<Medication> medicationId) {
    return entityStore.read(toEntityId(medicationId));
  }

  /**
   * Finds all medications.
   *
   * @return found medications
   */
  public List<Medication> list() {
    Stream<Medication> stream = entityStore.stream(Medication.class);
    return stream.collect(Collectors.toList());
  }
}
