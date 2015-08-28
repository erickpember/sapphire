// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.FhirEntityStore;
import com.datafascia.domain.fhir.Ids;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link Flag} data access.
 */
@Slf4j
public class FlagRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public FlagRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  private static EntityId toEntityId(Id<UnitedStatesPatient> patientId, Id<Flag> flagId) {
    return EntityId.builder()
        .path(PatientRepository.toEntityId(patientId))
        .path(Flag.class, flagId)
        .build();
  }

  private static Id<Flag> generateId(Flag flag) {
    String identifierValue = (flag.getId().isEmpty())
        ? UUID.randomUUID().toString() : flag.getId().getIdPart();
    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param flag
   *     to save
   */
  public void save(Flag flag) {
    Id<Flag> flagId = generateId(flag);
    flag.setId(new IdDt(Observation.class.getSimpleName(), flagId.toString()));

    Id<UnitedStatesPatient> patientId = Ids.toPrimaryKey(flag.getPatient().getReference());
    entityStore.save(toEntityId(patientId, flagId), flag);
  }

  /**
   * Finds flags for a patient, or all flags if patient is left blank.
   *
   * @param patientId
   *     patient ID
   * @return flags
   */
  public List<Flag> list(Id<UnitedStatesPatient> patientId) {
    return entityStore
        .stream(PatientRepository.toEntityId(patientId), Flag.class)
        .collect(Collectors.toList());
  }
}
