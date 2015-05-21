// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.ReflectEntityStore;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.MedicationAdministration;
import com.datafascia.domain.model.Patient;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Medication administration data access.
 */
@Slf4j
public class MedicationAdministrationRepository extends EntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public MedicationAdministrationRepository(ReflectEntityStore entityStore) {
    super(entityStore);
  }

  private static EntityId toEntityId(
      Id<Patient> patientId,
      Id<Encounter> encounterId,
      Id<MedicationAdministration> administrationId) {

    return EntityId.builder()
        .path(EncounterRepository.toEntityId(patientId, encounterId))
        .path(MedicationAdministration.class, administrationId)
        .build();
  }

  private static Id<MedicationAdministration> generateId(MedicationAdministration administration) {
    return (administration.getId() != null)
        ? administration.getId()
        : Id.of(UUID.randomUUID().toString());
  }

  /**
   * Saves entity.
   *
   * @param patient
   *     parent entity
   * @param encounter
   *     parent entity
   * @param administration
   *     to save
   */
  public void save(Patient patient, Encounter encounter, MedicationAdministration administration) {
    administration.setId(generateId(administration));

    entityStore.save(
        toEntityId(patient.getId(), encounter.getId(), administration.getId()),
        administration);
  }

  /**
   * Finds medication administrations for an encounter.
   *
   * @param patientId
   *     parent entity ID
   * @param encounterId
   *     encounter ID
   * @return medication administrations
   */
  public List<MedicationAdministration> list(Id<Patient> patientId, Id<Encounter> encounterId) {
    return entityStore
        .stream(
            EncounterRepository.toEntityId(patientId, encounterId), MedicationAdministration.class)
        .collect(Collectors.toList());
  }
}
