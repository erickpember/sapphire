// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
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
 * Procedure request data access.
 */
@Slf4j
public class ProcedureRequestRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public ProcedureRequestRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  private static EntityId toEntityId(
      Id<Encounter> encounterId, Id<ProcedureRequest> requestId) {

    return EntityId.builder()
        .path(EncounterRepository.toEntityId(encounterId))
        .path(ProcedureRequest.class, requestId)
        .build();
  }

  /**
   * Generates primary key from procedure request identifier.
   *
   * @param request
   *     procedure request from which to read the identifier
   * @return primary key
   */
  public static Id<ProcedureRequest> generateId(ProcedureRequest request) {
    String identifierValue = request.getIdentifierFirstRep().getValue();
    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param request
   *     to save
   */
  public void save(ProcedureRequest request) {
    Id<ProcedureRequest> requestId = generateId(request);
    request.setId(new IdDt(ProcedureRequest.class.getSimpleName(), requestId.toString()));

    Id<Encounter> encounterId = Ids.toPrimaryKey(request.getEncounter().getReference());

    entityStore.save(toEntityId(encounterId, requestId), request);
  }

  /**
   * Reads entity.
   *
   * @param encounterId
   *     parent entity ID
   * @param requestId
   *     to read
   * @return Optional entity, empty if not found.
   */
  public Optional<ProcedureRequest> read(
      Id<Encounter> encounterId, Id<ProcedureRequest> requestId) {

    return entityStore.read(toEntityId(encounterId, requestId));
  }

  /**
   * Finds procedure requests for an encounter.
   *
   * @param encounterId
   *     encounter ID
   * @return procedure requests
   */
  public List<ProcedureRequest> list(Id<Encounter> encounterId) {
    return entityStore
        .stream(EncounterRepository.toEntityId(encounterId), ProcedureRequest.class)
        .collect(Collectors.toList());
  }
}
