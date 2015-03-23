// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.Limit;
import com.datafascia.common.accumulo.MutationBuilder;
import com.datafascia.common.accumulo.MutationSetter;
import com.datafascia.common.accumulo.RowMapper;
import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.domain.model.Gender;
import com.datafascia.domain.model.MaritalStatus;
import com.datafascia.domain.model.Name;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.model.Race;
import com.neovisionaries.i18n.LanguageCode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;

/**
 * Patient data access.
 * <p>
 * The row ID for a patient entity has the format:
 * <pre>
 * Patient={patientId}&
 * </pre>
 * The patient ID is URL encoded to escape delimiter characters appearing in it.
 */
@Slf4j
public class PatientRepository extends BaseRepository {

  private static final String COLUMN_FAMILY = Patient.class.getSimpleName();
  private static final String INSTITUTION_PATIENT_ID = "institutionPatientId";
  private static final String ACCOUNT_NUMBER = "accountNumber";
  private static final String FIRST_NAME = "firstName";
  private static final String MIDDLE_NAME = "middleName";
  private static final String LAST_NAME = "lastName";
  private static final String GENDER = "gender";
  private static final String BIRTH_DATE = "birthDate";
  private static final String MARITAL_STATUS = "maritalStatus";
  private static final String RACE = "race";
  private static final String LANGUAGES = "languages";
  private static final String LAST_ENCOUNTER_ID = "lastEncounterId";
  private static final String ACTIVE = "active";
  private static final PatientRowMapper PATIENT_ROW_MAPPER = new PatientRowMapper();

  /**
   * Constructor
   *
   * @param accumuloTemplate
   *     data access operations template
   */
  @Inject
  public PatientRepository(AccumuloTemplate accumuloTemplate) {
    super(accumuloTemplate);
  }

  /**
   * Converts patient ID to row ID.
   *
   * @param patientId the patient identifier
   * @return row ID
   */
  private static String toRowId(Id<Patient> patientId) {
    return toRowId(Patient.class, patientId);
  }

  /**
   * Generates primary key from institution patient ID.
   *
   * @param patient
   *     patient to read property from
   * @return primary key
   */
  public static Id<Patient> getPatientId(Patient patient) {
    return Id.of(URNFactory.urn(URNFactory.NS_PATIENT_ID, patient.getInstitutionPatientId()));
  }

  /**
   * Saves entity.
   *
   * @param patient
   *     to save
   */
  public void save(Patient patient) {
    patient.setId(getPatientId(patient));

    accumuloTemplate.save(
        Tables.PATIENT,
        toRowId(patient.getId()),
        new MutationSetter() {
          @Override
          public void putWriteOperations(MutationBuilder mutationBuilder) {
            mutationBuilder
                .columnFamily(COLUMN_FAMILY)
                .put(INSTITUTION_PATIENT_ID, patient.getInstitutionPatientId())
                .put(ACCOUNT_NUMBER, patient.getAccountNumber())
                .put(FIRST_NAME, patient.getName().getFirst())
                .put(MIDDLE_NAME, patient.getName().getMiddle())
                .put(LAST_NAME, patient.getName().getLast())
                .put(GENDER, patient.getGender().getCode())
                .put(BIRTH_DATE, encode(patient.getBirthDate()))
                .put(MARITAL_STATUS, patient.getMaritalStatus().getCode())
                .put(RACE, patient.getRace().getCode())
                .put(LANGUAGES, encode(patient.getLangs().stream()
                    .map(LanguageCode::name)
                    .collect(Collectors.toList())))
                .put(LAST_ENCOUNTER_ID, patient.getLastEncounterId())
                .put(ACTIVE, String.valueOf(patient.isActive()));
          }
        });
  }

  private static class PatientRowMapper implements RowMapper<Patient> {
    private Patient patient;

    @Override
    public void onBeginRow(Key key) {
      patient = new Patient();
      patient.setId(Id.of(extractEntityId(key)));
      patient.setName(new Name());
      patient.setGender(Gender.UNKNOWN);
      patient.setMaritalStatus(MaritalStatus.UNKNOWN);
      patient.setRace(Race.UNKNOWN);
      patient.setLangs(Collections.emptyList());
    }

    @Override
    public void onReadEntry(Map.Entry<Key, Value> entry) {
      String value = entry.getValue().toString();
      switch (entry.getKey().getColumnQualifier().toString()) {
        case INSTITUTION_PATIENT_ID:
          patient.setInstitutionPatientId(value);
          break;
        case ACCOUNT_NUMBER:
          patient.setAccountNumber(value);
          break;
        case FIRST_NAME:
          patient.getName().setFirst(value);
          break;
        case MIDDLE_NAME:
          patient.getName().setMiddle(value);
          break;
        case LAST_NAME:
          patient.getName().setLast(value);
          break;
        case GENDER:
          patient.setGender(Gender.of(value).orElse(Gender.UNKNOWN));
          break;
        case BIRTH_DATE:
          patient.setBirthDate(decodeLocalDate(value));
          break;
        case MARITAL_STATUS:
          patient.setMaritalStatus(MaritalStatus.of(value).orElse(MaritalStatus.UNKNOWN));
          break;
        case RACE:
          patient.setRace(Race.of(value).orElse(Race.UNKNOWN));
          break;
        case LANGUAGES:
          patient.setLangs(decodeStringList(value).stream()
              .map(LanguageCode::getByCode)
              .collect(Collectors.toList()));
          break;
        case LAST_ENCOUNTER_ID:
          patient.setLastEncounterId(Id.of(value));
          break;
        case ACTIVE:
          patient.setActive(Boolean.parseBoolean(value));
          break;
      }
    }

    @Override
    public Patient onEndRow() {
      return patient;
    }
  }

  /**
   * Reads patient.
   *
   * @param patientId
   *     patient ID
   * @return optional entity, empty if not found
   */
  public Optional<Patient> read(Id<Patient> patientId) {
    Scanner scanner = accumuloTemplate.createScanner(Tables.PATIENT);
    scanner.setRange(Range.exact(toRowId(patientId)));
    scanner.fetchColumnFamily(new Text(COLUMN_FAMILY));

    return accumuloTemplate.queryForObject(scanner, PATIENT_ROW_MAPPER);
  }

  /**
   * Finds patients.
   *
   * @param optStart
   *     if present, start the scan from this patient
   * @param optActive
   *     if present, the active state to match
   * @param count maximum number of items to return in list
   *
   * @return found patients
   */
  public List<Patient> list(Optional<String> optStart, Optional<Boolean> optActive, int count) {
    Scanner scanner = accumuloTemplate.createScanner(Tables.PATIENT);
    if (optStart.isPresent()) {
      scanner.setRange(new Range(new Text(toRowId(Id.of(optStart.get()))), null));
    }
    scanner.fetchColumnFamily(new Text(COLUMN_FAMILY));

    Limit<Patient> maxLim = new Limit<>(count);
    if (optActive.isPresent()) {
      return accumuloTemplate.queryForList(
          scanner, PATIENT_ROW_MAPPER, patient -> patient.isActive() == optActive.get(), maxLim);
    } else {
      return accumuloTemplate.queryForList(scanner, PATIENT_ROW_MAPPER, patient -> true, maxLim);
    }
  }
}
