// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.FhirEntityStore;
import com.datafascia.domain.fhir.UnitedStatesPatient;
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
public class PatientRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public PatientRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  /**
   * Converts patient ID to entity ID.
   *
   * @param patientId
   *     patient ID
   * @return entity ID
   */
  static EntityId toEntityId(Id<UnitedStatesPatient> patientId) {
    return new EntityId(UnitedStatesPatient.class, patientId);
  }

  /**
   * Generates primary key from institution patient ID.
   *
   * @param patient
   *     patient to read property from
   * @return primary key
   */
  public static Id<UnitedStatesPatient> generateId(UnitedStatesPatient patient) {
    String identifierValue = patient.getIdentifierFirstRep().getValue();
    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param patient
   *     to save
   */
  public void save(UnitedStatesPatient patient) {
    Id<UnitedStatesPatient> patientId = generateId(patient);
    patient.setId(new IdDt(Patient.class.getSimpleName(), patientId.toString()));

    entityStore.save(toEntityId(patientId), patient);
  }

  /**
   * Reads patient.
   *
   * @param patientId
   *     patient ID
   * @return optional entity, empty if not found
   */
  public Optional<UnitedStatesPatient> read(Id<UnitedStatesPatient> patientId) {
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
  public List<UnitedStatesPatient> list(
      Optional<Id<UnitedStatesPatient>> optStartPatientId, Optional<Boolean> optActive, int limit) {

    Stream<UnitedStatesPatient> stream;
    if (optStartPatientId.isPresent()) {
      stream = entityStore.stream(toEntityId(optStartPatientId.get()));
    } else {
      stream = entityStore.stream(UnitedStatesPatient.class);
    }

    if (optActive.isPresent()) {
      Boolean active = optActive.get();
      stream = stream.filter(patient -> patient.getActive().equals(active));
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
  public void delete(Id<UnitedStatesPatient> patientId) {
    read(patientId).ifPresent(patient -> {
        patient.setActive(false);
        save(patient);
      });
  }
}
