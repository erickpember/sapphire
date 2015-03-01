// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.MutationBuilder;
import com.datafascia.common.accumulo.MutationSetter;
import com.datafascia.common.accumulo.RowMapper;
import com.datafascia.common.persist.Id;
import com.datafascia.models.Gender;
import com.datafascia.models.MaritalStatus;
import com.datafascia.models.Name;
import com.datafascia.models.Patient;
import com.datafascia.models.Race;
import com.neovisionaries.i18n.LanguageCode;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
 * Patient={primary key}&
 * </pre>
 * where the primary key has the format:
 * <pre>
 * urn:df-institution-patientId-1:{institution ID}:{facility ID}:{patient ID}
 * </pre>
 * The institution ID, facility ID and patient ID are URL encoded to escape
 * delimilter characters appearing in them.
 */
@Slf4j
public class PatientRepository extends BaseRepository {

  private static final String COLUMN_FAMILY = Patient.class.getSimpleName();
  private static final String PATIENT_ID = "patientId";
  private static final String ACCOUNT_NUMBER = "accountNumber";
  private static final String ACTIVE = "active";
  private static final String FIRST_NAME = "firstName";
  private static final String MIDDLE_NAME = "middleName";
  private static final String LAST_NAME = "lastName";
  private static final String GENDER = "gender";
  private static final String BIRTH_DATE = "birthDate";
  private static final String MARITAL_STATUS = "maritalStatus";
  private static final String RACE = "race";
  private static final String LANGUAGE = "language";
  private static final String INSTITUTION_PATIENT_ID = "institutionPatientId";
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

  private static String toRowId(Id<Patient> patientId) {
    return toRowId(Patient.class, patientId);
  }

  /**
   * Saves entity.
   *
   * @param patient
   *     to save
   */
  public void save(Patient patient) {
    accumuloTemplate.save(
        Tables.PATIENT,
        toRowId(patient.getId()),
        new MutationSetter() {
          @Override
          public void putWriteOperations(MutationBuilder mutationBuilder) {
            mutationBuilder
                .columnFamily(COLUMN_FAMILY)
                .put(PATIENT_ID, patient.getPatientId())
                .put(ACCOUNT_NUMBER, patient.getAccountNumber())
                .put(FIRST_NAME, patient.getName().getFirst())
                .put(MIDDLE_NAME, patient.getName().getMiddle())
                .put(LAST_NAME, patient.getName().getLast())
                .put(GENDER, patient.getGender().getCode())
                .put(BIRTH_DATE, encode(patient.getBirthDate()))
                .put(MARITAL_STATUS, patient.getMaritalStatus().getCode())
                .put(RACE, patient.getRace().getCode())
                .put(ACTIVE, String.valueOf(patient.isActive()))
                .put(INSTITUTION_PATIENT_ID, patient.getInstitutionPatientId());
            if (!patient.getLangs().isEmpty()) {
              mutationBuilder.put(LANGUAGE, patient.getLangs().get(0).name());
            }
          }
        });
  }

  private static class PatientRowMapper implements RowMapper<Patient> {
    private Patient patient;

    @Override
    public void onBeginRow(Key key) {
      patient = new Patient();
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
        case PATIENT_ID:
          patient.setPatientId(value);
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
        case LANGUAGE:
          patient.setLangs(Arrays.asList(LanguageCode.getByCode(value)));
          break;
        case ACTIVE:
          patient.setActive(Boolean.parseBoolean(value));
          break;
        case INSTITUTION_PATIENT_ID:
          patient.setInstitutionPatientId(URI.create(value));
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

    return Optional.ofNullable(accumuloTemplate.queryForObject(scanner, PATIENT_ROW_MAPPER));
  }

  /**
   * Finds patients.
   *
   * @param optionalActive
   *     if present, the active state to match
   * @return found patients
   */
  public List<Patient> list(Optional<Boolean> optionalActive) {
    Scanner scanner = accumuloTemplate.createScanner(Tables.PATIENT);
    scanner.fetchColumnFamily(new Text(COLUMN_FAMILY));

    if (optionalActive.isPresent()) {
      boolean active = optionalActive.get();
      return accumuloTemplate.queryForList(
          scanner, PATIENT_ROW_MAPPER, patient -> patient.isActive() == active);
    } else {
      return accumuloTemplate.queryForList(scanner, PATIENT_ROW_MAPPER);
    }
  }
}
