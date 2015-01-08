// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.LanguageCode;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.testng.Assert.assertEquals;

/**
 * A container for various test models.
 */
public class TestModels {
  public static URI getURI(){
    try {
      return new URI("test://testuri");
    } catch (URISyntaxException ex) {
      Logger.getLogger(EncounterTest.class.getName()).log(Level.SEVERE, null, ex);
      return null; // The hardcoded value above validates.
    }
  }

  public static Address address = new Address(){{
    setStreet("1234 Test Avenue");
    setCity("Test City");
    setStateProvince("Testlavania");
    setPostalCode("12345-6789");
    setUnit("F");
    setCountry(CountryCode.US);
  }};

  public static Name name = new Name(){{
    setFirst("Tester");
    setMiddle("Testard");
    setLast("Testington");
  }};

  public static Caregiver caregiver = new Caregiver(){{
    setAddress(address);
    setSpecialty(Specialty.Allergy);
    setName(name);
    setGender(Gender.Undifferentiated);
    setBirthDate(new Date());
    setPhoto(getURI());
    setOrganization("Test Corp.");
  }};

  public static Contact contact = new Contact(){{
    setAddress(address);
    setName(name);
    setGender(Gender.Undifferentiated);
    setBirthDate(new Date());
    setPhoto(getURI());
    setOrganization("Test Corp.");
    setRelationship("Tester");
  }};

  public static Patient patient = new Patient(){{
    setActive(true);
    setAddress(address);
    setBirthDate(new Date());
    setPhoto(getURI());
    setOrganization("Test Corp.");
    setName(name);
    setCareProvider(Arrays.asList(caregiver, caregiver));
    setContactDetails(Arrays.asList(contact, contact));
    setCreationDate(new Date());
    setDeceased(false);
    setId(getURI());
    setLangs(Arrays.asList(LanguageCode.en, LanguageCode.ch));
    setManagingOrg("Test Corp.");
    setMaritalStatus(MaritalStatus.DomesticPartner);
    setRace(Race.Black);
  }};

  public static Period period = new Period(){{
    setStart(new Date());
    setStop(new Date());
  }};

  public static CodeableConcept codeable = new CodeableConcept(){{
    setCode("Codeable");
    setText("Concept");
  }};

  public static Location location = new Location(){{
    setLocation(getURI());
    setPeriod(period);
  }};

  public static Participant participant = new Participant(){{
    setRole(codeable);
    setIndividual(getURI());
  }};

  public static Attachment attachment = new Attachment(){{
    setContentType("UTF-8");
    setLanguage(LanguageCode.en);
    setData("test text".getBytes());
    setUrl(getURI());
    setTitle("test text");
  }};

  public static Quantity quantity = new Quantity(){{
    setValue(new BigDecimal(10));
    setComparator(QuantityComparator.GreaterThan);
    setUnits("seconds");
    setSystem(getURI());
    setCode(codeable);
  }};

  public static Ratio ratio = new Ratio(){{
    setNumerator(quantity);
    setDenominator(quantity);
  }};

  public static SampledData sampledData = new SampledData(){{
  }};

  public static ObservationValue value = new ObservationValue(){{
    setQuantity(quantity);
    setCode(codeable);
    setAttachment(attachment);
    setRatio(ratio);
    setPeriod(period);
    setSampledData(sampledData);
    setText("A value");
  }};

  public static Range range = new Range(){{
    setLow(quantity);
    setHigh(quantity);
  }};

  public static ReferenceRange referenceRange = new ReferenceRange(){{
    setMeaning(codeable);
    setAge(range);
  }};

  public static ObservationValue observationValue = new ObservationValue(){{
    setQuantity(quantity);
    setCode(codeable);
    setAttachment(attachment);
    setRatio(ratio);
    setPeriod(period);
    setSampledData(sampledData);
    setText("An observation");
  }};

  public static ObservationRelated related = new ObservationRelated(){{
    setType(Arrays.asList(ObservationRelationshipType.DerivedFrom));
    setTarget(getURI());
  }};

  public static Observation observation = new Observation(){{
    setId(getURI());
    setName(codeable);
    setValues(observationValue);
    setInterpretation(ObservationInterpretation.A);
    setComments("The patient is alive.");
    setApplies(period);
    setIssued(new Date());
    setStatus(ObservationStatus.Final);
    setReliability(ObservationReliability.Ok);
    setSite(codeable);
    setMethod(codeable);
    setIdentifier(codeable);
    setSubject(getURI());
    setSpecimen(getURI());
    setPerformer(getURI());
    setRange(Arrays.asList(referenceRange, referenceRange));
    setRelatedTo(Arrays.asList(related, related));
  }};

  public static EncounterAccomodation accomodation = new EncounterAccomodation(){{
    setBed(getURI());
    setPeriod(period);
  }};

  public static Hospitalization hospitalization = new Hospitalization(){{
    setId(getURI());
    setOrigin(getURI());
    setPeriod(period);
    setAccomodation(accomodation);
    setDiet(codeable);
    setSpecialCourtesy(codeable);
    setSpecialArrangement(codeable);
    setDestination(getURI());
    setDischargeDisposition(codeable);
    setDischargeDiagnosis(getURI());
    setReadmission(true);
  }};

  public static Encounter encounter = new Encounter() {{
    setId(getURI());
    setStatus(EncounterStatus.InProgress);
    setEclass(EncounterClass.Ambulatory);
    setType(EncounterType.OKI);
    setPeriod(period);
    setReason(codeable);
    setIndication(getURI());
    setPriority(EncounterPriority.SemiUrgent);
    setServiceProvider(getURI());
    setHospitalisation(hospitalization);
    setLocation(Arrays.asList(location, location));
    setParticipants(Arrays.asList(participant, participant));
    setLinkedTo(getURI());
    setObservations(Arrays.asList(observation, observation));
    setPatient(getURI());
  }};
}
