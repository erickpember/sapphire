// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
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
 * {@link ProcedureRepository} test
 */
public class ProcedureRepositoryTest extends RepositoryTestSupport {

  private static final String TYPE_CODE1 = "typeCode1";
  private static final String TYPE_CODE2 = "typeCode2";

  @Inject
  private PatientRepository patientRepository;

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private ProcedureRepository procedureRepository;

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

  private Procedure createProcedure(String typeCode, String bodySiteCode, Encounter encounter) {
    Procedure procedure = new Procedure()
        .setCode(new CodeableConceptDt(CodingSystems.PROCEDURE, typeCode))
        .setPerformed(new DateTimeDt(new Date(), TemporalPrecisionEnum.SECOND))
        .setEncounter(new ResourceReferenceDt(encounter.getId()));
    procedure.addBodySite(
        new CodeableConceptDt(CodingSystems.BODY_SITE, bodySiteCode));
    return procedure;
  }

  @Test
  public void should_list_procedures() {
    UnitedStatesPatient patient = createPatient();
    patientRepository.save(patient);

    Encounter encounter = createEncounter(patient);
    encounterRepository.save(encounter);

    Procedure procedure1 = createProcedure(TYPE_CODE1, "1", encounter);
    procedureRepository.save(procedure1);

    Procedure procedure2 = createProcedure(TYPE_CODE2, "2", encounter);
    procedureRepository.save(procedure2);

    Id<Encounter> encounterId = Ids.toPrimaryKey(encounter.getId());
    List<Procedure> procedures = procedureRepository.list(encounterId);
    assertEquals(procedures.size(), 2);
    for (Procedure procedure : procedures) {
      String typeCode = procedure.getCode().getCodingFirstRep().getCode();
      switch (typeCode) {
        case TYPE_CODE1:
          assertEquals(procedure.getId().getIdPart(), procedure1.getId().getIdPart());
          break;
        case TYPE_CODE2:
          assertEquals(procedure.getId().getIdPart(), procedure2.getId().getIdPart());
          break;
        default:
          fail("unexpected procedure type code " + typeCode);
      }
    }
  }
}
