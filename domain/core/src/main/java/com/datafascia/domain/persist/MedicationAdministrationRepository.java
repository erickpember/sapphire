// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.MutationBuilder;
import com.datafascia.common.accumulo.MutationSetter;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.MedicationAdministration;
import com.datafascia.domain.model.MedicationAdministrationDosage;
import com.datafascia.domain.model.Patient;
import java.util.UUID;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Medication administration data access.
 * <p>
 * The row ID for an medication administration entity has the format:
 * <pre>
 * Patient={patientId}&Encounter={encounterId}&MedicationAdministration={administrationId}&
 * </pre>
 */
@Slf4j
public class MedicationAdministrationRepository extends BaseRepository {

  private static final String COLUMN_FAMILY = MedicationAdministration.class.getSimpleName();
  private static final String STATUS = "status";
  private static final String PATIENT_ID = "patientId";
  private static final String PRACTITIONER_ID = "practitionerId";
  private static final String ENCOUNTER_ID = "encounterId";
  private static final String PRESCRIPTION_ID = "prescriptionId";
  private static final String WAS_NOT_GIVEN = "wasNotGiven";
  private static final String REASONS_NOT_GIVEN = "reasonsNotGiven";
  private static final String REASONS_GIVEN = "reasonsGiven";
  private static final String EFFECTIVE_TIME_PERIOD_START = "effectiveTimePeriod.start";
  private static final String EFFECTIVE_TIME_PERIOD_END = "effectiveTimePeriod.end";
  private static final String MEDICATION_ID = "medicationId";
  private static final String DEVICE_ID = "deviceId";
  private static final String DOSAGE_TEXT = "dosage.text";
  private static final String DOSAGE_SITE_CODING_CODE = "dosage.site.coding.code";
  private static final String DOSAGE_ROUTE_CODING_CODE = "dosage.route.coding.code";
  private static final String DOSAGE_METHOD_CODING_CODE = "dosage.method.coding.code";
  private static final String DOSAGE_QUANTITY_VALUE = "dosage.quantity.value";
  private static final String DOSAGE_QUANTITY_UNITS = "dosage.quantity.units";
  private static final String DOSAGE_RATE_NUMERATOR_VALUE = "dosage.rate.numerator.value";
  private static final String DOSAGE_RATE_NUMERATOR_UNITS = "dosage.rate.numerator.units";
  private static final String DOSAGE_RATE_DENOMINATOR_VALUE = "dosage.rate.denominator.value";
  private static final String DOSAGE_RATE_DENOMINATOR_UNITS = "dosage.rate.denominator.units";

  /**
   * Constructor
   *
   * @param accumuloTemplate
   *     data access operations template
   */
  @Inject
  public MedicationAdministrationRepository(AccumuloTemplate accumuloTemplate) {
    super(accumuloTemplate);
  }

  private static String toRowId(
      Id<Patient> patientId,
      Id<Encounter> encounterId,
      Id<MedicationAdministration> administrationId) {

    return EncounterRepository.toRowId(patientId, encounterId) +
        toRowId(MedicationAdministration.class, administrationId);
  }

  private static Id<MedicationAdministration> getEntityId(MedicationAdministration administration) {
    return (administration.getId() != null)
        ? administration.getId()
        : Id.of(UUID.randomUUID().toString());
  }

  /**
   * Saves entity.
   *
   * @param patient
   *     parent entity
   * @param encounter
   *     parent entity
   * @param administration
   *     to save
   */
  public void save(Patient patient, Encounter encounter, MedicationAdministration administration) {
    administration.setId(getEntityId(administration));

    accumuloTemplate.save(
        Tables.PATIENT,
        toRowId(patient.getId(), encounter.getId(), administration.getId()),
        new MutationSetter() {
          @Override
          public void putWriteOperations(MutationBuilder mutationBuilder) {
            mutationBuilder.columnFamily(COLUMN_FAMILY)
                .put(STATUS, administration.getStatus())
                .put(PATIENT_ID, administration.getPatientId())
                .put(PRACTITIONER_ID, administration.getPractitionerId())
                .put(ENCOUNTER_ID, administration.getEncounterId())
                .put(PRESCRIPTION_ID, administration.getPrescriptionId())
                .put(WAS_NOT_GIVEN, administration.getWasNotGiven())
                .put(REASONS_NOT_GIVEN, administration.getReasonsNotGiven())
                .put(REASONS_GIVEN, administration.getReasonsGiven())
                .put(
                    EFFECTIVE_TIME_PERIOD_START,
                    administration.getEffectiveTimePeriod().getStartInclusive())
                .put(
                    EFFECTIVE_TIME_PERIOD_END,
                    administration.getEffectiveTimePeriod().getEndExclusive())
                .put(MEDICATION_ID, administration.getMedicationId())
                .put(DEVICE_ID, administration.getDeviceId());

            MedicationAdministrationDosage dosage = administration.getDosage();
            if (dosage != null) {
              mutationBuilder
                  .put(DOSAGE_TEXT, dosage.getText())
                  .put(DOSAGE_SITE_CODING_CODE, dosage.getSite().getCode())
                  .put(DOSAGE_ROUTE_CODING_CODE, dosage.getRoute().getCode())
                  .put(DOSAGE_METHOD_CODING_CODE, dosage.getMethod().getCode())
                  .put(DOSAGE_QUANTITY_VALUE, dosage.getQuantity().getValue())
                  .put(DOSAGE_QUANTITY_UNITS, dosage.getQuantity().getUnit())
                  .put(DOSAGE_RATE_NUMERATOR_VALUE, dosage.getRate().getNumerator().getValue())
                  .put(DOSAGE_RATE_NUMERATOR_UNITS, dosage.getRate().getNumerator().getUnit())
                  .put(DOSAGE_RATE_DENOMINATOR_VALUE, dosage.getRate().getDenominator().getValue())
                  .put(DOSAGE_RATE_DENOMINATOR_UNITS, dosage.getRate().getDenominator().getUnit());
            }
          }
        });
  }
}
