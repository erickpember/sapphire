// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.common.persist.Id;
import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.LanguageCode;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

/**
 * A container for various test models.
 */
@Slf4j
public class TestModels {
  public static Date getDate() {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    try {
      return format.parse("2009-12-31 12:12:12 +0000");
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static URI getURI() {
    try {
      return new URI("test://testuri");
    } catch (URISyntaxException ex) {
      throw new RuntimeException("Unexpected invalid URI for test.", ex);
    }
  }

  public static Address address = new Address() {{
      setStreet("1234 Test Avenue");
      setCity("Test City");
      setStateProvince("Testlavania");
      setPostalCode("12345-6789");
      setUnit("F");
      setCountry(CountryCode.US);
      }};

  public static Name name = new Name() {{
      setFirst("Tester");
      setMiddle("Testard");
      setLast("Testington");
      }};

  public static Caregiver caregiver = new Caregiver() {{
      setAddress(address);
      setSpecialty(Specialty.Allergy);
      setName(name);
      setGender(Gender.Undifferentiated);
      setBirthDate(getDate());
      setPhoto(getURI());
      setOrganization("Test Corp.");
      }};

  public static Contact contact = new Contact() {{
      setAddress(address);
      setName(name);
      setGender(Gender.Undifferentiated);
      setBirthDate(getDate());
      setPhoto(getURI());
      setOrganization("Test Corp.");
      setRelationship("Tester");
      }};

  public static Patient patient = new Patient() {{
      setActive(true);
      setAddress(address);
      setBirthDate(getDate());
      setPhoto(getURI());
      setOrganization("Test Corp.");
      setName(name);
      setCareProvider(Arrays.asList(caregiver, caregiver));
      setContactDetails(Arrays.asList(contact, contact));
      setCreationDate(getDate());
      setDeceased(false);
      setId(Id.of("1234"));
      setLangs(Arrays.asList(LanguageCode.en, LanguageCode.ch));
      setManagingOrg("Test Corp.");
      setMaritalStatus(MaritalStatus.DomesticPartner);
      setRace(Race.Black);
    }};

  public static Period period = new Period() {{
      setStart(getDate());
      setStop(getDate());
      }};

  public static CodeableConcept codeable = new CodeableConcept() {{
      setCode("Codeable");
      setText("Concept");
      }};

  public static Location location = new Location() {{
      setLocation(getURI());
      setPeriod(period);
      }};

  public static Participant participant = new Participant() {{
      setRole(codeable);
      setIndividual(getURI());
      }};

  public static Attachment attachment = new Attachment() {{
      setContentType("UTF-8");
      setLanguage(LanguageCode.en);
      setData("test text".getBytes());
      setUrl(getURI());
      setTitle("test text");
      }};

  public static Quantity quantity = new Quantity() {{
      setValue(new BigDecimal(10));
      setComparator(QuantityComparator.GreaterThan);
      setUnits("seconds");
      setSystem(getURI());
      setCode(codeable);
      }};

  public static Ratio ratio = new Ratio() {{
      setNumerator(quantity);
      setDenominator(quantity);
      }};

  public static SampledData sampledData = new SampledData() {{ }};

  public static ObservationValue value = new ObservationValue() {{
      setQuantity(quantity);
      setCode(codeable);
      setAttachment(attachment);
      setRatio(ratio);
      setPeriod(period);
      setSampledData(sampledData);
      setText("A value");
      }};

  public static Range range = new Range() {{
      setLow(quantity);
      setHigh(quantity);
      }};

  public static ReferenceRange referenceRange = new ReferenceRange() {{
      setMeaning(codeable);
      setAge(range);
      }};

  public static ObservationValue observationValue = new ObservationValue() {{
      setQuantity(quantity);
      setCode(codeable);
      setAttachment(attachment);
      setRatio(ratio);
      setPeriod(period);
      setSampledData(sampledData);
      setText("An observation");
      }};

  public static ObservationRelated related = new ObservationRelated() {{
      setType(Arrays.asList(ObservationRelationshipType.DerivedFrom));
      setTarget(getURI());
      }};

  public static Observation observation = new Observation() {{
      setId(Id.of("1234"));
      setName(codeable);
      setValues(observationValue);
      setInterpretation(ObservationInterpretation.A);
      setComments("The patient is alive.");
      setApplies(period);
      setIssued(getDate());
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

  public static EncounterAccomodation accomodation = new EncounterAccomodation() {{
      setBed(getURI());
      setPeriod(period);
      }};

  public static Hospitalization hospitalization = new Hospitalization() {{
      setId(Id.of("1234"));
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
      setId(Id.of("1234"));
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
