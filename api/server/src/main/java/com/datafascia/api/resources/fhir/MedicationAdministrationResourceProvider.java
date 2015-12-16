// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.primitive.IdDt;
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
import com.datafascia.domain.persist.MedicationAdministrationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * MedicationAdministration resource endpoint
 */
public class MedicationAdministrationResourceProvider implements IResourceProvider {

  @Inject
  private MedicationAdministrationRepository medicationAdministrationRepository;

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   *
   * @return Class of resource.
   */
  @Override
  public Class<MedicationAdministration> getResourceType() {
    return MedicationAdministration.class;
  }

  /**
   * Store a new medicationAdministration.
   *
   * @param medicationAdministration The new medicationAdministration to store.
   * @return Outcome of create method. Resource ID of MedicationAdministration.
   */
  @Create
  public MethodOutcome create(@ResourceParam MedicationAdministration medicationAdministration) {
    if (medicationAdministration.getEncounter() != null) {
      medicationAdministrationRepository.save(medicationAdministration);
      return new MethodOutcome(medicationAdministration.getId());
    } else {
      throw new UnprocessableEntityException("Can not create MedicationAdministration:"
          + " encounter reference can not be null.");
    }
  }

  /**
   * Searches MedicationAdministrations based on encounter. Returns list of Medication
   * Administrations where MedicationAdministration.encounter matches a given Encounter resource ID.
   *
   * @param encounterId Internal resource ID for the Encounter for which we want corresponding
   *                    medicationAdministrations.
   * @return Search results.
   */
  @Search()
  public List<MedicationAdministration> searchByEncounterId(
      @RequiredParam(name = MedicationAdministration.SP_ENCOUNTER) StringParam encounterId) {
    Id<Encounter> encounterInternalId = Id.of(encounterId.getValue());
    List<MedicationAdministration> medicationAdministrations = medicationAdministrationRepository.
        list(encounterInternalId);

    return medicationAdministrations;
  }

  /**
   * Because the MedicationAdministrationRepository does not support single-argument reads, a
   * double-argument read method that requires the Encounter ID as well as the Medication
   * Administration ID is implemented here in the API as a search method.
   *
   * @param encounterId    Resource ID for the Encounter used in looking up a Administration.
   * @param administrationId Resource ID of the MedicationAdministration we want to retrieve.
   * @param prescriptionId Resource ID of the associated Prescription, for optional filtering.
   * @return A list containing up to 1 MedicationAdministration, matching this query.
   */
  @Search()
  public List<MedicationAdministration> search(
      @RequiredParam(name = MedicationAdministration.SP_ENCOUNTER) StringParam encounterId,
      @OptionalParam(name = MedicationAdministration.SP_RES_ID) StringParam administrationId,
      @OptionalParam(name = MedicationAdministration.SP_PRESCRIPTION) StringParam prescriptionId) {
    List<MedicationAdministration> medicationAdministrations = new ArrayList<>();

    // Retrieve single record
    if (administrationId != null) {
      Id<Encounter> encounterInternalId = Id.of(encounterId.getValue());
      Id<MedicationAdministration> administrationInternalId = Id.of(administrationId.getValue());
      Optional<MedicationAdministration> result = medicationAdministrationRepository.
          read(encounterInternalId, administrationInternalId);

      if (result.isPresent()) {
        medicationAdministrations.add(result.get());
      }
    } else {
      // Pull records for the encounter.
      medicationAdministrations.addAll(medicationAdministrationRepository.list(Id.of(encounterId.
          getValue())));
    }

    if (prescriptionId != null) {
      List<MedicationAdministration> filteredResults = new ArrayList<>();
      for (MedicationAdministration medicationAdministration : medicationAdministrations) {
        if (medicationAdministration.getPrescription().getReference().getIdPart().equalsIgnoreCase(
            prescriptionId.getValue())) {
          filteredResults.add(medicationAdministration);
        }
      }
      medicationAdministrations = filteredResults;
    }

    return medicationAdministrations;
  }

  /**
   * Completely replaces the content of the MedicationAdministration resource with the content given
   * in the request.
   *
   * @param medicationAdministration New MedicationAdministration value.
   * @return Outcome of create method. Resource ID of MedicationAdministration.
   */
  @Update
  public MethodOutcome update(@ResourceParam MedicationAdministration medicationAdministration) {
    IdDt resourceId = medicationAdministration.getId();
    if (resourceId == null) {
      throw new UnprocessableEntityException("MedicationAdministration: no ID supplied. "
          + "Can not update.");
    }

    checkForEncounterReference(medicationAdministration);

    Id<Encounter> encounterId = Ids.toPrimaryKey(medicationAdministration.getEncounter().
        getReference());

    // Check if medicationadministration already exists.
    Id<MedicationAdministration> medicationAdministrationId = MedicationAdministrationRepository.
        generateId(medicationAdministration);
    Optional<MedicationAdministration> optionalMedicationAdministration
        = medicationAdministrationRepository.read(encounterId, medicationAdministrationId);
    if (!optionalMedicationAdministration.isPresent()) {
      throw new InvalidRequestException(String.format(
          "MedicationAdministration ID [%s] did not already exist:",
          medicationAdministrationId));
    }

    medicationAdministrationRepository.save(medicationAdministration);
    return new MethodOutcome(medicationAdministration.getId());
  }

  private void checkForEncounterReference(MedicationAdministration administration) throws
      UnprocessableEntityException {
    if (administration.getEncounter() == null || administration.getEncounter().isEmpty()) {
      throw new UnprocessableEntityException("MedicationAdministration with identifier "
          + administration.getIdentifierFirstRep().getValue()
          + " lacks the mandatory reference to encounter, can not be saved or updated.");
    }
  }
}
