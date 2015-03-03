// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist.opal;

import com.datafascia.models.Patient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Integration test for Patient DAO
 */
@Slf4j
public class PatientDaoIT extends DaoIT {
  /**
   * Test the presence and validity of patients.
   *
   * @throws Exception should not happen
   */
  @Test
  public void patients() throws Exception {
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");

    log.info("Getting the patients");
    PatientDao patientDao = new PatientDao(accumuloTemplate);
    Iterator<Patient> patients = patientDao.patients(true);

    int count = 0;
    while (patients.hasNext()) {
      Patient patient = patients.next();
      switch (patient.getId().toString()) {
        case "urn:df-patientId-1:96087004":
          validatePatient(patient, "ECMNOTES", null, "TEST",
              LocalDateTime.parse("1977-01-01T05:00:00Z", dateFormat).toLocalDate(),
              "urn:df-patientId-1:96087004", "urn:df-institution-patientId-1:UCSF::96087004");
          break;
        case "urn:df-patientId-1:96087039":
          validatePatient(patient, "ONE", "A", "ECM-MSSGE",
              LocalDateTime.parse("1960-06-06T04:00:00Z", dateFormat).toLocalDate(),
              "urn:df-patientId-1:96087039", "urn:df-institution-patientId-1:UCSF::96087039");
          break;
        case "urn:df-patientId-1:96087047":
          validatePatient(patient, "ONE", "B", "ECM-MSSGE",
              LocalDateTime.parse("1954-10-29T05:00:00Z", dateFormat).toLocalDate(),
              "urn:df-patientId-1:96087047", "urn:df-institution-patientId-1:UCSF:SICU:96087047");
          break;
        case "urn:df-patientId-1:96087055":
          validatePatient(patient, "ONE", "C", "ECM-MSSGE",
              LocalDateTime.parse("1996-07-29T04:00:00Z", dateFormat).toLocalDate(),
              "urn:df-patientId-1:96087055", "urn:df-institution-patientId-1:UCSF::96087055");
          break;
        case "urn:df-patientId-1:96087063":
          validatePatient(patient, "ONE", "D", "ECM-MSSGE",
              LocalDateTime.parse("1977-10-29T04:00:00Z", dateFormat).toLocalDate(),
              "urn:df-patientId-1:96087063", "urn:df-institution-patientId-1:UCSF::96087063");
          break;
        case "urn:df-patientId-1:97534012":
          validatePatient(patient, "ONEFIVE", "C", "MB-CHILD",
              LocalDateTime.parse("1999-02-20T05:00:00Z", dateFormat).toLocalDate(),
              "urn:df-patientId-1:97534012", "urn:df-institution-patientId-1:UCSF:SICU:97534012");
          break;
      }

      count++;
    }

    assertEquals(count, 703);
  }

  /**
   * Validates a patient object against various expected values.
   *
   * @param patient the patient object
   * @param firstName first name
   * @param middleName middle name
   * @param lastName last name
   * @param birthDate date of birth
   * @param patId patient identifier
   * @param instId institution identifier
   */
  public void validatePatient(Patient patient, String firstName, String middleName, String lastName,
      LocalDate birthDate, String patId, String instId) {
    assertEquals(patient.getName().getFirst(), firstName);
    assertEquals(patient.getName().getMiddle(), middleName);
    assertEquals(patient.getName().getLast(), lastName);
    assertEquals(patient.getBirthDate(), birthDate);
    assertEquals(patient.getId().toString(), patId);
    assertEquals(patient.getInstitutionPatientId(), instId);
  }
}
