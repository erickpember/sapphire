// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.datafascia.models.Patient;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Integration tests for patient resources
 */
@Slf4j
public class PatientIT extends ApiIT {
  /**
   * Fetch admitted patients and validate them.
   *
   * @throws Exception
   */
  @Test
  public void testPatient() throws Exception {
    List<Patient> patients = api.patients(true);
    for (Patient pat : patients) {
      String id = pat.getId().toString();
      switch (id) {
        case "urn:df-patientId-1:96087004":
          validatePatient(pat, "ECMNOTES", null, "TEST",
              dateFormat.parse("1977-01-01T05:00:00Z"), "urn:df-patientId-1:96087004",
              "urn:df-institution-patientId-1:UCSF::96087004");
          break;
        case "urn:df-patientId-1:96087039":
          validatePatient(pat, "ONE", "A", "ECM-MSSGE",
              dateFormat.parse("1960-06-06T04:00:00Z"), "urn:df-patientId-1:96087039",
              "urn:df-institution-patientId-1:UCSF::96087039");
          break;
        case "urn:df-patientId-1:96087047":
          validatePatient(pat, "ONE", "B", "ECM-MSSGE",
              dateFormat.parse("1954-10-29T05:00:00Z"), "urn:df-patientId-1:96087047",
              "urn:df-institution-patientId-1:UCSF:SICU:96087047");
          break;
        case "urn:df-patientId-1:96087055":
          validatePatient(pat, "ONE", "C", "ECM-MSSGE",
              dateFormat.parse("1996-07-29T04:00:00Z"), "urn:df-patientId-1:96087055",
              "urn:df-institution-patientId-1:UCSF::96087055");
          break;
        case "urn:df-patientId-1:96087063":
          validatePatient(pat, "ONE", "D", "ECM-MSSGE",
              dateFormat.parse("1977-10-29T04:00:00Z"), "urn:df-patientId-1:96087063",
              "urn:df-institution-patientId-1:UCSF::96087063");
          break;
        case "urn:df-patientId-1:97534012":
          validatePatient(pat, "ONEFIVE", "C", "MB-CHILD",
              dateFormat.parse("1999-02-20T05:00:00Z"), "urn:df-patientId-1:97534012",
              "urn:df-institution-patientId-1:UCSF:SICU:97534012");
          break;
      }
    }
  }

  /**
   * Validates a patient object against various expected values.
   */
  public void validatePatient(Patient patient, String firstName, String middleName,
      String lastName, Date birthDate, String patId, String instId) {
    assertEquals(patient.getName().getFirst(), firstName);
    assertEquals(patient.getName().getMiddle(), middleName);
    assertEquals(patient.getName().getLast(), lastName);
    assertEquals(patient.getBirthDate(), birthDate);
    assertEquals(patient.getId().toString(), patId);
    assertEquals(patient.getInstitutionPatientId().toString(), instId);
  }
}
