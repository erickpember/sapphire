// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.MutationBuilder;
import com.datafascia.common.accumulo.MutationSetter;
import com.datafascia.common.accumulo.RowMapper;
import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.domain.model.CodeableConcept;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.MedicationAdministration;
import com.datafascia.domain.model.MedicationAdministrationDosage;
import com.datafascia.domain.model.MedicationAdministrationStatus;
import com.datafascia.domain.model.NumericQuantity;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.model.Ratio;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;

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

  private static final MedicationAdministrationRowMapper MEDICATION_ADMINISTRATION_ROW_MAPPER =
      new MedicationAdministrationRowMapper();
  private static final TypeReference<List<CodeableConcept>> CODEABLE_CONCEPT_LIST_TYPE =
      new TypeReference<List<CodeableConcept>>() { };

  private static class MedicationAdministrationRowMapper
      implements RowMapper<MedicationAdministration> {

    private Interval<Instant> effectiveTimePeriod;
    private MedicationAdministrationDosage dosage;
    private MedicationAdministration administration;

    @Override
    public void onBeginRow(Key key) {
      administration = new MedicationAdministration();
      administration.setId(Id.of(extractEntityId(key)));

      effectiveTimePeriod = new Interval<>();
      administration.setEffectiveTimePeriod(effectiveTimePeriod);

      dosage = new MedicationAdministrationDosage();
      dosage.setQuantity(new NumericQuantity());
      dosage.setRate(new Ratio(new NumericQuantity(), new NumericQuantity()));
      administration.setDosage(dosage);
    }

    @Override
    public void onReadEntry(Map.Entry<Key, Value> entry) {
      byte[] value = entry.getValue().get();
      switch (entry.getKey().getColumnQualifier().toString()) {
        case STATUS:
          administration.setStatus(MedicationAdministrationStatus.valueOf(decodeString(value)));
          break;
        case PATIENT_ID:
          administration.setPatientId(Id.of(decodeString(value)));
          break;
        case PRACTITIONER_ID:
          administration.setPractitionerId(Id.of(decodeString(value)));
          break;
        case ENCOUNTER_ID:
          administration.setEncounterId(Id.of(decodeString(value)));
          break;
        case PRESCRIPTION_ID:
          administration.setPrescriptionId(Id.of(decodeString(value)));
          break;
        case WAS_NOT_GIVEN:
          administration.setWasNotGiven(decodeBoolean(value));
          break;
        case REASONS_NOT_GIVEN:
          administration.setReasonsNotGiven(decode(value, CODEABLE_CONCEPT_LIST_TYPE));
          break;
        case REASONS_GIVEN:
          administration.setReasonsGiven(decode(value, CODEABLE_CONCEPT_LIST_TYPE));
          break;
        case EFFECTIVE_TIME_PERIOD_START:
          effectiveTimePeriod.setStartInclusive(decodeInstant(value));
          break;
        case EFFECTIVE_TIME_PERIOD_END:
          effectiveTimePeriod.setEndExclusive(decodeInstant(value));
          break;
        case MEDICATION_ID:
          administration.setMedicationId(Id.of(decodeString(value)));
          break;
        case DEVICE_ID:
          administration.setDeviceId(Id.of(decodeString(value)));
          break;
        case DOSAGE_TEXT:
          dosage.setText(decodeString(value));
          break;
        case DOSAGE_SITE_CODING_CODE:
          String siteCode = decodeString(value);
          dosage.setSite(new CodeableConcept(siteCode, siteCode));
          break;
        case DOSAGE_ROUTE_CODING_CODE:
          String routeCode = decodeString(value);
          dosage.setRoute(new CodeableConcept(routeCode, routeCode));
          break;
        case DOSAGE_METHOD_CODING_CODE:
          String methodCode = decodeString(value);
          dosage.setMethod(new CodeableConcept(methodCode, methodCode));
          break;
        case DOSAGE_QUANTITY_VALUE:
          dosage.getQuantity().setValue(decodeBigDecimal(value));
          break;
        case DOSAGE_QUANTITY_UNITS:
          dosage.getQuantity().setUnit(decodeUnit(value));
          break;
        case DOSAGE_RATE_NUMERATOR_VALUE:
          dosage.getRate().getNumerator().setValue(decodeBigDecimal(value));
          break;
        case DOSAGE_RATE_NUMERATOR_UNITS:
          dosage.getRate().getNumerator().setUnit(decodeUnit(value));
          break;
        case DOSAGE_RATE_DENOMINATOR_VALUE:
          dosage.getRate().getDenominator().setValue(decodeBigDecimal(value));
          break;
        case DOSAGE_RATE_DENOMINATOR_UNITS:
          dosage.getRate().getDenominator().setUnit(decodeUnit(value));
          break;
      }
    }

    @Override
    public MedicationAdministration onEndRow() {
      return administration;
    }
  }

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

  /**
   * Finds medication administrations for an encounter.
   *
   * @param patientId
   *     parent entity ID
   * @param encounterId
   *     encounter ID
   * @return medication administrations
   */
  public List<MedicationAdministration> list(Id<Patient> patientId, Id<Encounter> encounterId) {
    Scanner scanner = accumuloTemplate.createScanner(Tables.PATIENT);
    scanner.setRange(Range.prefix(EncounterRepository.toRowId(patientId, encounterId)));
    scanner.fetchColumnFamily(new Text(COLUMN_FAMILY));

    return accumuloTemplate.queryForList(scanner, MEDICATION_ADMINISTRATION_ROW_MAPPER);
  }
}
