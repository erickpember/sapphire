// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.Medication.Package;
import ca.uhn.fhir.model.dstu2.resource.Medication.Product;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.Ids;
import java.util.List;
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

  private Medication createMedication(String code) {
    Medication medication = new Medication();
    medication.setCode(new CodeableConceptDt(code, code).setText("name"));
    medication.setIsBrand(Boolean.TRUE);
    medication.setManufacturer(new ResourceReferenceDt("manufacturerId"));

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
    Medication medication = createMedication("code");
    medicationRepository.save(medication);

    Id<Medication> medicationId = Ids.toPrimaryKey(medication.getId());
    Optional<Medication> optionalMedication = medicationRepository.read(medicationId);
    assertEquals(optionalMedication.get().getId(), medication.getId());

    Medication medication2 = createMedication("code2");
    medicationRepository.save(medication2);

    List<Medication> medications = medicationRepository.list();
    assertEquals(medications.size(), 2);
  }
}
