// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.FlagStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import com.datafascia.common.persist.Id;
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
 * {@link FlagRepository} test
 */
public class FlagRepositoryTest extends RepositoryTestSupport {

  private static final String ADVANCE_DIRECTIVE = "AD";
  private static final String PHYSICIAN_ORDERS_FOR_LIFE_SUSTAINING_TREATMENT = "POLST";

  @Inject
  private PatientRepository patientRepository;

  @Inject
  private FlagRepository flagRepository;

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

  private Flag createFlag(String code, UnitedStatesPatient patient) {
    PeriodDt period = new PeriodDt();
    period.setStart(new Date(), TemporalPrecisionEnum.DAY);

    Flag flag = new Flag()
        .setStatus(FlagStatusEnum.ACTIVE)
        .setPeriod(period)
        .setCode(new CodeableConceptDt(code, code))
        .setPatient(new ResourceReferenceDt(patient));
    return flag;
  }

  @Test
  public void should_list_flags() {
    UnitedStatesPatient patient = createPatient();
    patientRepository.save(patient);

    Flag flag1 = createFlag(ADVANCE_DIRECTIVE, patient);
    flagRepository.save(flag1);

    Flag flag2 = createFlag(PHYSICIAN_ORDERS_FOR_LIFE_SUSTAINING_TREATMENT, patient);
    flagRepository.save(flag2);

    Id<UnitedStatesPatient> patientId = Ids.toPrimaryKey(patient.getId());
    List<Flag> flags = flagRepository.list(patientId);
    assertEquals(flags.size(), 2);
    for (Flag flag : flags) {
      switch (flag.getCode().getCodingFirstRep().getCode()) {
        case "AD":
          assertEquals(flag.getId().getIdPart(), flag1.getId().getIdPart());
          break;
        case "POLST":
          assertEquals(flag.getId().getIdPart(), flag2.getId().getIdPart());
          break;
        default:
          fail("unexpected flag code:" + flag.getCode().getCodingFirstRep().getCode());
      }
    }
  }
}
