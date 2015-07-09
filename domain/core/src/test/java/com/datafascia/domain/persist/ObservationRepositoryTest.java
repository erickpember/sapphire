// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.StringDt;
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
 * {@link ObservationRepository} test
 */
public class ObservationRepositoryTest extends RepositoryTestSupport {

  private static final String NUMERICAL_PAIN_LEVEL_LOW = "numericalPainLevelLow";
  private static final String NUMERICAL_PAIN_LEVEL_HIGH = "numericalPainLevelHigh";

  @Inject
  private PatientRepository patientRepository;

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private ObservationRepository observationRepository;

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

  private Observation createObservation(String code, String value) {
    StringDt observationValue = new StringDt();
    observationValue.setValue(value);

    Observation observation = new Observation();
    observation.setCode(new CodeableConceptDt("system", code));
    observation.setValue(observationValue);
    observation.setIssued(new Date(), TemporalPrecisionEnum.SECOND);
    return observation;
  }

  @Test
  public void should_list_observations() {
    UnitedStatesPatient patient = createPatient();
    patientRepository.save(patient);

    Encounter encounter = createEncounter(patient);
    encounterRepository.save(encounter);

    Observation observation1 = createObservation(NUMERICAL_PAIN_LEVEL_LOW, "1");
    observationRepository.save(encounter, observation1);

    Observation observation2 = createObservation(NUMERICAL_PAIN_LEVEL_HIGH, "2");
    observationRepository.save(encounter, observation2);

    Id<Encounter> encounterId = Ids.toPrimaryKey(encounter.getId());
    List<Observation> observations = observationRepository.list(encounterId);
    assertEquals(observations.size(), 2);
    for (Observation observation : observations) {
      switch (observation.getCode().getCodingFirstRep().getCode()) {
        case NUMERICAL_PAIN_LEVEL_LOW:
          assertEquals(observation.getId().getIdPart(), observation1.getId().getIdPart());
          break;
        case NUMERICAL_PAIN_LEVEL_HIGH:
          assertEquals(observation.getId().getIdPart(), observation2.getId().getIdPart());
          break;
        default:
          fail("unexpected observation code:" +
                  observation.getCode().getCodingFirstRep().getCode());
      }
    }
  }
}
