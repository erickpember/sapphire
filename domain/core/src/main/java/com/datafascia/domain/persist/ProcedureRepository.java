// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.FhirEntityStore;
import com.datafascia.domain.fhir.Ids;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Procedure data access.
 */
@Slf4j
public class ProcedureRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public ProcedureRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  private static EntityId toEntityId(Id<Encounter> encounterId, Id<Procedure> procedureId) {
    return EntityId.builder()
        .path(EncounterRepository.toEntityId(encounterId))
        .path(Procedure.class, procedureId)
        .build();
  }

  /**
   * Generates primary key.
   *
   * @param procedure
   *      read property from
   * @return primary key
   */
  public static Id<Procedure> generateId(Procedure procedure) {
    String identifierValue;
    if (!procedure.getId().isEmpty()) {
      identifierValue = procedure.getId().getIdPart();
    } else {
      StringJoiner joiner = new StringJoiner("^")
          .add(procedure.getIdentifierFirstRep().getValue())
          .add(procedure.getCode().getCodingFirstRep().getCode());
      for (CodeableConceptDt bodySite : procedure.getBodySite()) {
        joiner.add(bodySite.getCodingFirstRep().getCode());
      }

      identifierValue = joiner.toString();
    }

    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param procedure
   *     to save
   */
  public void save(Procedure procedure) {
    Id<Procedure> procedureId = generateId(procedure);
    procedure.setId(new IdDt(Procedure.class.getSimpleName(), procedureId.toString()));

    Id<Encounter> encounterId = Ids.toPrimaryKey(procedure.getEncounter().getReference());
    entityStore.save(toEntityId(encounterId, procedureId), procedure);
  }

  /**
   * Finds procedures for an encounter.
   *
   * @param encounterId
   *     encounter ID
   * @return procedures
   */
  public List<Procedure> list(Id<Encounter> encounterId) {
    return entityStore
        .stream(EncounterRepository.toEntityId(encounterId), Procedure.class)
        .collect(Collectors.toList());
  }
}
