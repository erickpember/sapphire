// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.datafascia.common.fhir.DependencyInjectingResourceProvider;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.LocationRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Location resource endpoint
 */
@NoArgsConstructor @Slf4j
public class LocationResourceProvider extends DependencyInjectingResourceProvider {

  @Inject
  private LocationRepository locationRepository;

  @Override
  protected void onInjected() {
    log.info("locationRepository {}", locationRepository);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   *
   * @return Class of resource.
   */
  @Override
  public Class<Location> getResourceType() {
    return Location.class;
  }

  /**
   * Store a new location.
   *
   * @param location The new location to store.
   * @return Outcome of create method. Resource ID of Location.
   */
  @Create
  public MethodOutcome create(@ResourceParam Location location) {
    // Check if location already exists.
    Id<Location> locationId = LocationRepository.generateId(location);
    Optional<Location> optionalLocation = locationRepository.read(locationId);
    if (optionalLocation.isPresent()) {
      throw new InvalidRequestException(String.format("Location ID [%s] already exists",
          locationId));
    }

    locationRepository.save(location);
    return new MethodOutcome(location.getId());
  }

  /**
   * Completely replaces the content of the location resource with the content given in the request.
   *
   * @param resourceId Id of resource to update.
   * @param location   New location value.
   * @return Outcome of create method. Resource ID of Location.
   */
  @Update
  public MethodOutcome update(@IdParam IdDt resourceId, @ResourceParam Location location) {
    if (resourceId == null) {
      throw new UnprocessableEntityException("No identifier supplied");
    }

    locationRepository.save(location);
    return new MethodOutcome(location.getId());
  }

  /**
   * Retrieves a location using the ID.
   *
   * @param resourceId ID of Location resource.
   * @return Returns a resource matching this identifier, or null if none exists.
   */
  @Read()
  public Location getResourceById(@IdParam IdDt resourceId) {
    Location result = null;
    try {
      result = locationRepository.read(Id.of(resourceId.getIdPart())).get();
    } catch (NoSuchElementException e) {
      throw new ResourceNotFoundException("Failed to find resource for ID " + resourceId);
    }
    return result;
  }

  /**
   * Retrieves a location using the external Location Identifier.
   *
   * @param locationIdentifier Identifier of Location resource.
   * @return Returns a resource matching this identifier, or null if none exists.
   */
  @Search()
  public Location getResourceByIdentifier(
      @RequiredParam(name = Location.SP_IDENTIFIER) StringParam locationIdentifier) {
    return locationRepository.read(Id.of(locationIdentifier.getValue())).get();
  }
}
