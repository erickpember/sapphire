// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.composite.RatioDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.api.client.Observations;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.datafascia.emerge.ucsf.codes.ventilation.DailySpontaneousBreathingTrialContraindicatedEnum;
import com.datafascia.emerge.ucsf.codes.ventilation.DailySpontaneousBreathingTrialValueEnum;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link DailySpontaneousBreathingTrialImpl} test
 */
public class DailySpontaneousBreathingTrialImplTest extends DailySpontaneousBreathingTrialImpl {
  private int observationCount;
  private final String encounterId = "1";
  private List<MedicationAdministration> admins = new ArrayList<>();

  public DailySpontaneousBreathingTrialImplTest() {
  }

  /**
   * Test of getValue method, of class DailySpontaneousBreathingTrialImpl.
   */
  @Test
  public void testGetValue_3args() {
    Observation given = createObservation(ObservationCodeEnum.SPONTANEOUS_BREATHING_TRIAL.getCode(),
        "Yes");
    assertEquals(getValue(new Observations(Arrays.asList(given)), Instant.now(),
        encounterId), DailySpontaneousBreathingTrialValueEnum.GIVEN);

    Observation pressureSupport = createObservation(ObservationCodeEnum.PRESSURE_SUPPORT.getCode(),
        4);
    Observation peep = createObservation(ObservationCodeEnum.PEEP.getCode(), 4);
    Observation fio2 = createObservation(ObservationCodeEnum.FIO2.getCode(), 49);

    assertEquals(getValue(new Observations(Arrays
        .asList(pressureSupport, peep, fio2)), Instant.now(), encounterId),
        DailySpontaneousBreathingTrialValueEnum.GIVEN);

    fio2 = createObservation(ObservationCodeEnum.FIO2.getCode(), 51);
    assertEquals(getValue(new Observations(Arrays
        .asList(pressureSupport, peep, fio2)), Instant.now(), encounterId),
        DailySpontaneousBreathingTrialValueEnum.CONTRAINDICATED);

    Observation notGiven = createObservation(ObservationCodeEnum.SPONTANEOUS_BREATHING_TRIAL
        .getCode(), "No");
    assertEquals(getValue(new Observations(Arrays.asList(notGiven)), Instant.now(),
        encounterId), DailySpontaneousBreathingTrialValueEnum.NOT_GIVEN);

    Instant tooLongAgo = Instant.now().minus(26, ChronoUnit.HOURS);
    given.setEffective(new DateTimeDt(Date.from(tooLongAgo)));

    assertEquals(getValue(new Observations(Arrays.asList(given)), Instant.now(),
        encounterId), DailySpontaneousBreathingTrialValueEnum.NOT_GIVEN);
  }

  /**
   * Test of getContraindicatedReason method, of class DailySpontaneousBreathingTrialImpl.
   */
  @Test
  public void testGetContraindicatedReason_3args() {
    MedicationAdministration nmba = createMedicationAdministration(
        MedsSetEnum.INTERMITTENT_VECURONIUM_IV.getCode(),
        MedicationAdministrationStatusEnum.IN_PROGRESS, 100, "mg");

    admins = Arrays.asList(nmba);

    assertEquals(getContraindicatedReason(new Observations(new ArrayList<>()), Instant.now(),
        encounterId).get(), DailySpontaneousBreathingTrialContraindicatedEnum.RECEIVING_NMBA);

    admins = new ArrayList<>();

    Observation tof = createObservation(ObservationCodeEnum.TRAIN_OF_FOUR.getCode(),
        "1");

    assertEquals(getContraindicatedReason(new Observations(Arrays.asList(tof)), Instant.now(),
        encounterId).get(), DailySpontaneousBreathingTrialContraindicatedEnum.RECEIVING_NMBA);

    Observation tof2 = createObservation(ObservationCodeEnum.TRAIN_OF_FOUR.getCode(),
        "4");

    assertEquals(getContraindicatedReason(new Observations(Arrays.asList(tof, tof2)), Instant.now(),
        encounterId), Optional.empty());

    Observation fio2 = createObservation(ObservationCodeEnum.FIO2.getCode(), 51);

    assertEquals(getContraindicatedReason(new Observations(Arrays.asList(tof, tof2, fio2)), Instant
        .now(), encounterId).get(), DailySpontaneousBreathingTrialContraindicatedEnum.FIO2_OVER_50);
  }

  @Override
  protected List<MedicationAdministration> getAdminsForEncounter(String encounterId) {
    return admins;
  }

  private Observation createObservation(String code, String value) {
    return createObservation(code, new StringDt(value));
  }

  private Observation createObservation(String code, int value) {
    return createObservation(code, new QuantityDt(value));
  }

  private Observation createObservation(String code, IDatatype value) {
    DateTimeDt effectiveTime = new DateTimeDt(Date.from(Instant.now().minus(1,
        ChronoUnit.MINUTES)));
    Observation observation = new Observation()
        .setCode(new CodeableConceptDt("system", code))
        .setValue(value)
        .setIssued(new Date(), TemporalPrecisionEnum.SECOND)
        .setEffective(effectiveTime);
    observation.setId(this.getClass().getSimpleName() + code + ":" + observationCount++);
    return observation;
  }

  private MedicationAdministration createMedicationAdministration(String medsSet,
      MedicationAdministrationStatusEnum status, int dose, String unit) {

    MedicationAdministration administration = new MedicationAdministration();
    administration.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_MEDICATION_ADMINISTRATION)
        .setValue("whatever");
    administration.addIdentifier()
        .setSystem(CodingSystems.UCSF_MEDICATION_GROUP_NAME)
        .setValue(medsSet);
    administration.setStatus(status);
    administration.setEffectiveTime(new DateTimeDt(Date.from(Instant.now().minus(1,
        ChronoUnit.MINUTES))));
    MedicationAdministration.Dosage dosage = new MedicationAdministration.Dosage();
    dosage.setQuantity(new SimpleQuantityDt(dose, "", unit));
    dosage.setRate(new RatioDt());
    administration.setDosage(dosage);
    return administration;
  }
}
