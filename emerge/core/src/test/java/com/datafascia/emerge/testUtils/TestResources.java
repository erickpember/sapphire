// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.testUtils;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.composite.RatioDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.MedicationOrderClient;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import java.time.Instant;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Contains one-off resource creation methods for the purpose of unit testing.
 */
public class TestResources {
  public static MedicationAdministration createMedicationAdministration(String id, String medsSet,
      MedicationAdministrationStatusEnum status, int dose, String unit,
      DateTimeDt effectiveTime, String orderId) {
    MedicationAdministration administration = new MedicationAdministration();
    administration.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_MEDICATION_ADMINISTRATION)
        .setValue(id);
    administration.addIdentifier().setSystem(CodingSystems.UCSF_MEDICATION_GROUP_NAME)
        .setValue(medsSet);
    administration.setStatus(status);
    administration.setPrescription(new ResourceReferenceDt(orderId));
    administration.setEffectiveTime(effectiveTime);
    MedicationAdministration.Dosage dosage = new MedicationAdministration.Dosage();
    dosage.setQuantity(new SimpleQuantityDt(dose, "", unit));
    dosage.setRate(new RatioDt());
    administration.setDosage(dosage);
    return administration;
  }

  public static MedicationOrder createMedicationOrder(String identifier,
      MedicationOrderStatusEnum status) {
    MedicationOrder prescription = new MedicationOrder();
    prescription.addIdentifier().setSystem(CodingSystems.UCSF_MEDICATION_GROUP_NAME)
        .setValue(identifier);
    prescription.addIdentifier().setSystem(IdentifierSystems.INSTITUTION_MEDICATION_ORDER)
        .setValue(identifier);
    prescription.setStatus(status);
    return prescription;
  }

  public static ClientBuilder createMockClient() {
    MedicationOrder active = createMedicationOrder("activeOrder", MedicationOrderStatusEnum.ACTIVE);
    MedicationOrder completed
        = createMedicationOrder("completedOrder", MedicationOrderStatusEnum.COMPLETED);
    ClientBuilder apiClient = mock(ClientBuilder.class);
    MedicationOrderClient orderClient = mock(MedicationOrderClient.class);
    when(orderClient.read("activeOrder", "encounterId")).thenReturn(active);
    when(orderClient.read("completedOrder", "encounterId")).thenReturn(completed);
    when(apiClient.getMedicationOrderClient()).thenReturn(orderClient);
    return apiClient;
  }

  public static Observation createObservation(String code, Double value, Instant time) {
    return createObservation(code, new QuantityDt(value), time);
  }

  public static Observation createObservation(String code, IDatatype value, Instant time) {
    DateTimeDt effectiveTime = new DateTimeDt(Date.from(time));
    Observation observation = new Observation()
        .setCode(new CodeableConceptDt("system", code))
        .setValue(value)
        .setIssued(new Date(), TemporalPrecisionEnum.SECOND)
        .setEffective(effectiveTime);
    observation.setId("obs" + code);
    return observation;
  }
}
