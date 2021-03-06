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
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.MedicationOrderClient;
import com.datafascia.api.client.ObservationClient;
import com.datafascia.api.client.ProcedureRequestClient;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Setter;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Contains one-off resource creation methods for the purpose of unit testing.
 */
@Setter
public class TestResources {
  private static final List<Observation> observations = new ArrayList<>();
  private static final List<ProcedureRequest> procedureRequests = new ArrayList<>();

  public static MedicationAdministration createMedicationAdministration(String id,
      List<String> medsSets, MedicationAdministrationStatusEnum status, int dose, String unit,
      DateTimeDt effectiveTime, String orderId) {
    MedicationAdministration administration = new MedicationAdministration();
    administration.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_MEDICATION_ADMINISTRATION)
        .setValue(id);
    for (String medsSet : medsSets) {
      administration.addIdentifier().setSystem(CodingSystems.UCSF_MEDICATION_GROUP_NAME)
          .setValue(medsSet);
    }
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
    ObservationClient observationClient = mock(ObservationClient.class);
    when(observationClient.searchObservation(anyString(), anyString(), anyString())).thenReturn(
        observations);
    when(apiClient.getObservationClient()).thenReturn(observationClient);
    ProcedureRequestClient procedureRequestClient = mock(ProcedureRequestClient.class);
    when(procedureRequestClient.search(anyString(), anyString(), anyString())).thenReturn(
        procedureRequests);
    when(apiClient.getProcedureRequestClient()).thenReturn(procedureRequestClient);
    return apiClient;
  }

  public static Observation createObservation(String code, Double value, Instant time) {
    return createObservation(code, new QuantityDt(value), time);
  }

  public static Observation createObservation(String code, String value, Instant time) {
    return createObservation(code, new StringDt(value), time);
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
