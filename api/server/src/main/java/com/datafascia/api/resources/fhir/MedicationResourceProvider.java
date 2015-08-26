// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Medication;
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
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.datafascia.common.fhir.DependencyInjectingResourceProvider;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.MedicationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Medication resource endpoint
 */
@NoArgsConstructor @Slf4j
public class MedicationResourceProvider extends DependencyInjectingResourceProvider {

  @Inject
  private MedicationRepository medicationRepository;

  @Override
  protected void onInjected() {
    log.info("medicationRepository {}", medicationRepository);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   *
   * @return Class of resource.
   */
  @Override
  public Class<Medication> getResourceType() {
    return Medication.class;
  }

  /**
   * Store a new medication.
   *
   * @param medication The new medication to store.
   * @return Outcome of create method. Resource ID of Medication.
   */
  @Create
  public MethodOutcome create(@ResourceParam Medication medication) {
    // Check if medication already exists.
    Id<Medication> medicationId = MedicationRepository.generateId(medication);
    Optional<Medication> optionalMedication = medicationRepository.read(medicationId);
    if (optionalMedication.isPresent()) {
      throw new InvalidRequestException(String.format("Medication ID [%s] already exists",
          medicationId));
    }

    medicationRepository.save(medication);
    return new MethodOutcome(medication.getId());
  }

  /**
   * Completely replaces the content of the medication resource with the content given in the
   * request.
   *
   * @param resourceId Id of resource to update.
   * @param medication New medication value.
   * @return Outcome of create method. Resource ID of Medication.
   */
  @Update
  public MethodOutcome update(@IdParam IdDt resourceId, @ResourceParam Medication medication) {
    if (resourceId == null) {
      throw new UnprocessableEntityException("No identifier supplied");
    }

    medicationRepository.save(medication);
    return new MethodOutcome(medication.getId());
  }

  /**
   * Retrieves a medication using the ID.
   *
   * @param resourceId ID of Medication resource.
   * @return resource matching this identifier
   * @throws ResourceNotFoundException if not found
   */
  @Read()
  public Medication getResourceById(@IdParam IdDt resourceId) {
    Optional<Medication> result = medicationRepository.read(Id.of(resourceId.getIdPart()));
    if (result.isPresent()) {
      return result.get();
    } else {
      throw new ResourceNotFoundException("Medication resource with ID: " + resourceId.getIdPart()
          + " not found.");
    }
  }

  /**
   * Retrieves a medication using the external Medication Code (used as Identifier).
   *
   * @param medicationCode Identifier of Medication resource.
   * @return list of resources matching this code, empty if none exist.
   */
  @Search()
  public List<Medication> searchResourceByCode(
      @RequiredParam(name = Medication.SP_CODE) StringParam medicationCode) {
    List<Medication> medications = new ArrayList<>();
    Optional<Medication> result = medicationRepository.read(Id.of(medicationCode.getValue()));
    if (result.isPresent()) {
      medications.add(result.get());
    }
    return medications;
  }
}
