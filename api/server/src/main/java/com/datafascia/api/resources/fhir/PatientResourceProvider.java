// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.datafascia.common.api.ApiParams;
import com.datafascia.common.fhir.DependencyInjectingResourceProvider;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.PatientRepository;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Patient resource endpoint
 */
@NoArgsConstructor @Slf4j
public class PatientResourceProvider extends DependencyInjectingResourceProvider {

  @Inject
  private PatientRepository patientRepository;

  @Override
  protected void onInjected() {
    log.info("patientRepository {}", patientRepository);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   *
   * @return Class of resource.
   */
  @Override
  public Class<UnitedStatesPatient> getResourceType() {
    return UnitedStatesPatient.class;
  }

  /**
   * Store a new patient.
   *
   * @param patient The new patient to store.
   * @return Outcome of create method. Resource ID of Patient.
   */
  @Create
  public MethodOutcome create(@ResourceParam UnitedStatesPatient patient) {
    // Check if patient already exists.
    Id<UnitedStatesPatient> patientId = PatientRepository.generateId(patient);
    Optional<UnitedStatesPatient> optionalPatient = patientRepository.read(patientId);
    if (optionalPatient.isPresent()) {
      throw new InvalidRequestException(String.format("Patient ID [%s] already exists", patientId));
    }

    patientRepository.save(patient);
    return new MethodOutcome(patient.getId());
  }

  /**
   * Completely replaces the content of the patient resource with the content given in the request.
   *
   * @param resourceId Id of resource to update.
   * @param patient New patient value.
   * @return Outcome of create method. Resource ID of Patient.
   */
  @Update
  public MethodOutcome update(@IdParam IdDt resourceId,
          @ResourceParam UnitedStatesPatient patient) {
    if (resourceId == null) {
      throw new UnprocessableEntityException("No identifier supplied");
    }

    patientRepository.save(patient);
    return new MethodOutcome(patient.getId());
  }

  /**
   * Deletes patient.
   *
   * @param resourceId ID of patient resource.
   */
  @Delete()
  public void deletePatient(@IdParam IdDt resourceId) {
    Id<UnitedStatesPatient> patientId = Id.of(resourceId.getIdPart());
    patientRepository.delete(patientId);
  }

  /**
   * Retrieves a patient using the ID.
   *
   * @param resourceId ID of patient resource.
   * @return Returns a resource matching this identifier, or null if none exists.
   */
  @Read()
  public UnitedStatesPatient getResourceById(@IdParam IdDt resourceId) {
    return patientRepository.read(Id.of(resourceId.getIdPart())).get();
  }

  /**
   * Searches patients based on whether or not they are active.
   *
   * @param startPatientId If present, start the scan from this patient ID.
   * @param isActive Whether to search for active or inactive patients.
   * @param count Maximum number of patients to return in page.
   * @return Search results.
   */
  @Search()
  public List<UnitedStatesPatient> list(
          @OptionalParam(name = UnitedStatesPatient.SP_RES_ID) StringParam startPatientId,
          @OptionalParam(name = UnitedStatesPatient.SP_ACTIVE) StringParam isActive,
          @OptionalParam(name = ApiParams.COUNT) NumberParam count) {
    Optional<Id<UnitedStatesPatient>> optStartPatientId;
    if (startPatientId == null) {
      optStartPatientId = Optional.empty();
    } else {
      optStartPatientId = Optional.of(Id.of(startPatientId.getValue()));
    }

    Optional<Boolean> optActive;
    if (isActive == null) {
      optActive = Optional.empty();
    } else {
      optActive = Optional.of(Boolean.parseBoolean(isActive.getValue()));
    }

    if (count == null) {
      count = new NumberParam("100");
    }

    List<UnitedStatesPatient> patients = patientRepository.list(
            optStartPatientId, optActive, count.getValue().intValueExact());
    return patients;
  }
}
