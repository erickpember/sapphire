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
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
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
 * Medication administration data access.
 */
@Slf4j
public class MedicationAdministrationRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public MedicationAdministrationRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  private static EntityId toEntityId(
      Id<Encounter> encounterId, Id<MedicationAdministration> administrationId) {

    return EntityId.builder()
        .path(EncounterRepository.toEntityId(encounterId))
        .path(MedicationAdministration.class, administrationId)
        .build();
  }

  /**
   * Generates primary key from institution medication administration identifier.
   *
   * @param administration medication administration from which to read the identifier
   * @return primary key
   */
  public static Id<MedicationAdministration> generateId(MedicationAdministration administration) {
    String identifierValue = administration.getIdentifierFirstRep().getValue();
    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param administration to save
   */
  public void save(MedicationAdministration administration) {
    Id<MedicationAdministration> medicationadministrationId = generateId(administration);
    administration.setId(new IdDt(MedicationAdministration.class.getSimpleName(),
        medicationadministrationId.toString()));

    Id<Encounter> encounterId = Ids.toPrimaryKey(administration.getEncounter().getReference());

    entityStore.save(toEntityId(encounterId, medicationadministrationId), administration);
  }

  /**
   * Reads entity.
   *
   * @param encounterId      parent entity ID
   * @param medicationadministrationId to read
   * @return Optional entity, empty if not found.
   */
  public Optional<MedicationAdministration> read(Id<Encounter> encounterId,
      Id<MedicationAdministration> medicationadministrationId) {
    return entityStore.read(toEntityId(encounterId, medicationadministrationId));
  }

  /**
   * Finds medication administrations for an encounter.
   *
   * @param encounterId
   *     encounter ID
   * @return medication administrations
   */
  public List<MedicationAdministration> list(Id<Encounter> encounterId) {
    return entityStore
        .stream(EncounterRepository.toEntityId(encounterId), MedicationAdministration.class)
        .collect(Collectors.toList());
  }
}
