// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
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
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.MedicationOrderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link MedicationOrder} resource provider
 */
@NoArgsConstructor @Slf4j
public class MedicationOrderResourceProvider implements IResourceProvider {

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private MedicationOrderRepository medicationOrderRepository;

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   *
   * @return Class of resource.
   */
  @Override
  public Class<MedicationOrder> getResourceType() {
    return MedicationOrder.class;
  }

  /**
   * Store a new MedicationOrder.
   *
   * @param medicationOrder The new MedicationOrder to store.
   * @return Outcome of create method. Resource ID of MedicationOrder.
   */
  @Create
  public MethodOutcome create(@ResourceParam MedicationOrder medicationOrder) {
    if (medicationOrder.getEncounter() != null) {
      medicationOrderRepository.save(medicationOrder);
      return new MethodOutcome(medicationOrder.getId());
    } else {
      throw new UnprocessableEntityException("Can not create MedicationOrder:"
          + " encounter reference can not be null.");
    }
  }

  /**
   * Because the MedicationOrderRepository does not support single-argument reads, a
   * double-argument read method that requires the Encounter ID as well as the Medication
   * Order ID is implemented here in the API as a search method.
   * Absent a medicationOrderId, all medication orders are searched.
   *
   * @param encounterId    Internal resource ID of the specific Encounter to search.
   * @param medicationOrderId Resource ID of the specific MedicationOrder we want to retrieve.
   * @return MedicationOrder list, matching this query.
   */
  @Search()
  public List<MedicationOrder> search(
      @RequiredParam(name = MedicationOrder.SP_ENCOUNTER) StringParam encounterId,
      @OptionalParam(name = MedicationOrder.SP_RES_ID) StringParam medicationOrderId) {

    List<MedicationOrder> medicationOrders = new ArrayList<>();

    if (medicationOrderId != null) {
      Id<Encounter> encounterInternalId = Id.of(encounterId.getValue());
      Id<MedicationOrder> orderInternalId = Id.of(medicationOrderId.getValue());
      Optional<MedicationOrder> result = medicationOrderRepository.read(
          encounterInternalId, orderInternalId);

      if (result.isPresent()) {
        medicationOrders.add(result.get());
      }
    } else {
      // Pull records for the encounter.
      medicationOrders.addAll(medicationOrderRepository.list(Id.of(encounterId.getValue())));
    }

    return medicationOrders;
  }

  /**
   * Completely replaces the content of the MedicationOrder resource with the content given
   * in the request.
   *
   * @param medicationOrder New MedicationOrder content.
   * @return Outcome of create method. Resource ID of MedicationOrder.
   */
  @Update
  public MethodOutcome update(@ResourceParam MedicationOrder medicationOrder) {
    IdDt resourceId = medicationOrder.getId();
    if (resourceId == null) {
      throw new UnprocessableEntityException("MedicationOrder: no ID supplied. "
          + "Can not update.");
    }

    checkForEncounterReference(medicationOrder);

    Id<Encounter> encounterId = Ids.toPrimaryKey(
        medicationOrder.getEncounter().
            getReference());

    // Check if entity already exists.
    Id<MedicationOrder> medicationPrescriptionId = MedicationOrderRepository.generateId(
        medicationOrder);
    Optional<MedicationOrder> optionalMedicationPrescription = medicationOrderRepository.read(
        encounterId, medicationPrescriptionId);
    if (!optionalMedicationPrescription.isPresent()) {
      throw new InvalidRequestException(String.format(
          "MedicationOrder ID [%s] did not already exist",
          medicationPrescriptionId));
    }

    medicationOrderRepository.save(medicationOrder);
    return new MethodOutcome(medicationOrder.getId());
  }

  private void checkForEncounterReference(MedicationOrder medicationOrder)
      throws UnprocessableEntityException {

    if (medicationOrder.getEncounter() == null || medicationOrder.getEncounter().isEmpty()) {
      throw new UnprocessableEntityException("MedicationPrescription with identifier "
          + medicationOrder.getIdentifierFirstRep().getValue()
          + " lacks the mandatory reference to encounter, can not be saved or updated.");
    }
  }
}
