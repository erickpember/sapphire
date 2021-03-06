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

import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.FhirEntityStore;
import com.datafascia.domain.fhir.Ids;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import java.util.List;
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
    String identifierValue = flag.getCode().getCodingFirstRep().getCode();
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
    flag.setId(new IdDt(Flag.class.getSimpleName(), flagId.toString()));

    Id<UnitedStatesPatient> patientId = Ids.toPrimaryKey(flag.getSubject().getReference());
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
