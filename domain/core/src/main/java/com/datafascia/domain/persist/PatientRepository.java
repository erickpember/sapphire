// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.ReflectEntityStore;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.domain.model.Patient;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Patient data access.
 */
@Slf4j
public class PatientRepository extends EntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public PatientRepository(ReflectEntityStore entityStore) {
    super(entityStore);
  }

  /**
   * Converts patient ID to entity ID.
   *
   * @param patientId
   *     patient ID
   * @return entity ID
   */
  static EntityId toEntityId(Id<Patient> patientId) {
    return new EntityId(Patient.class, patientId);
  }

  /**
   * Generates primary key from institution patient ID.
   *
   * @param patient
   *     patient to read property from
   * @return primary key
   */
  public static Id<Patient> generateId(Patient patient) {
    return Id.of(URNFactory.urn(URNFactory.NS_PATIENT_ID, patient.getInstitutionPatientId()));
  }

  /**
   * Saves entity.
   *
   * @param patient
   *     to save
   */
  public void save(Patient patient) {
    patient.setId(generateId(patient));

    entityStore.save(toEntityId(patient.getId()), patient);
  }

  /**
   * Reads patient.
   *
   * @param patientId
   *     patient ID
   * @return optional entity, empty if not found
   */
  public Optional<Patient> read(Id<Patient> patientId) {
    return entityStore.read(toEntityId(patientId));
  }

  /**
   * Finds patients.
   *
   * @param optStartPatientId
   *     if present, start the scan from this patient ID
   * @param optActive
   *     if present, the active state to match
   * @param limit
   *     maximum number of items to return in list
   * @return found patients
   */
  public List<Patient> list(
      Optional<Id<Patient>> optStartPatientId, Optional<Boolean> optActive, int limit) {

    Stream<Patient> stream;
    if (optStartPatientId.isPresent()) {
      stream = entityStore.stream(toEntityId(optStartPatientId.get()));
    } else {
      stream = entityStore.stream(Patient.class);
    }

    if (optActive.isPresent()) {
      boolean active = optActive.get();
      stream = stream.filter(patient -> patient.isActive() == active);
    }

    return stream.limit(limit)
        .collect(Collectors.toList());
  }

  /**
   * Deletes patient.
   *
   * @param patientId
   *     patient ID
   */
  public void delete(Id<Patient> patientId) {
    read(patientId).ifPresent(patient -> {
        patient.setActive(false);
        save(patient);
      });
  }
}
