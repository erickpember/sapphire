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
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.FhirEntityStore;
import com.datafascia.domain.fhir.Ids;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Medication order data access.
 */
@Slf4j
public class MedicationOrderRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public MedicationOrderRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  private static EntityId toEntityId(
      Id<Encounter> encounterId, Id<MedicationOrder> prescriptionId) {

    return EntityId.builder()
        .path(EncounterRepository.toEntityId(encounterId))
        .path(MedicationOrder.class, prescriptionId)
        .build();
  }

  /**
   * Generates primary key from institution-assigned medication order identifier.
   *
   * @param medicationOrder medication prescription from which to read the identifier
   * @return primary key
   */
  public static Id<MedicationOrder> generateId(MedicationOrder medicationOrder) {
    String identifierValue = medicationOrder.getIdentifierFirstRep().getValue();
    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param medicationOrder to save
   */
  public void save(MedicationOrder medicationOrder) {
    Id<MedicationOrder> orderId = generateId(medicationOrder);
    medicationOrder.setId(new IdDt(
        MedicationOrder.class.getSimpleName(), orderId.toString()));

    Id<Encounter> encounterId = Ids.toPrimaryKey(medicationOrder.getEncounter().getReference());

    entityStore.save(toEntityId(encounterId, orderId), medicationOrder);
  }

  /**
   * Reads entity.
   *
   * @param encounterId      parent entity ID
   * @param medicationOrderId to read
   * @return Optional entity, empty if not found.
   */
  public Optional<MedicationOrder> read(
      Id<Encounter> encounterId, Id<MedicationOrder> medicationOrderId) {

    return entityStore.read(toEntityId(encounterId, medicationOrderId));
  }

  /**
   * Finds medication orders for an encounter.
   *
   * @param encounterId encounter ID
   * @return medication orders
   */
  public List<MedicationOrder> list(Id<Encounter> encounterId) {
    return entityStore
        .stream(EncounterRepository.toEntityId(encounterId), MedicationOrder.class)
        .collect(Collectors.toList());
  }
}
