// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.ObservationRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Observation resource endpoint
 */
public class ObservationResourceProvider implements IResourceProvider {

  @Inject
  private ObservationRepository observationRepository;

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
   * Updates resource, or creates resource if no resource already exists for the id.
   *
   * @param observation
   *     new content to store
   * @return method outcome
   */
  @Update
  public MethodOutcome update(@ResourceParam Observation observation) {
    if (observation.getEncounter() == null) {
      throw new UnprocessableEntityException(
          "Cannot create Observation: missing encounter reference");
    }

    observationRepository.save(observation);
    return new MethodOutcome(observation.getId());
  }

  /**
   * Searches observations based on encounter. Returns list of Observations where
   * Observation.encounter matches a given Encounter resource ID.
   *
   * @param encounterId Internal resource ID for the Encounter for which we want corresponding
   *                    observations.
   * @param code        Type of Observation, the code member of the first entry in the Observation
   *                    CodeableConcept field Code.
   * @return Search results.
   */
  @Search()
  public List<Observation> search(
      @RequiredParam(name = Observation.SP_ENCOUNTER) StringParam encounterId,
      @OptionalParam(name = Observation.SP_CODE) StringParam code) {
    List<Observation> observations = new ArrayList<>();

    Id<Encounter> encounterInternalId = Id.of(encounterId.getValue());
    observations.addAll(observationRepository.list(encounterInternalId));

    if (code != null) {
      List<Observation> filteredResults = new ArrayList<>();
      for (Observation observation : observations) {
        if (observation.getCode().getCodingFirstRep().getCode().equalsIgnoreCase(code.getValue())) {
          filteredResults.add(observation);
        }
      }
      observations = filteredResults;
    }
    return observations;
  }
}
