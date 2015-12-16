// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.ProcedureRepository;
import java.util.List;
import javax.inject.Inject;

/**
 * Procedure resource endpoint
 */
public class ProcedureResourceProvider implements IResourceProvider {

  @Inject
  private ProcedureRepository procedureRepository;

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   *
   * @return Class of resource.
   */
  @Override
  public Class<Procedure> getResourceType() {
    return Procedure.class;
  }

  /**
   * Store a new procedure.
   *
   * @param procedure The new procedure to store.
   * @return Outcome of create method. Resource ID of Procedure.
   */
  @Create
  public MethodOutcome create(@ResourceParam Procedure procedure) {
    procedureRepository.save(procedure);
    return new MethodOutcome(procedure.getId());
  }

  /**
   * Searches procedures based on encounter. Returns list of Procedures where
   * Procedure.encounter matches a given Encounter resource ID.
   *
   * @param encounterId Internal resource ID for the Encounter for which we want corresponding
   *                    procedures.
   * @return Search results.
   */
  @Search()
  public List<Procedure> searchByEncounterId(
      @RequiredParam(name = Procedure.SP_ENCOUNTER) StringParam encounterId) {
    Id<Encounter> encounterInternalId = Id.of(encounterId.getValue());
    List<Procedure> procedures = procedureRepository.list(encounterInternalId);
    return procedures;
  }
}
