// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import com.datafascia.common.fhir.DependencyInjectingResourceProvider;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.MedicationAdministrationRepository;
import java.util.List;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MedicationAdministration resource endpoint
 */
@NoArgsConstructor @Slf4j
public class MedicationAdministrationResourceProvider extends DependencyInjectingResourceProvider {

  @Inject
  private MedicationAdministrationRepository medicationAdministrationRepository;

  @Override
  protected void onInjected() {
    log.info("medicationAdministrationRepository {}", medicationAdministrationRepository);
  }

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
    medicationAdministrationRepository.save(medicationAdministration);
    return new MethodOutcome(medicationAdministration.getId());
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
}
