// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.MedicationPrescription;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import com.datafascia.common.fhir.DependencyInjectingResourceProvider;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.MedicationPrescriptionRepository;
import java.util.List;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MedicationPrescription resource endpoint
 */
@NoArgsConstructor @Slf4j
public class MedicationPrescriptionResourceProvider extends DependencyInjectingResourceProvider {

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
    medicationPrescriptionRepository.save(medicationPrescription);
    return new MethodOutcome(medicationPrescription.getId());
  }

  /**
   * Searches MedicationPrescriptions based on encounter. Returns list of Medication Prescriptions
   * where MedicationPrescription.encounter matches a given Encounter resource ID.
   *
   * @param encounterId Internal resource ID for the Encounter for which we want corresponding
   *                    medicationPrescriptions.
   * @return Search results.
   */
  @Search()
  public List<MedicationPrescription> searchByEncounterId(
      @RequiredParam(name = MedicationPrescription.SP_ENCOUNTER) StringParam encounterId) {
    Id<Encounter> encounterInternalId = Id.of(encounterId.getValue());
    List<MedicationPrescription> medicationPrescriptions = medicationPrescriptionRepository.
        list(encounterInternalId);

    return medicationPrescriptions;
  }
}
