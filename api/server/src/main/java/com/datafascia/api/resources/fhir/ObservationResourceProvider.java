// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import com.datafascia.common.fhir.DependencyInjectingResourceProvider;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.Ids;
import com.datafascia.domain.persist.ObservationRepository;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Observation resource endpoint
 */
@NoArgsConstructor @Slf4j
public class ObservationResourceProvider extends DependencyInjectingResourceProvider {

  @Inject
  private ObservationRepository observationRepository;

  @Override
  protected void onInjected() {
    log.info("observationRepository {}", observationRepository);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   *
   * @return Class of resource.
   */
  @Override
  public Class<Observation> getResourceType() {
    return Observation.class;
  }

  /**
   * Store a new observation.
   *
   * @param observation The new observation to store. Valid encounter reference required.
   * @return Outcome of create method. Resource ID of Observation.
   */
  @Create
  public MethodOutcome create(@ResourceParam Observation observation) {
    // Check if observation already exists.
    Id<Observation> observationId = ObservationRepository.generateId(observation);
    Id<Encounter> encounterId = Ids.toPrimaryKey(observation.getEncounter().getReference());
    Optional<Observation> optionalObservation = observationRepository.read(encounterId,
            observationId);
    if (optionalObservation.isPresent()) {
      throw new InvalidRequestException(String.format("Observation ID [%s] already exists",
              observationId));
    }

    observationRepository.save(encounterId, observation);
    return new MethodOutcome(observation.getId());
  }

  /**
   * Deletes observation.
   *
   * @param observationId ID of observation resource.
   * @param encounterId ID of parent encounter resource.
   */
  @Delete()
  public void deleteObservation(@IdParam IdDt observationId, @IdParam IdDt encounterId) {
    Id<Encounter> encounterInternalId = Id.of(encounterId.getIdPart());
    Id<Observation> observationInternalId = Id.of(observationId.getIdPart());
    observationRepository.delete(encounterInternalId, observationInternalId);
  }

  /**
   * Retrieves an observation using the ID.
   *
   * @param observationId ID of observation resource.
   * @param encounterId ID of parent encounter resource.
   * @return Returns a resource matching this identifier, or null if none exists.
   */
  @Read()
  public Observation getResourceById(@IdParam IdDt observationId, @IdParam IdDt encounterId) {
    Id<Encounter> encounterInternalId = Id.of(encounterId.getIdPart());
    Id<Observation> observationInternalId = Id.of(observationId.getIdPart());
    return observationRepository.read(encounterInternalId, observationInternalId).get();
  }

  /**
   * Searches observations based on encounter.
   *
   * @param encounterId Encounter for which we want corresponding observations.
   * @return Search results.
   */
  @Search()
  public List<Observation> search(@IdParam IdDt encounterId) {
    Id<Encounter> encounterInternalId = Id.of(encounterId.getIdPart());
    List<Observation> observations = observationRepository.list(encounterInternalId);
    return observations;
  }
}
