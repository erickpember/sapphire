// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.MedicationPrescription;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.datafascia.common.fhir.DependencyInjectingResourceProvider;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.Ids;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.MedicationPrescriptionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MedicationPrescription resource endpoint
 */
@NoArgsConstructor @Slf4j
public class MedicationPrescriptionResourceProvider extends DependencyInjectingResourceProvider {

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private MedicationPrescriptionRepository medicationPrescriptionRepository;

  @Override
  protected void onInjected() {
    log.info("medicationPrescriptionRepository {}", medicationPrescriptionRepository);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   *
   * @return Class of resource.
   */
  @Override
  public Class<MedicationPrescription> getResourceType() {
    return MedicationPrescription.class;
  }

  /**
   * Store a new MedicationPrescription.
   *
   * @param medicationPrescription The new medicationPrescription to store.
   * @return Outcome of create method. Resource ID of MedicationPrescription.
   */
  @Create
  public MethodOutcome create(@ResourceParam MedicationPrescription medicationPrescription) {
    if (medicationPrescription.getEncounter() != null) {
      medicationPrescriptionRepository.save(medicationPrescription);
      return new MethodOutcome(medicationPrescription.getId());
    } else {
      throw new UnprocessableEntityException("Can not create MedicationPrescription:"
          + " encounter reference can not be null.");
    }
  }

  /**
   * Because the MedicationPrescriptionRepository does not support single-argument reads, a
   * double-argument read method that requires the Encounter ID as well as the Medication
   * Prescription ID is implemented here in the API as a search method.
   * Absent an encounterID, all encounters are searched.
   * Absent a prescriptionId, all prescriptions are searched.
   *
   * @param encounterId    Internal resource ID for the Encounter used in looking up a Prescription.
   * @param prescriptionId Resource ID of the specific MedicationPrescription we want to retrieve.
   * @return
   *     A list containing MedicationPrescriptions, matching this query.
   */
  @Search()
  public List<MedicationPrescription> search(
      @OptionalParam(name = MedicationPrescription.SP_ENCOUNTER) StringParam encounterId,
      @OptionalParam(name = MedicationPrescription.SP_RES_ID) StringParam prescriptionId) {
    List<MedicationPrescription> medicationPrescriptions = new ArrayList<>();

    if (encounterId != null && prescriptionId != null) {
      Id<Encounter> encounterInternalId = Id.of(encounterId.getValue());
      Id<MedicationPrescription> prescriptionInternalId = Id.of(prescriptionId.getValue());
      Optional<MedicationPrescription> result = medicationPrescriptionRepository.
          read(encounterInternalId, prescriptionInternalId);

      if (result.isPresent()) {
        medicationPrescriptions.add(result.get());
      }
    } else {
      // Pull records for one encounter.
      if (encounterId != null) {
        medicationPrescriptions.addAll(medicationPrescriptionRepository.list(Id.of(encounterId.
            getValue())));
      } else {
        // Pull records for all encounters.
        List<Encounter> allEncounters = encounterRepository.list(Optional.empty());
        for (Encounter eachEncounter : allEncounters) {
          List<MedicationPrescription> admins = medicationPrescriptionRepository.list(
              EncounterRepository.generateId(eachEncounter));
          medicationPrescriptions.addAll(admins);
        }

        // Filter by prescription ID if necessary. This is a very inefficient 1-arg get.
        if (prescriptionId != null) {
          List<MedicationPrescription> filteredResults = new ArrayList<>();
          for (MedicationPrescription medicationPrescription : medicationPrescriptions) {
            if (medicationPrescription.getId().getIdPart().equalsIgnoreCase(
                prescriptionId.getValue())) {
              filteredResults.add(medicationPrescription);
              break;
            }
          }
          medicationPrescriptions = filteredResults;
        }
      }
    }

    return medicationPrescriptions;
  }

  /**
   * Completely replaces the content of the MedicationPrescription resource with the content given
   * in the request.
   *
   * @param medicationPrescription New MedicationPrescription value.
   * @return Outcome of create method. Resource ID of MedicationPrescription.
   */
  @Update
  public MethodOutcome update(@ResourceParam MedicationPrescription medicationPrescription) {
    IdDt resourceId = medicationPrescription.getId();
    if (resourceId == null) {
      throw new UnprocessableEntityException("MedicationPrescription: no ID supplied. "
          + "Can not update.");
    }

    checkForEncounterReference(medicationPrescription);

    Id<Encounter> encounterId = Ids.toPrimaryKey(medicationPrescription.getEncounter().
        getReference());

    // Check if medicationprescription already exists.
    Id<MedicationPrescription> medicationPrescriptionId = MedicationPrescriptionRepository.
        generateId(medicationPrescription);
    Optional<MedicationPrescription> optionalMedicationPrescription
        = medicationPrescriptionRepository.read(encounterId, medicationPrescriptionId);
    if (!optionalMedicationPrescription.isPresent()) {
      throw new InvalidRequestException(String.format(
          "MedicationPrescription ID [%s] did not already exist:",
          medicationPrescriptionId));
    }

    medicationPrescriptionRepository.save(medicationPrescription);
    return new MethodOutcome(medicationPrescription.getId());
  }

  private void checkForEncounterReference(MedicationPrescription prescription) throws
      UnprocessableEntityException {
    if (prescription.getEncounter() == null || prescription.getEncounter().isEmpty()) {
      throw new UnprocessableEntityException("MedicationPrescription with identifier "
          + prescription.getIdentifierFirstRep().getValue()
          + " lacks the mandatory reference to encounter, can not be saved or updated.");
    }
  }
}
