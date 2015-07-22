// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
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
   * @param encounter parent entity
   * @param administration to save
   */
  public void save(Encounter encounter, MedicationAdministration administration) {
    Id<MedicationAdministration> administrationId = generateId(administration);
    administration.setId(new IdDt(MedicationAdministration.class.getSimpleName(), administrationId.
        toString()));

    Id<Encounter> encounterId = Ids.toPrimaryKey(encounter.getId());
    entityStore.save(toEntityId(encounterId, administrationId), administration);
  }

  /**
   * Saves entity.
   *
   * @param encounterId    parent entity ID
   * @param administration to save
   */
  public void save(Id<Encounter> encounterId, MedicationAdministration administration) {
    Id<MedicationAdministration> medicationadministrationId = generateId(administration);
    administration.setId(new IdDt(MedicationAdministration.class.getSimpleName(),
        medicationadministrationId.toString()));

    entityStore.save(toEntityId(encounterId, medicationadministrationId), administration);
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
