// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dao;

import com.datafascia.models.Gender;
import com.datafascia.models.Name;
import com.datafascia.models.Patient;
import com.datafascia.models.Race;
import com.datafascia.urn.URNFactory;
import com.google.common.base.Enums;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;

import static com.datafascia.string.StringUtils.trimQuote;

/**
 * Patient data access to/from Accumulo. This class expects the Accumulo connector object to be
 * injected via Guice framework.
 *
 * The RowID for the patient object in opal_dF_data is composed of these items:
 *
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
 *
 *   1. The literal string "ObjectStore"
 *   2. The NULL character aka \x00
 *   3. The literal string "PatientVisitMap"
 *   4. The NULL character
 *   5. The GUID of the patient visit
 *
 * Using the above examples, the RowID for the accumulo query could look like:
 *
 * "ObjectStore\x00PatientVisitMap\x00UCSF | SICU | 7d136475-09d0-498c-95e8-26e21e99f789"
 *
 */
@Slf4j
public class PatientDao extends AbstractDao {
  private static final SimpleDateFormat dateOfBirthFormat = new SimpleDateFormat("yyyyMMdd");

  /**
   * @param connect the Accumulo connection
   *
   * @return the list of active patients
   */
  public static Iterator<Patient> patients(Connector connect) {
    ArrayList<Patient> patients = new ArrayList<Patient>();
    ArrayList<String> patientIds = getPatientIds(connect, "true");
    for (String patientId : patientIds) {
      Optional<Patient> patient = patient(connect, patientId, lastVisitId(connect, patientId));
      if (patient.isPresent()) {
        patients.add(patient.get());
      }
    }

    return patients.iterator();
  }

  /**
   * @param activeFlag the active flag setting to filter with
   *
   * @return the list of active patient identifiers in the unit
   */
  public static ArrayList<String> getPatientIds(Connector connect, String activeFlag) {
    log.debug("Getting list of active patient ids...");
    ArrayList<String> patientIds = new ArrayList<>();
    try {
      Scanner scan = connect.createScanner(OPAL_DF_DATA, auths);
      scan.fetchColumnFamily(new Text(PATIENT_PRESENT));

      Iterator<Entry<Key,Value>> iter = scan.iterator();
      while (iter.hasNext()) {
        Entry<Key,Value> e = iter.next();
        if (e.getValue().toString().equals(activeFlag)) {
          String str[] = e.getKey().getRow().toString().split(NUL);
          if (str[2] != null) {
            patientIds.add(str[2]);
          } else {
            log.error("Invalid row identifier: " + e.getKey().getRow().toString());
          }
        }
      }
    } catch (TableNotFoundException e) {
      log.error("Table not found: " + OPAL_DF_DATA);
    }

    return patientIds;
  }

  /**
   * @param connect the Accumulo connection object
   * @param patientId the patient identifier
   *
   * @return return the latest/last visit identifier for the patient
   */
  public static Optional<String> lastVisitId(Connector connect, String patientId) {
    log.debug("Fetching visit id for patient " + patientId);
    try {
      Scanner scan = connect.createScanner(OPAL_DF_DATA, auths);
      scan.setRange(Range.exact(PATIENT_OBJECT_KEY_PREFIX + patientId));
      scan.fetchColumnFamily(new Text(VISIT_ID));

      Iterator<Entry<Key,Value>> iter = scan.iterator();
      while (iter.hasNext()) {
        Entry<Key,Value> e = iter.next();

        return Optional.of(trimQuote(e.getValue().toString()));
      }
    } catch (TableNotFoundException e) {
      log.error("Table not found: " + OPAL_DF_DATA);
    }

    return Optional.empty();
  }

  /**
   * @param connect the Accumulo connection
   * @param patientId the patient identifier
   * @param visitId the visit map identifier
   *
   * @return the patient 
   */
  public static Optional<Patient> patient(Connector connect, String patientId, Optional<String>
      visitId) {
    if (!visitId.isPresent()) {
      return Optional.empty();
    }

    try {
      Scanner scan = connect.createScanner(OPAL_DF_DATA, auths);
      scan.setRange(Range.exact(VISIT_KEY_PREFIX + visitId.get()));
      Patient patient = new Patient();
      patient.setId(URNFactory.patientId(patientId));
      Name name = new Name();

      Iterator<Entry<Key,Value>> iter = scan.iterator();
      while (iter.hasNext()) {
        Entry<Key,Value> e = iter.next();
        Value value = e.getValue();

        String colfStr[] = e.getKey().getColumnFamily().toString().split(NUL);
        VisitMapColFamily colFam = Enums.getIfPresent(VisitMapColFamily.class,
            colfStr[1].trim()).or(VisitMapColFamily.df_IGNORE);
        switch (colFam) {
          case dF_pidGivenName :
            name.setFirst(trimQuote(value.toString()));
            break;
          case dF_pidFamilyName :
            name.setLast(trimQuote(value.toString()));
            break;
          case dF_pidMiddleInitialOrName :
            name.setMiddle(trimQuote(value.toString()));
            break;
          case dF_pidSex :
            Gender gender =
                Enums.getIfPresent(Gender.class, trimQuote(value.toString())).or(Gender.Unknown);
            patient.setGender(gender);
            break;
          case dF_pidRace :
            Race race =
                Enums.getIfPresent(Race.class, trimQuote(value.toString())).or(Race.Unknown);
            patient.setRace(race);
            break;
          case dF_pidPatientId :
            String fields[] = visitId.get().split("\\|");
            patient.setInstitutionPatientId(URNFactory.institutionPatientId(fields[0].trim(),
                fields[1].trim(), trimQuote(value.toString())));
            break;
          case dF_pidDateTimeOfBirth :
            patient.setBirthDate(dateOfBirthFormat.parse(trimQuote(value.toString())));
            break;
          default :
            break;
        }
      }
      patient.setName(name);

      return Optional.of(patient);
    } catch (TableNotFoundException | ParseException | URISyntaxException |
        UnsupportedEncodingException exp) {
      log.error("Error building patient object", exp);
    }

    return Optional.empty();
  }
}
