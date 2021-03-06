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
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Practitioner;
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
import com.datafascia.domain.persist.PractitionerRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Practitioner resource endpoint
 */
public class PractitionerResourceProvider implements IResourceProvider {

  @Inject
  private PractitionerRepository practitionerRepository;

  @Override
  public Class<Practitioner> getResourceType() {
    return Practitioner.class;
  }

  /**
   * Store a new practitioner.
   *
   * @param practitioner
   *     new practitioner to store.
   * @return outcome of create method. Resource ID of Practitioner.
   */
  @Create
  public MethodOutcome create(@ResourceParam Practitioner practitioner) {
    // Check if practitioner already exists.
    Id<Practitioner> practitionerId = PractitionerRepository.generateId(practitioner);
    Optional<Practitioner> optionalPractitioner = practitionerRepository.read(practitionerId);
    if (optionalPractitioner.isPresent()) {
      throw new InvalidRequestException(
          String.format("Practitioner ID [%s] already exists", practitionerId));
    }

    practitionerRepository.save(practitioner);
    return new MethodOutcome(practitioner.getId());
  }

  /**
   * Completely replaces the content of the practitioner resource with the content given in the
   * request.
   *
   * @param resourceId
   *     ID of resource to update.
   * @param practitioner
   *     new practitioner value.
   * @return outcome of create method. Resource ID of Practitioner.
   */
  @Update
  public MethodOutcome update(@IdParam IdDt resourceId, @ResourceParam Practitioner practitioner) {
    if (resourceId == null) {
      throw new UnprocessableEntityException("No identifier supplied");
    }

    practitionerRepository.save(practitioner);
    return new MethodOutcome(practitioner.getId());
  }

  /**
   * Retrieves a practitioner using the ID.
   *
   * @param resourceId
   *     ID of resource.
   * @return resource matching this identifier
   * @throws ResourceNotFoundException
   *     if not found
   */
  @Read()
  public Practitioner getResourceById(@IdParam IdDt resourceId) {
    Optional<Practitioner> result = practitionerRepository.read(Id.of(resourceId.getIdPart()));
    if (result.isPresent()) {
      return result.get();
    } else {
      throw new ResourceNotFoundException(
          "Practitioner resource with ID: " + resourceId.getIdPart() + " not found.");
    }
  }

  /**
   * Retrieves a practitioner specified by external practitioner identifier.
   *
   * @param practitionerIdentifier
   *     external practitioner identifier
   * @return list containing the matching resource, or an empty list if no match is found
   */
  @Search()
  public List<Practitioner> searchByIdentifier(
      @RequiredParam(name = Practitioner.SP_IDENTIFIER) StringParam practitionerIdentifier) {
    List<Practitioner> practitioners = new ArrayList<>();
    Optional<Practitioner> result = practitionerRepository.read(
        Id.of(practitionerIdentifier.getValue()));
    if (result.isPresent()) {
      practitioners.add(result.get());
    }
    return practitioners;
  }

  /**
   * Retrieves all practitioners.
   *
   * @return all practitioners
   */
  @Search()
  public List<Practitioner> search() {
    return practitionerRepository.list();
  }
}
