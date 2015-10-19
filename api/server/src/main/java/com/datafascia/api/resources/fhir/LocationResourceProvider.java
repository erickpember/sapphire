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
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.LocationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Location resource endpoint
 */
public class LocationResourceProvider implements IResourceProvider {

  @Inject
  private LocationRepository locationRepository;

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
   * @return resource matching this identifier
   * @throws ResourceNotFoundException if not found
   */
  @Read()
  public Location getResourceById(@IdParam IdDt resourceId) {
    Optional<Location> result = locationRepository.read(Id.of(resourceId.getIdPart()));
    if (result.isPresent()) {
      return result.get();
    } else {
      throw new ResourceNotFoundException("Location resource with ID: " + resourceId.getIdPart()
          + " not found.");
    }
  }

  /**
   * Retrieves a location using the external Location Identifier.
   *
   * @param locationIdentifier Identifier of Location resource.
   * @return list containing the matching resource, or an empty list if no match is found
   */
  @Search()
  public List<Location> searchByIdentifier(
      @RequiredParam(name = Location.SP_IDENTIFIER) StringParam locationIdentifier) {
    List<Location> locations = new ArrayList<>();
    Optional<Location> result = locationRepository.read(Id.of(locationIdentifier.getValue()));
    if (result.isPresent()) {
      locations.add(result.get());
    }
    return locations;
  }

  /**
   * Retrieves all locations
   *
   * @return
   *     all locations
   */
  @Search()
  public List<Location> search() {
    return locationRepository.list();
  }
}
