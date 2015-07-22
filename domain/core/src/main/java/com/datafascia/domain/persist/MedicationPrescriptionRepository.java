// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.MedicationPrescription;
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
 * Medication prescription data access.
 */
@Slf4j
public class MedicationPrescriptionRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public MedicationPrescriptionRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  private static EntityId toEntityId(
      Id<Encounter> encounterId, Id<MedicationPrescription> prescriptionId) {

    return EntityId.builder()
        .path(EncounterRepository.toEntityId(encounterId))
        .path(MedicationPrescription.class, prescriptionId)
        .build();
  }

  /**
   * Generates primary key from institution medication prescription identifier.
   *
   * @param prescription medication prescription from which to read the identifier
   * @return primary key
   */
  public static Id<MedicationPrescription> generateId(MedicationPrescription prescription) {
    String identifierValue = prescription.getIdentifierFirstRep().getValue();
    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param encounter parent entity
   * @param prescription to save
   */
  public void save(Encounter encounter, MedicationPrescription prescription) {
    Id<MedicationPrescription> prescriptionId = generateId(prescription);
    prescription.setId(new IdDt(MedicationPrescription.class.getSimpleName(), prescriptionId.
        toString()));

    Id<Encounter> encounterId = Ids.toPrimaryKey(encounter.getId());
    entityStore.save(toEntityId(encounterId, prescriptionId), prescription);
  }

  /**
   * Saves entity.
   *
   * @param encounterId  parent entity ID
   * @param prescription to save
   */
  public void save(Id<Encounter> encounterId, MedicationPrescription prescription) {
    Id<MedicationPrescription> medicationprescriptionId = generateId(prescription);
    prescription.setId(new IdDt(MedicationPrescription.class.getSimpleName(),
        medicationprescriptionId.toString()));

    entityStore.save(toEntityId(encounterId, medicationprescriptionId), prescription);
  }

  /**
   * Saves entity.
   *
   * @param prescription to save
   */
  public void save(MedicationPrescription prescription) {
    Id<MedicationPrescription> medicationprescriptionId = generateId(prescription);
    prescription.setId(new IdDt(MedicationPrescription.class.getSimpleName(),
        medicationprescriptionId.toString()));

    Id<Encounter> encounterId = Ids.toPrimaryKey(prescription.getEncounter().getReference());

    entityStore.save(toEntityId(encounterId, medicationprescriptionId), prescription);
  }

  /**
   * Finds medication prescriptions for an encounter.
   *
   * @param encounterId encounter ID
   * @return medication prescriptions
   */
  public List<MedicationPrescription> list(Id<Encounter> encounterId) {
    return entityStore
        .stream(EncounterRepository.toEntityId(encounterId), MedicationPrescription.class)
        .collect(Collectors.toList());
  }
}
