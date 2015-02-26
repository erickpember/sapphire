// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.accumulo.AccumuloTemplate;
import com.datafascia.accumulo.RowMapper;
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
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.ColumnVisibility;
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
  private static final ColumnVisibility COLUMN_VISIBILITY = new ColumnVisibility("System");
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

  private BatchWriter writer;

  /**
   * Constructor
   *
   * @param accumuloTemplate
   *     data access operations template
   */
  @Inject
  public PatientRepository(AccumuloTemplate accumuloTemplate) {
    super(accumuloTemplate);

    writer = accumuloTemplate.createBatchWriter(Tables.PATIENT);
  }

  private static String toRowId(Id<Patient> patientId) {
    return toRowId(Patient.class, patientId);
  }

  private void putValue(Mutation mutation, String columnQualifier, Object value) {
    if (value != null) {
      mutation.put(COLUMN_FAMILY, columnQualifier, COLUMN_VISIBILITY, value.toString());
    }
  }

  private Mutation toMutation(Patient patient) {
    Mutation mutation = new Mutation(toRowId(patient.getId()));
    putValue(mutation, PATIENT_ID, patient.getPatientId());
    putValue(mutation, ACCOUNT_NUMBER, patient.getAccountNumber());
    putValue(mutation, FIRST_NAME, patient.getName().getFirst());
    putValue(mutation, MIDDLE_NAME, patient.getName().getMiddle());
    putValue(mutation, LAST_NAME, patient.getName().getLast());
    putValue(mutation, GENDER, patient.getGender().getCode());
    putValue(mutation, BIRTH_DATE, encode(patient.getBirthDate()));
    putValue(mutation, MARITAL_STATUS, patient.getMaritalStatus().getCode());
    putValue(mutation, RACE, patient.getRace().getCode());
    if (!patient.getLangs().isEmpty()) {
      putValue(mutation, LANGUAGE, patient.getLangs().get(0).name());
    }
    putValue(mutation, ACTIVE, String.valueOf(patient.isActive()));
    putValue(mutation, INSTITUTION_PATIENT_ID, patient.getInstitutionPatientId());
    return mutation;
  }

  /**
   * Saves entity.
   *
   * @param patient
   *     to save
   */
  public void save(Patient patient) {
    patient.setId(Id.of(patient.getInstitutionPatientId().toString()));
    try {
      writer.addMutation(toMutation(patient));
      writer.flush();
    } catch (MutationsRejectedException e) {
      throw new IllegalStateException("Cannot save patient ID " + patient.getId(), e);
    }
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
}
