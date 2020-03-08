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

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.Ids;
import com.datafascia.domain.fhir.Languages;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.neovisionaries.i18n.LanguageCode;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * {@link ProcedureRequestRepository} test
 */
public class ProcedureRequestRepositoryTest extends RepositoryTestSupport {

  private static final String TYPE_CODE1 = "typeCode1";
  private static final String TYPE_CODE2 = "typeCode2";

  @Inject
  private PatientRepository patientRepository;

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private ProcedureRequestRepository procedurerequestRepository;

  private UnitedStatesPatient createPatient() {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT).setValue("UCSF-12345");
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT).setValue("12345");
    patient.addName()
        .addGiven("pat1firstname").addGiven("pat1middlename").addFamily("pat1lastname");
    patient.addCommunication()
        .setPreferred(true).setLanguage(Languages.createLanguage(LanguageCode.en));
    patient
        .setRace(RaceEnum.ASIAN)
        .setMaritalStatus(MaritalStatusCodesEnum.M)
        .setGender(AdministrativeGenderEnum.MALE)
        .setBirthDate(new DateDt(new Date()))
        .setActive(true);
    return patient;
  }

  private Encounter createEncounter(UnitedStatesPatient patient) {
    PeriodDt period = new PeriodDt();
    period.setStart(new Date(), TemporalPrecisionEnum.DAY);

    Encounter encounter = new Encounter();
    encounter.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("12345");
    encounter
        .setPeriod(period)
        .setPatient(new ResourceReferenceDt(patient.getId()));
    return encounter;
  }

  private ProcedureRequest createProcedureRequest(String typeCode, String bodySiteCode, String id,
      Encounter encounter) {
    ProcedureRequest procedurerequest = new ProcedureRequest()
        .setCode(new CodeableConceptDt(CodingSystems.PROCEDURE_REQUEST, typeCode))
        .setSubject(encounter.getPatient())
        .setOrderer(encounter.getPatient())
        .setPerformer(encounter.getPatient())
        .setOrderedOn(new DateTimeDt(new Date(), TemporalPrecisionEnum.SECOND))
        .setEncounter(new ResourceReferenceDt(encounter.getId()));
    procedurerequest.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PROCEDURE_REQUEST)
        .setValue(id);
    procedurerequest.addBodySite(
        new CodeableConceptDt(CodingSystems.BODY_SITE, bodySiteCode));
    return procedurerequest;
  }

  @Test
  public void should_list_procedurerequests() {
    UnitedStatesPatient patient = createPatient();
    patientRepository.save(patient);

    Encounter encounter = createEncounter(patient);
    encounterRepository.save(encounter);

    ProcedureRequest procedurerequest1 = createProcedureRequest(TYPE_CODE1, "1", "1", encounter);
    procedurerequestRepository.save(procedurerequest1);

    ProcedureRequest procedurerequest2 = createProcedureRequest(TYPE_CODE2, "2", "2", encounter);
    procedurerequestRepository.save(procedurerequest2);

    Id<Encounter> encounterId = Ids.toPrimaryKey(encounter.getId());
    List<ProcedureRequest> procedurerequests = procedurerequestRepository.list(encounterId);
    assertEquals(procedurerequests.size(), 2);
    for (ProcedureRequest procedurerequest : procedurerequests) {
      String typeCode = procedurerequest.getCode().getCodingFirstRep().getCode();
      switch (typeCode) {
        case TYPE_CODE1:
          assertEquals(procedurerequest.getId().getIdPart(), procedurerequest1.getId().getIdPart());
          break;
        case TYPE_CODE2:
          assertEquals(procedurerequest.getId().getIdPart(), procedurerequest2.getId().getIdPart());
          break;
        default:
          fail("unexpected procedurerequest type code " + typeCode);
      }
    }
  }
}
