// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.ObservationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Observation resource endpoint
 */
public class ObservationResourceProvider implements IResourceProvider {

  @Inject
  private EncounterRepository encounterRepository;

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
   * Store a new observation.
   *
   * @param observation The new observation to store.
   * @return Outcome of create method. Resource ID of Observation.
   */
  @Create
  public MethodOutcome create(@ResourceParam Observation observation) {
    if (observation.getEncounter() != null) {
      observationRepository.save(observation);
      return new MethodOutcome(observation.getId());
    } else {
      throw new UnprocessableEntityException("Can not create Observation:"
          + " encounter reference can not be null.");
    }
  }

  /**
   * Searches observations based on encounter. Returns list of Observations where
   * Observation.encounter matches a given Encounter resource ID.
   *
   * If encounterId is left blank, observations for all encounters are retrieved.
   *
   * @param encounterId Internal resource ID for the Encounter for which we want corresponding
   *                    observations.
   * @param code        Type of Observation, the code member of the first entry in the Observation
   *                    CodeableConcept field Code.
   * @return Search results.
   */
  @Search()
  public List<Observation> search(
      @OptionalParam(name = Observation.SP_ENCOUNTER) StringParam encounterId,
      @OptionalParam(name = Observation.SP_CODE) StringParam code) {
    List<Observation> observations = new ArrayList<>();

    if (encounterId != null) {
      Id<Encounter> encounterInternalId = Id.of(encounterId.getValue());
      observations.addAll(observationRepository.list(encounterInternalId));
    } else {
      // Pull records for all encounters.
      List<Encounter> allEncounters = encounterRepository.list(Optional.empty());
      for (Encounter eachEncounter : allEncounters) {
        List<Observation> admins = observationRepository.list(
            EncounterRepository.generateId(eachEncounter));
        observations.addAll(admins);
      }
    }

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
