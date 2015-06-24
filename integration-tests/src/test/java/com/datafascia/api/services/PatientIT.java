// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

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

    int count = 0;

    // The new DSTU2 bundle is not being used, it could not be made to work.
    Bundle results = client.search()
            .forResource(UnitedStatesPatient.class)
            .where(UnitedStatesPatient.ACTIVE.exactly().code("true"))
            .execute();

    List<IResource> patients = extractBundle(results, UnitedStatesPatient.class);

    for (IResource resource : patients) {
      UnitedStatesPatient pat = (UnitedStatesPatient) resource;
      String id = pat.getId().getIdPart();
      switch (id) {
        case "urn:df-institution-patientId-1:UCSF::96087004":
          validatePatient(pat, "ECMNOTES", null, "TEST", new DateDt("1977-01-01"),
                  "urn:df-patientId-1:96087004", "urn:df-institution-patientId-1:UCSF::96087004");
          break;
        case "urn:df-institution-patientId-1:UCSF::96087039":
          validatePatient(pat, "ONE", "A", "ECM-MSSGE", new DateDt("1960-06-06"),
                  "urn:df-patientId-1:96087039", "urn:df-institution-patientId-1:UCSF::96087039");
          break;
        case "urn:df-institution-patientId-1:UCSF:SICU:96087047":
          validatePatient(pat, "ONE", "B", "ECM-MSSGE", new DateDt("1954-10-29"),
                  "urn:df-patientId-1:96087047",
                  "urn:df-institution-patientId-1:UCSF:SICU:96087047");
          break;
        case "urn:df-institution-patientId-1:UCSF::96087055":
          validatePatient(pat, "ONE", "C", "ECM-MSSGE", new DateDt("1996-07-29"),
                  "urn:df-patientId-1:96087055", "urn:df-institution-patientId-1:UCSF::96087055");
          break;
        case "urn:df-institution-patientId-1:UCSF::96087063":
          validatePatient(pat, "ONE", "D", "ECM-MSSGE", new DateDt("1977-10-29"),
                  "urn:df-patientId-1:96087063", "urn:df-institution-patientId-1:UCSF::96087063");
          break;
        case "urn:df-institution-patientId-1:UCSF:SICU:97534012":
          validatePatient(pat, "ONEFIVE", "C", "MB-CHILD",
                  new DateDt("1999-02-20"), "urn:df-patientId-1:97534012",
                  "urn:df-institution-patientId-1:UCSF:SICU:97534012");
          break;
        default:
          fail("Did not recognize ID: " + id);
          break;
      }
      count++;
    }
    assertEquals(count, 6, "testPatient did not find its expected patients!");

    // test read
    UnitedStatesPatient patient = client.read()
            .resource(UnitedStatesPatient.class)
            .withId("urn:df-institution-patientId-1:UCSF::96087004")
            .execute();
    assertEquals(patient.getId().getIdPart(), "urn:df-institution-patientId-1:UCSF::96087004");
    assertEquals(patient.getGenderElement().getValueAsEnum(), AdministrativeGenderEnum.FEMALE);

    // test update
    patient.setGender(AdministrativeGenderEnum.MALE);
    MethodOutcome updateResults = client.update()
            .resource(patient)
            .execute();
    assertEquals(updateResults.getId().getIdPart(),
            "urn:df-institution-patientId-1:UCSF::96087004");
    patient = client.read()
            .resource(UnitedStatesPatient.class)
            .withId("urn:df-institution-patientId-1:UCSF::96087004")
            .execute();
    assertEquals(patient.getGenderElement().getValueAsEnum(), AdministrativeGenderEnum.MALE);

    client.delete().resourceById(patient.getId()).execute();
    patient = client.read()
            .resource(UnitedStatesPatient.class)
            .withId("urn:df-institution-patientId-1:UCSF::96087004")
            .execute();
    assertFalse(patient.getActive(), "Patient was not deleted by being set to inactive.");
  }

  /**
   * Extracts a bundle from a web API query
   *
   * @param bundle A result from the web API query.
   * @param clazz Type of resource we're extracting.
   * @return A list of resources that were in the bundle.
   */
  public List<IResource> extractBundle(Bundle bundle, Class clazz) {
    List<IResource> resources = bundle.getResources(clazz);

    while (bundle.getLinkNext().isEmpty() == false) {
      bundle = client.loadPage().next(bundle).execute();
      resources.addAll(bundle.getResources(clazz));
    }
    return resources;
  }

  /**
   * Validates a patient object against various expected values.
   *
   * @param patient Patient
   * @param firstName Patient first name
   * @param birthDate Patient birth date, without hhmmss time.
   * @param lastName Patient last name
   * @param middleName Patient's second first name.
   * @param patId Patient id.
   * @param instId External patient id.
   */
  public void validatePatient(UnitedStatesPatient patient, String firstName, String middleName,
          String lastName, DateDt birthDate, String patId, String instId) {
    assertEquals(patient.getName().get(0).getGiven().get(0).toString(), firstName);
    if (patient.getName().get(0).getGiven().size() <= 1) {
      assertNull(middleName, "patient middle name is null, should be" + middleName);
    } else {
      assertEquals(patient.getName().get(0).getGiven().get(1).toString(), middleName);
    }
    assertEquals(patient.getName().get(0).getFamily().get(0).toString(), lastName);
    assertEquals(patient.getBirthDate().getTime(), birthDate.getValue().getTime());
    assertEquals(patient.getId().getIdPart(), instId);
  }
}
