// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.FhirEntityStore;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Encounter data access.
 */
@Slf4j
public class EncounterRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore entity store
   */
  @Inject
  public EncounterRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  /**
   * Converts encounter ID to entity ID.
   *
   * @param encounterId encounter ID
   * @return entity ID
   */
  static EntityId toEntityId(Id<Encounter> encounterId) {
    return new EntityId(Encounter.class, encounterId);
  }

  /**
   * Generates primary key from institution encounter identifier.
   *
   * @param encounter encounter to read property from
   * @return primary key
   */
  public static Id<Encounter> generateId(Encounter encounter) {
    String identifierValue = encounter.getIdentifierFirstRep().getValue();
    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param encounter to save
   */
  public void save(Encounter encounter) {
    Id<Encounter> encounterId = generateId(encounter);
    encounter.setId(new IdDt(Encounter.class.getSimpleName(), encounterId.toString()));

    entityStore.save(toEntityId(encounterId), encounter);
  }

  /**
   * Reads encounter.
   *
   * @param encounterId encounter ID
   * @return optional entity, empty if not found
   */
  public Optional<Encounter> read(Id<Encounter> encounterId) {
    return entityStore.read(toEntityId(encounterId));
  }

  /**
   * Finds all encounters, or a set filtered by status.
   *
   * @param optStatus Status of encounter, as an optional search filter.
   * @return encounters
   */
  public List<Encounter> list(Optional<EncounterStateEnum> optStatus) {
    Stream<Encounter> stream = entityStore.stream(Encounter.class);
    if (optStatus.isPresent()) {
      stream = stream.filter(encounter -> encounter.getStatusElement().getValueAsEnum()
              .equals(optStatus.get()));
    }

    return stream.collect(Collectors.toList());
  }

  /**
   * Deletes encounter and all of its children.
   *
   * @param encounterId encounter ID
   */
  public void delete(Id<Encounter> encounterId) {
    entityStore.delete(toEntityId(encounterId));
  }
}
