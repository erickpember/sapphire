// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
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
import com.datafascia.domain.persist.EncounterRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Encounter resource endpoint
 */
@NoArgsConstructor @Slf4j
public class EncounterResourceProvider extends DependencyInjectingResourceProvider {

  @Inject
  private EncounterRepository encounterRepository;

  @Override
  protected void onInjected() {
    log.info("encounterRepository {}", encounterRepository);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   *
   * @return Class of resource.
   */
  @Override
  public Class<Encounter> getResourceType() {
    return Encounter.class;
  }

  /**
   * Store a new encounter.
   *
   * @param encounter The new encounter to store.
   * @return Outcome of create method. Resource ID of Encounter.
   */
  @Create
  public MethodOutcome create(@ResourceParam Encounter encounter) {
    // Check if encounter already exists.
    Id<Encounter> encounterId = EncounterRepository.generateId(encounter);
    Optional<Encounter> optionalEncounter = encounterRepository.read(encounterId);
    if (optionalEncounter.isPresent()) {
      throw new InvalidRequestException(String.format("Encounter ID [%s] already exists",
          encounterId));
    }

    encounterRepository.save(encounter);
    return new MethodOutcome(encounter.getId());
  }

  /**
   * Completely replaces the content of the encounter resource with the content given in the
   * request.
   *
   * @param resourceId Id of resource to update.
   * @param encounter  New encounter value.
   * @return Outcome of create method. Resource ID of Encounter.
   */
  @Update
  public MethodOutcome update(@IdParam IdDt resourceId, @ResourceParam Encounter encounter) {
    if (resourceId == null) {
      throw new UnprocessableEntityException("No identifier supplied");
    }

    // Check if encounter already exists.
    Id<Encounter> encounterId = EncounterRepository.generateId(encounter);
    Optional<Encounter> optionalEncounter = encounterRepository.read(encounterId);
    if (!optionalEncounter.isPresent()) {
      throw new InvalidRequestException(String.format("Encounter ID [%s] did not already exist:",
          encounterId));
    }

    encounterRepository.save(encounter);
    return new MethodOutcome(encounter.getId());
  }

  /**
   * Deletes encounter.
   *
   * @param resourceId ID of encounter resource.
   */
  @Delete()
  public void deleteEncounter(@IdParam IdDt resourceId) {
    Id<Encounter> encounterId = Id.of(resourceId.getIdPart());
    encounterRepository.delete(encounterId);
  }

  /**
   * Retrieves a encounter using the ID.
   *
   * @param resourceId ID of Encounter resource.
   * @return resource matching this identifier
   * @throws ResourceNotFoundException if not found
   */
  @Read()
  public Encounter getResourceById(@IdParam IdDt resourceId) {
    Optional<Encounter> result = encounterRepository.read(Id.of(resourceId.getIdPart()));
    if (result.isPresent()) {
      return result.get();
    } else {
      throw new ResourceNotFoundException("Encounter resource with ID: " + resourceId.getIdPart()
          + " not found.");
    }
  }

  /**
   * Retrieves a encounter using the external Encounter Identifier.
   *
   * @param encounterIdentifier Identifier of Encounter resource.
   * @return list containing the matching encounter, or an empty list.
   */
  @Search()
  public List<Encounter> searchByIdentifier(
      @RequiredParam(name = Encounter.SP_IDENTIFIER) StringParam encounterIdentifier) {
    List<Encounter> encounters = new ArrayList<>();
    Optional<Encounter> result = encounterRepository.read(Id.of(encounterIdentifier.getValue()));
    if (result.isPresent()) {
      encounters.add(result.get());
    }
    return encounters;
  }

  /**
   * Searches encounters based on status, or just returns all encounters.
   *
   * @param status Status of encounter as an optional filtering parameter.
   * @return Search results.
   */
  @Search()
  public List<Encounter> listById(@OptionalParam(name = Encounter.SP_STATUS) StringParam status) {
    Optional<EncounterStateEnum> optStatus;
    if (status == null) {
      optStatus = Optional.empty();
    } else {
      optStatus = Optional.of(EncounterStateEnum.valueOf(status.getValue()));
    }

    List<Encounter> encounters = encounterRepository.list(optStatus);
    return encounters;
  }
}
