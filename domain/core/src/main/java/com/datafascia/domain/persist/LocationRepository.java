// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.FhirEntityStore;
import com.google.common.io.BaseEncoding;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Location data access.
 */
@Slf4j
public class LocationRepository extends FhirEntityStoreRepository {

  private static final BaseEncoding ENCODING = BaseEncoding.base64Url().omitPadding();

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public LocationRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  /**
   * Converts location ID to entity ID.
   *
   * @param locationId
   *     location ID
   * @return entity ID
   */
  static EntityId toEntityId(Id<Location> locationId) {
    return new EntityId(Location.class, locationId);
  }

  /**
   * Generates primary key from location identifier.
   *
   * @param location
   *     location to read property from
   * @return primary key
   */
  public static Id<Location> generateId(Location location) {
    String identifierValue = location.getIdentifierFirstRep().getValue();
    return Id.of(ENCODING.encode(identifierValue.getBytes(StandardCharsets.UTF_8)));
  }

  /**
   * Saves entity.
   *
   * @param location
   *     to save
   */
  public void save(Location location) {
    Id<Location> locationId = generateId(location);
    location.setId(new IdDt(Location.class.getSimpleName(), locationId.toString()));

    entityStore.save(toEntityId(locationId), location);
  }

  /**
   * Reads entity.
   *
   * @param locationId
   *     location ID
   * @return optional entity, empty if not found
   */
  public Optional<Location> read(Id<Location> locationId) {
    return entityStore.read(toEntityId(locationId));
  }
}
