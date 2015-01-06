// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.LanguageCode;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Patient model
 */
@Slf4j
public class PatientTest {
  ObjectMapper mapper;
  Patient testPatient = new Patient(){{
    setActive(true);
    setAddress(new Address(){{
      setStreet("1234 Test Avenue");
      setCity("Test City");
      setStateProvince("Testlavania");
      setPostalCode("12345-6789");
      setUnit("F");
      setCountry(CountryCode.US);
    }});
    setBirthDate(new Date());
    try {
      setPhoto(new URI("url://image"));
    } catch (URISyntaxException ex) {
      log.error("Invalid URL for photo", ex);
    }
    setOrganization("Test Corp.");
    setName(new Name(){{
      setFirst("Tester");
      setMiddle("Testard");
      setLast("Testington");
      setGender(Gender.Undifferentiated);
      setBirthDate(new Date());
      try {
        setPhoto(new URI("uri://image"));
      } catch (URISyntaxException ex) {
        log.error("Invalid URL for photo", ex);
      }
      setOrganization("Test Corp.");
    }});
    setCareProvider(Arrays.asList(new Caregiver(){{
      setAddress(new Address(){{
        setStreet("1234 Test Avenue");
        setCity("Test City");
        setStateProvince("Testlavania");
        setPostalCode("12345-6789");
        setUnit("F");
        setCountry(CountryCode.US);
      }});
      setSpecialty(Specialty.Allergy);
      setName(new Name(){{
        setFirst("Tester");
        setMiddle("Testard");
        setLast("Testington");
        setGender(Gender.Undifferentiated);
        setBirthDate(new Date());
        try {
          setPhoto(new URI("uri://image"));
        } catch (URISyntaxException ex) {
          log.error("Invalid URL for photo", ex);
        }
        setOrganization("Test Corp.");
      }});
    }}));
    setContactDetails(Arrays.asList(new Contact(){{
      setAddress(new Address(){{
        setStreet("1234 Test Avenue");
        setCity("Test City");
        setStateProvince("Testlavania");
        setPostalCode("12345-6789");
        setUnit("F");
        setCountry(CountryCode.US);
      }});
      setName(new Name(){{
        setFirst("Tester");
        setMiddle("Testard");
        setLast("Testington");
        setGender(Gender.Undifferentiated);
        setBirthDate(new Date());
        try {
          setPhoto(new URI("uri://image"));
        } catch (URISyntaxException ex) {
          log.error("Invalid URL for photo", ex);
        }
        setOrganization("Test Corp.");
      }});
      setRelationship("Tester");
    }}));
    setCreationDate(new Date());
    setDeceased(false);
    try {
      setId(new URI("uri://id"));
    } catch (URISyntaxException ex) {
      log.error("Invalid identifier", ex);
    }
    setLangs(Arrays.asList(LanguageCode.en, LanguageCode.ch));
    setManagingOrg("Test Corp.");
    setMaritalStatus(MaritalStatus.DomesticPartner);
    setRace(Race.Black);
  }};

  @BeforeMethod
  public void beforeTests() {
    mapper = new ObjectMapper();
  }

  @Test
  public void encodeDecodeTest() throws IOException, URISyntaxException {
    String jsonString = mapper.writeValueAsString(testPatient);
    Patient decodePatient = mapper.readValue(jsonString, Patient.class);
    assertEquals(testPatient.getId(), decodePatient.getId());
    assertEquals(testPatient.getAddress(), decodePatient.getAddress());
    assertEquals(testPatient.getBirthDate(), decodePatient.getBirthDate());
    assertEquals(testPatient.getCareProvider(), decodePatient.getCareProvider());
    assertEquals(testPatient.getContactDetails(), decodePatient.getContactDetails());
    assertEquals(testPatient.getCreationDate(), decodePatient.getCreationDate());
    assertEquals(testPatient.getLangs(), decodePatient.getLangs());
    assertEquals(testPatient.getManagingOrg(), decodePatient.getManagingOrg());
    assertEquals(testPatient.getMaritalStatus(), decodePatient.getMaritalStatus());
    assertEquals(testPatient.getRace(), decodePatient.getRace());
  }
}
