// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.Medication.Package;
import ca.uhn.fhir.model.dstu2.resource.Medication.Product;
import ca.uhn.fhir.model.dstu2.valueset.MedicationKindEnum;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.Ids;
import java.util.Optional;
import javax.inject.Inject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link MedicationRepository} test
 */
public class MedicationRepositoryTest extends RepositoryTestSupport {

  @Inject
  private MedicationRepository medicationRepository;

  private Medication createMedication() {
    Medication medication = new Medication();
    medication.setName("name");
    medication.setCode(new CodeableConceptDt("code", "code"));
    medication.setIsBrand(Boolean.TRUE);
    medication.setManufacturer(new ResourceReferenceDt("manufacturerId"));
    medication.setKind(MedicationKindEnum.PRODUCT);

    Product product = new Product();
    product.setForm(new CodeableConceptDt("formCode", "formCode"));
    medication.setProduct(product);

    Package aPackage = new Package();
    aPackage.setContainer(new CodeableConceptDt("containerCode", "containerCode"));
    medication.setPackage(aPackage);
    return medication;
  }

  @Test
  public void should_read_medication() {
    Medication medication = createMedication();
    medicationRepository.save(medication);

    Id<Medication> medicationId = Ids.toPrimaryKey(medication.getId());
    Optional<Medication> optionalMedication = medicationRepository.read(medicationId);
    assertEquals(optionalMedication.get().getId(), medication.getId());
  }
}
