// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist.opal;

import com.datafascia.accumulo.AccumuloTemplate;
import com.datafascia.accumulo.RowMapper;
import com.datafascia.accumulo.SingleValueRowMapper;
import com.datafascia.common.persist.Id;
import com.datafascia.models.Gender;
import com.datafascia.models.Name;
import com.datafascia.models.Patient;
import com.datafascia.models.Race;
import com.datafascia.urn.URNFactory;
import com.google.common.base.Enums;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;

/**
 * Patient data access to/from Accumulo. This class expects the Accumulo connector object to be
 * injected via Guice framework.
 *
 * The RowID for the patient object in opal_dF_data is composed of these items:
 *   1. The literal string "ObjectStore"
 *   2. The NULL character aka \x00
 *   3. The literal string "PatientObject"
 *   4. The NULL character
 *   5. The patient token string e.g. "9752949"
 *
 * Using the above examples, the RowID for the accumulo query would look like:
 *
 * "ObjectStore\x00PatientObject\x009752949"
 *
 * The RowID for the patient visit map is composed of these items:
 *   1. The literal string "ObjectStore"
 *   2. The NULL character aka \x00
 *   3. The literal string "PatientVisitMap"
 *   4. The NULL character
 *   5. The GUID of the patient visit
 *
 * Using the above examples, the RowID for the accumulo query could look like:
 *
 * "ObjectStore\x00PatientVisitMap\x00UCSF | SICU | 7d136475-09d0-498c-95e8-26e21e99f789"
 */
@Slf4j
public class PatientDao extends OpalDao {
  /** patient present column family */
  private static final Text PATIENT_PRESENT = toColumnFamily(FieldType.BOOLEAN, "PatientPresent");

  /** last visit identifier column family */
  private static final Text LAST_VISIT_ID = toColumnFamily(FieldType.STRING, "LastVisitOiid");

  /**
   * Constructor
   *
   * @param accumuloTemplate
   *     data access operations template
   */
  @Inject
  public PatientDao(AccumuloTemplate accumuloTemplate) {
    super(accumuloTemplate);
  }

  /**
   *
   * @param active flag to indicate status of patients to return
   *
   * @return the list of active patients
   */
  public Iterator<Patient> patients(boolean active) {
    List<Patient> patients = new ArrayList<Patient>();

    List<String> patientIds = getPatientIds(active);
    for (String patientId : patientIds) {
      Optional<Patient> optionalPatient = patient(patientId, getLastVisitId(patientId));
      if (optionalPatient.isPresent()) {
        Patient patient = optionalPatient.get();
        patient.setActive(active);
        patients.add(patient);
      }
    }

    return patients.iterator();
  }

  @Slf4j
  private static class GetPatientIdsRowMapper implements RowMapper<String> {
    private final boolean activeFlag;
    private String patientId;
    private boolean patientPresent;

    GetPatientIdsRowMapper(boolean activeFlag) {
      this.activeFlag = activeFlag;
    }

    @Override
    public void onBeginRow(Key key) {
      String[] str = splitKey(key.getRow().toString());
      if (str.length == 3) {
        patientId = str[2];
      } else {
        log.warn("Invalid row identifier: " + key.getRow().toString());
      }

      patientPresent = false;
    }

    @Override
    public void onReadEntry(Entry<Key, Value> entry) {
      patientPresent = decodeBoolean(entry.getValue());
    }

    @Override
    public String onEndRow() {
      return (patientPresent == activeFlag) ? patientId : null;
    }
  }

  /**
   * @param activeFlag the active flag setting to filter with
   *
   * @return the list of active patient identifiers in the unit
   */
  public List<String> getPatientIds(boolean activeFlag) {
    Scanner scanner = getScanner();
    scanner.setRange(toRange(Kinds.PATIENT_OBJECT));
    scanner.fetchColumnFamily(PATIENT_PRESENT);

    return accumuloTemplate.queryForList(scanner, new GetPatientIdsRowMapper(activeFlag));
  }

  /**
   * Finds visit identifiers for the patient.
   *
   * @param patientId patient identifier
   * @return collection of visit identifiers, empty if none found
   */
  public List<String> findVisitIds(String patientId) {
    Optional<Value> value = getFieldValue(
        ObjectStore, Kinds.PATIENT_OBJECT, patientId, PatientFields.VISIT_OIIDS);
    if (value.isPresent()) {
      return Arrays.asList(decodeStringArray(value.get()));
    } else {
      return Collections.emptyList();
    }
  }

  private static class LastVisitIdRowMapper extends SingleValueRowMapper<String> {
    @Override
    public void onReadEntry(Entry<Key, Value> entry) {
      setValue(decodeString(entry.getValue()));
    }
  }

  /**
   * @param patientId the patient identifier
   *
   * @return return the latest/last visit identifier for the patient
   */
  public Optional<String> getLastVisitId(String patientId) {
    Scanner scanner = getScanner();
    scanner.setRange(toRange(Kinds.PATIENT_OBJECT, patientId));
    scanner.fetchColumnFamily(LAST_VISIT_ID);

    String lastVisitId = accumuloTemplate.queryForObject(scanner, new LastVisitIdRowMapper());
    return Optional.ofNullable(lastVisitId);
  }

  /**
   * @param patientId the patient identifier
   * @param visitId the visit map identifier
   * @return the patient
   */
  public Optional<Patient> patient(String patientId, Optional<String> visitId) {
    if (!visitId.isPresent()) {
      return Optional.empty();
    }

    Scanner scanner = getScanner();
    scanner.setRange(toRange(Kinds.PATIENT_VISIT_MAP, visitId.get()));

    Patient patient = new Patient();
    patient.setId(Id.of(patientId));
    patient.setGender(Gender.UNKNOWN);
    patient.setRace(Race.UNKNOWN);
    Name name = new Name();

    for (Entry<Key, Value> entry : scanner) {
      Value value = entry.getValue();

      String[] colfStr = splitKey(entry.getKey().getColumnFamily().toString());
      VisitMapColFamily colFam = Enums.getIfPresent(VisitMapColFamily.class,
          colfStr[1].trim()).or(VisitMapColFamily.df_IGNORE);
      try {
        switch (colFam) {
          case dF_pidGivenName :
            name.setFirst(decodeString(value));
            break;
          case dF_pidFamilyName :
            name.setLast(decodeString(value));
            break;
          case dF_pidMiddleInitialOrName :
            name.setMiddle(decodeString(value));
            break;
          case dF_pidSex :
            Gender gender = Gender.of(decodeString(value)).orElse(Gender.UNKNOWN);
            patient.setGender(gender);
            break;
          case dF_pidRace :
            Race race = Race.of(decodeString(value)).orElse(Race.UNKNOWN);
            patient.setRace(race);
            break;
          case dF_pidPatientId :
            String[] fields = visitId.get().split("\\|");
            patient.setInstitutionPatientId(URNFactory.institutionPatientId(fields[0].trim(),
                fields[1].trim(), decodeString(value)));
            break;
          case dF_pidDateTimeOfBirth :
            patient.setBirthDate(LocalDate.parse(decodeString(value),
                DateTimeFormatter.BASIC_ISO_DATE));
            break;
          default :
            break;
        }
        patient.setName(name);

        return Optional.of(patient);
      } catch (DateTimeParseException e) {
        log.error("Error building patient object", e);
      }
    }
    return Optional.empty();
  }
}