// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.persist.Id;
import com.datafascia.domain.model.CodeableConcept;
import com.datafascia.domain.model.Medication;
import com.datafascia.domain.model.MedicationPackage;
import com.datafascia.domain.model.Product;
import java.util.Arrays;
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
    medication.setCode(new CodeableConcept() {
      {
        setCodings(Arrays.asList("code"));
        setText("code");
      }
    });
    medication.setIsBrand(Boolean.TRUE);
    medication.setManufacturerId(Id.of("manufacturerId"));
    medication.setKind("kind");

    Product product = new Product();
    product.setForm(new CodeableConcept() {
      {
        setCodings(Arrays.asList("formCode"));
        setText("formCode");
      }
    });
    medication.setProduct(product);

    MedicationPackage aPackage = new MedicationPackage();
    aPackage.setContainer(new CodeableConcept() {
      {
        setCodings(Arrays.asList("containerCode"));
        setText("containerCode");
      }
    });
    medication.setPackage(aPackage);
    return medication;
  }

  @Test
  public void should_read_medication() {
    Medication medication = createMedication();
    medicationRepository.save(medication);

    Optional<Medication> optionalMedication = medicationRepository.read(medication.getId());
    assertEquals(optionalMedication.get(), medication);
  }
}
