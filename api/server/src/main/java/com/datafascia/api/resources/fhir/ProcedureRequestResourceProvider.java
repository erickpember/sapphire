// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.Ids;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.ProcedureRequestRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * ProcedureRequest resource endpoint
 */
public class ProcedureRequestResourceProvider implements IResourceProvider {

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private ProcedureRequestRepository procedureRequestRepository;

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   *
   * @return Class of resource.
   */
  @Override
  public Class<ProcedureRequest> getResourceType() {
    return ProcedureRequest.class;
  }

  /**
   * Store a new ProcedureRequest.
   *
   * @param procedureRequest The new procedureRequest to store.
   * @return Outcome of create method. Resource ID of ProcedureRequest.
   */
  @Create
  public MethodOutcome create(@ResourceParam ProcedureRequest procedureRequest) {
    validate(procedureRequest);
    procedureRequestRepository.save(procedureRequest);
    return new MethodOutcome(procedureRequest.getId());
  }

  /**
   * Searches ProcedureRequests based on encounter. Returns list of Procedure Requests
   * where ProcedureRequest.encounter matches a given Encounter resource ID.
   *
   * @param encounterId Internal resource ID for the Encounter for which we want corresponding
   *                    procedureRequests.
   * @return Search results.
   */
  @Search()
  public List<ProcedureRequest> searchByEncounterId(
      @RequiredParam(name = ProcedureRequest.SP_ENCOUNTER) StringParam encounterId) {
    Id<Encounter> encounterInternalId = Id.of(encounterId.getValue());
    List<ProcedureRequest> procedureRequests = procedureRequestRepository.
        list(encounterInternalId);

    return procedureRequests;
  }

  /**
   * Because the ProcedureRequestRepository does not support single-argument reads, a
   * double-argument read method that requires the Encounter ID as well as the Procedure
   * Request ID is implemented here in the API as a search method.
   *
   * @param encounterId    Internal resource ID for the Encounter used in looking up a Request.
   * @param requestId Resource ID of the specific ProcedureRequest we want to retrieve.
   * @return A list containing up to 1 ProcedureRequest, matching this query.
   */
  @Search()
  public List<ProcedureRequest> searchByEncounterIdAndProcedureRequestId(
      @RequiredParam(name = ProcedureRequest.SP_ENCOUNTER) StringParam encounterId,
      @RequiredParam(name = ProcedureRequest.SP_RES_ID) StringParam requestId) {
    Id<Encounter> encounterInternalId = Id.of(encounterId.getValue());
    Id<ProcedureRequest> requestInternalId = Id.of(requestId.getValue());
    List<ProcedureRequest> procedureRequests = new ArrayList<>();
    Optional<ProcedureRequest> result = procedureRequestRepository.
        read(encounterInternalId, requestInternalId);

    if (result.isPresent()) {
      procedureRequests.add(result.get());
    }

    return procedureRequests;
  }

  /**
   * Because the ProcedureRequestRepository does not support single-argument reads, a
   * double-argument read method that requires the Encounter ID as well as the Procedure
   * Request ID is implemented here in the API as a search method.
   * Absent an encounterId, all encounters are searched.
   * Absent a requestId, all requests are searched.
   *
   * @param encounterId    Internal resource ID for the Encounter used in looking up a Request.
   * @param requestId Resource ID of the specific ProcedureRequest we want to retrieve.
   * @return A list containing up to 1 ProcedureRequest, matching this query.
   */
  @Search()
  public List<ProcedureRequest> search(
      @OptionalParam(name = ProcedureRequest.SP_ENCOUNTER) StringParam encounterId,
      @OptionalParam(name = ProcedureRequest.SP_RES_ID) StringParam requestId) {
    List<ProcedureRequest> procedureRequests = new ArrayList<>();

    if (encounterId != null && requestId != null) {
      Id<Encounter> encounterInternalId = Id.of(encounterId.getValue());
      Id<ProcedureRequest> requestInternalId = Id.of(requestId.getValue());
      Optional<ProcedureRequest> result = procedureRequestRepository.
          read(encounterInternalId, requestInternalId);

      if (result.isPresent()) {
        procedureRequests.add(result.get());
      }
    } else {
      // Pull records for one encounter.
      if (encounterId != null) {
        procedureRequests.addAll(procedureRequestRepository.list(Id.of(encounterId.
            getValue())));
      } else {
        // Pull records for all encounters.
        List<Encounter> allEncounters = encounterRepository.list(Optional.empty());
        for (Encounter eachEncounter : allEncounters) {
          List<ProcedureRequest> admins = procedureRequestRepository.list(
              EncounterRepository.generateId(eachEncounter));
          procedureRequests.addAll(admins);
        }

        // Filter by request ID if necessary. This is a very inefficient 1-arg get.
        if (requestId != null) {
          List<ProcedureRequest> filteredResults = new ArrayList<>();
          for (ProcedureRequest procedureRequest : procedureRequests) {
            if (procedureRequest.getId().getIdPart().equalsIgnoreCase(
                requestId.getValue())) {
              filteredResults.add(procedureRequest);
              break;
            }
          }
          procedureRequests = filteredResults;
        }
      }
    }
    return procedureRequests;
  }

  /**
   * Completely replaces the content of the ProcedureRequest resource with the content given
   * in the request.
   *
   * @param procedureRequest New ProcedureRequest value.
   * @return Outcome of create method. Resource ID of ProcedureRequest.
   */
  @Update
  public MethodOutcome update(@ResourceParam ProcedureRequest procedureRequest) {
    validate(procedureRequest);

    Id<Encounter> encounterId = Ids.toPrimaryKey(procedureRequest.getEncounter().
        getReference());

    // Check if procedurerequest already exists.
    Id<ProcedureRequest> procedureRequestId = ProcedureRequestRepository.
        generateId(procedureRequest);
    Optional<ProcedureRequest> optionalProcedureRequest
        = procedureRequestRepository.read(encounterId, procedureRequestId);
    if (!optionalProcedureRequest.isPresent()) {
      throw new InvalidRequestException(String.format(
          "ProcedureRequest ID [%s] did not already exist:",
          procedureRequestId));
    }

    procedureRequestRepository.save(procedureRequest);
    return new MethodOutcome(procedureRequest.getId());
  }

  private void validate(ProcedureRequest request) throws
      UnprocessableEntityException {
    if (request.getId() == null) {
      throw new UnprocessableEntityException("ProcedureRequest: no ID supplied. "
          + "Can not save or update.");
    }
    if (request.getEncounter() == null || request.getEncounter().isEmpty()) {
      throw new UnprocessableEntityException("ProcedureRequest with identifier "
          + request.getIdentifierFirstRep().getValue()
          + " lacks the mandatory reference to encounter, can not be saved or updated.");
    }
  }
}
