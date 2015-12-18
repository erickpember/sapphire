// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.EpisodeOfCare;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
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
import com.datafascia.domain.persist.EpisodeOfCareRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * EpisodeOfCare resource endpoint
 */
public class EpisodeOfCareResourceProvider implements IResourceProvider {

  @Inject
  private EpisodeOfCareRepository episodeOfCareRepository;

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   *
   * @return Class of resource.
   */
  @Override
  public Class<EpisodeOfCare> getResourceType() {
    return EpisodeOfCare.class;
  }

  /**
   * Store a new EpisodeOfCare.
   *
   * @param episodeOfCare The new EpisodeOfCare to store.
   * @return Outcome of create method. Resource ID of EpisodeOfCare.
   */
  @Create
  public MethodOutcome create(@ResourceParam EpisodeOfCare episodeOfCare) {
    // Check if EpisodeOfCare already exists.
    Id<EpisodeOfCare> episodeOfCareId = episodeOfCareRepository.generateId(episodeOfCare);
    Optional<EpisodeOfCare> optionalEpisodeOfCare = episodeOfCareRepository.read(episodeOfCareId);
    if (optionalEpisodeOfCare.isPresent()) {
      throw new InvalidRequestException(String.format("EpisodeOfCare ID [%s] already exists",
          episodeOfCareId));
    }

    episodeOfCareRepository.save(episodeOfCare);
    return new MethodOutcome(episodeOfCare.getId());
  }

  /**
   * Completely replaces the content of the EpisodeOfCare resource with the content given in the
   * request.
   *
   * @param resourceId Id of resource to update.
   * @param episodeOfCare  New EpisodeOfCare value.
   * @return Outcome of create method. Resource ID of EpisodeOfCare.
   */
  @Update
  public MethodOutcome update(@IdParam IdDt resourceId,
      @ResourceParam EpisodeOfCare episodeOfCare) {
    if (resourceId == null) {
      throw new UnprocessableEntityException("No identifier supplied");
    }

    // Check if EpisodeOfCare already exists.
    Id<EpisodeOfCare> episodeOfCareId = EpisodeOfCareRepository.generateId(episodeOfCare);
    Optional<EpisodeOfCare> optionalEpisodeOfCare = episodeOfCareRepository.read(episodeOfCareId);
    if (!optionalEpisodeOfCare.isPresent()) {
      throw new InvalidRequestException(
          String.format("EpisodeOfCare ID [%s] did not already exist:", episodeOfCareId));
    }

    episodeOfCareRepository.save(episodeOfCare);
    return new MethodOutcome(episodeOfCare.getId());
  }

  /**
   * Deletes EpisodeOfCare.
   *
   * @param episodeOfCareId ID of EpisodeOfCare resource.
   */
  @Delete()
  public void delete(@IdParam IdDt episodeOfCareId) {
    episodeOfCareRepository.delete(Id.of(episodeOfCareId.getIdPart()));
  }

  /**
   * Retrieves a EpisodeOfCare using the ID.
   *
   * @param episodeOfCareId ID of EpisodeOfCare resource.
   * @return resource matching this identifier
   * @throws ResourceNotFoundException if not found
   */
  @Read()
  public EpisodeOfCare getResourceById(@IdParam IdDt episodeOfCareId) {
    Optional<EpisodeOfCare> result
        = episodeOfCareRepository.read(Id.of(episodeOfCareId.getIdPart()));
    if (result.isPresent()) {
      return result.get();
    } else {
      throw new ResourceNotFoundException("EpisodeOfCare resource with ID: "
          + episodeOfCareId.getIdPart() + " not found.");
    }
  }

  /**
   * Retrieves a EpisodeOfCare using the external EpisodeOfCare Identifier.
   *
   * @param episodeOfCareIdentifier Identifier of EpisodeOfCare resource.
   * @return list containing the matching EpisodeOfCare, or an empty list.
   */
  @Search()
  public List<EpisodeOfCare> searchByIdentifier(
      @RequiredParam(name = EpisodeOfCare.SP_IDENTIFIER) StringParam episodeOfCareIdentifier) {
    List<EpisodeOfCare> episodeOfCares = new ArrayList<>();
    Optional<EpisodeOfCare> result
        = episodeOfCareRepository.read(Id.of(episodeOfCareIdentifier.getValue()));
    if (result.isPresent()) {
      episodeOfCares.add(result.get());
    }
    return episodeOfCares;
  }
}
