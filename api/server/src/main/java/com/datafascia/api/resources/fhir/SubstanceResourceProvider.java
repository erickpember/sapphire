// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Substance;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.datafascia.common.api.ApiParams;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.SubstanceRepository;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Substance resource endpoint
 */
public class SubstanceResourceProvider implements IResourceProvider {

  public static final int MAX_DEFAULT_SUBSTANCE_RESULTS = 100000;

  @Inject
  private SubstanceRepository substanceRepository;

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   *
   * @return Class of resource.
   */
  @Override
  public Class<Substance> getResourceType() {
    return Substance.class;
  }

  /**
   * Store a new substance.
   *
   * @param substance The new substance to store.
   * @return Outcome of create method. Resource ID of substance.
   */
  @Create
  public MethodOutcome create(@ResourceParam Substance substance) {
    // Check if substance already exists.
    Id<Substance> substanceId = SubstanceRepository.generateId(substance);
    Optional<Substance> optionalSubstance = substanceRepository.read(substanceId);
    if (optionalSubstance.isPresent()) {
      throw new InvalidRequestException(
          String.format("Substance ID [%s] already exists", substanceId));
    }

    substanceRepository.save(substance);
    return new MethodOutcome(substance.getId());
  }

  /**
   * Completely replaces the content of the substance resource with the content given in the
   * request.
   *
   * @param resourceId Id of resource to update.
   * @param substance New substance value.
   * @return Outcome of create method. Resource ID of Substance.
   */
  @Update
  public MethodOutcome update(@IdParam IdDt resourceId,
      @ResourceParam Substance substance) {
    if (resourceId == null) {
      throw new UnprocessableEntityException("No identifier supplied");
    }

    substanceRepository.save(substance);
    return new MethodOutcome(substance.getId());
  }

  /**
   * Retrieves a substance using the ID.
   *
   * @param resourceId ID of substance resource.
   * @return resource matching this identifier
   * @throws ResourceNotFoundException if not found
   */
  @Read()
  public Substance getResourceById(@IdParam IdDt resourceId) {
    Optional<Substance> result = substanceRepository.read(Id.of(resourceId.getIdPart()));
    if (result.isPresent()) {
      return result.get();
    } else {
      throw new ResourceNotFoundException("Substance resource with ID: " + resourceId.getIdPart()
          + " not found.");
    }
  }

  /**
   * Searches substances based on whether or not they are active.
   *
   * @param startSubstanceId If present, start the scan from this substance ID.
   * @param count Maximum number of substances to return in page.
   * @return Search results.
   */
  @Search()
  public List<Substance> list(
      @OptionalParam(name = Substance.SP_RES_ID) StringParam startSubstanceId,
      @OptionalParam(name = ApiParams.COUNT) NumberParam count) {
    Optional<Id<Substance>> optStartSubstanceId;
    if (startSubstanceId == null) {
      optStartSubstanceId = Optional.empty();
    } else {
      optStartSubstanceId = Optional.of(Id.of(startSubstanceId.getValue()));
    }

    if (count == null) {
      count = new NumberParam(Integer.toString(MAX_DEFAULT_SUBSTANCE_RESULTS));
    }

    List<Substance> substances = substanceRepository.list(
        optStartSubstanceId, count.getValue().intValueExact());
    return substances;
  }
}
