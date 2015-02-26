// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.common.persist.Id;
import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.LanguageCode;
import java.awt.Image;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import tec.units.ri.util.UCUM;

/**
 * A container for various test models.
 */
@Slf4j
public class TestModels {
  public static Instant getDateTime() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");
    LocalDateTime dateTime = LocalDateTime.parse("2009-12-31 12:12:12 +0000", formatter);
    return dateTime.toInstant(ZoneOffset.UTC);
  }

  public static Link<Image> getPhoto() {
    try {
      return Link.of("http://image.datafascia.com/image1", LinkRelation.Icon, "image/png", null);
    } catch (MalformedURLException ex) {
      throw new RuntimeException("Unexpected invalid URL for test.", ex);
    }
  }

  public static LocalDate getDate() {
    return LocalDate.of(2009, Month.DECEMBER, 31);
  }

  public static URI getURI() {
    try {
      return new URI("test://testuri");
    } catch (URISyntaxException ex) {
      throw new RuntimeException("Unexpected invalid URI for test.", ex);
    }
  }

  public static Address address = new Address() {
    {
      setStreet("1234 Test Avenue");
      setCity("Test City");
      setStateProvince("Testlavania");
      setPostalCode("12345-6789");
      setUnit("F");
      setCountry(CountryCode.US);
    }
  };

  public static Name name = new Name() {
    {
      setFirst("Tester");
      setMiddle("Testard");
      setLast("Testington");
    }
  };

  public static Caregiver caregiver = new Caregiver() {
    {
      setAddress(address);
      setSpecialty(Specialty.Allergy);
      setName(name);
      setGender(Gender.UNDIFFERENTIATED);
      setBirthDate(getDate());
      setPhoto(TestModels.getPhoto());
      setOrganization("Test Corp.");
    }
  };

  public static Contact contact = new Contact() {
    {
      setAddress(address);
      setName(name);
      setGender(Gender.UNDIFFERENTIATED);
      setBirthDate(getDate());
      setPhoto(TestModels.getPhoto());
      setOrganization("Test Corp.");
      setRelationship("Tester");
    }
  };

  public static Patient patient = new Patient() {
    {
      setActive(true);
      setAddress(address);
      setBirthDate(getDate());
      setPhoto(TestModels.getPhoto());
      setOrganization("Test Corp.");
      setName(name);
      setCareProvider(Arrays.asList(caregiver, caregiver));
      setContactDetails(Arrays.asList(contact, contact));
      setCreationDate(getDateTime());
      setDeceased(false);
      setId(Id.of("1234"));
      setLangs(Arrays.asList(LanguageCode.en, LanguageCode.ch));
      setManagingOrg("Test Corp.");
      setMaritalStatus(MaritalStatus.DOMESTIC_PARTNER);
      setRace(Race.BLACK);
    }
  };

  public static Period period = new Period() {
    {
      setStart(getDateTime());
      setStop(getDateTime());
    }
  };

  public static CodeableConcept codeable = new CodeableConcept() {
    {
      setCode("Codeable");
      setText("Concept");
    }
  };

  public static Location location = new Location() {
    {
      setLocation(getURI());
      setPeriod(period);
    }
  };

  public static Participant participant = new Participant() {
    {
      setRole(codeable);
      setIndividual(getURI());
    }
  };

  public static Attachment attachment = new Attachment() {
    {
      setContentType("UTF-8");
      setLanguage(LanguageCode.en);
      setData("test text".getBytes());
      setUrl(getURI());
      setTitle("test text");
    }
  };

  public static Ratio ratio = new Ratio() {
    {
      setNumerator(new NumericQuantity(new BigDecimal(1), UCUM.CELSIUS));
      setDenominator(new NumericQuantity(new BigDecimal(3), UCUM.CELSIUS));
    }
  };

  public static SampledData sampledData = new SampledData() {
    {
      ArrayList<BigDecimal> data = new ArrayList<>();
      data.add(new BigDecimal(8447));
      data.add(new BigDecimal(958737));
      data.add(new BigDecimal(38382672));
      data.add(new BigDecimal(.000001));
      data.add(new BigDecimal(-900000000));
      setData(data);
      setDimensions(9000l);
      setFactor(new BigDecimal(5));
      setLowerLimit(new BigDecimal(3.50));
      setOrigin(new NumericQuantity(new BigDecimal(3.1), UCUM.BTU));
      setPeriod(new BigDecimal(28));
      setUpperLimit(new BigDecimal(9001));
    }
  };

  public static ObservationValue value = new ObservationValue() {
    {
      setQuantity(numericQuantity);
      setCode(codeable);
      setAttachment(attachment);
      setRatio(ratio);
      setPeriod(period);
      setSampledData(sampledData);
      setText("A value");
    }
  };

  public static Range range = new Range() {
    {
      setLow(new NumericQuantity(new BigDecimal(0), UCUM.LITER));
      setHigh(new NumericQuantity(new BigDecimal(28935.78394d), UCUM.LITER));
    }
  };

  public static NumericQuantity numericQuantity = new NumericQuantity(new BigDecimal("3.1"), UCUM
      .METER);

  public static ReferenceRange referenceRange = new ReferenceRange() {
    {
      setMeaning(codeable);
      setAge(range);
    }
  };

  public static ObservationValue observationValue = new ObservationValue() {
    {
      setQuantity(numericQuantity);
      setCode(codeable);
      setAttachment(attachment);
      setRatio(ratio);
      setPeriod(period);
      setSampledData(sampledData);
      setText("An observation");
    }
  };

  public static ObservationRelated related = new ObservationRelated() {
    {
      setType(Arrays.asList(ObservationRelationshipType.DerivedFrom));
      setTarget(getURI());
    }
  };

  public static Observation observation = new Observation() {
    {
      setId(Id.of("1234"));
      setName(codeable);
      setValues(observationValue);
      setInterpretation(ObservationInterpretation.A);
      setComments("The patient is alive.");
      setApplies(period);
      setIssued(getDateTime());
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
    }
  };

  public static Person person = new Person() {
    {
      setName(name);
      setAddress(address);
      setGender(Gender.MALE);
      setBirthDate(getDate());
      setPhoto(TestModels.getPhoto());
      setOrganization("Some Ficticious Hospital");
    }
  };

  public static EncounterAccomodation accomodation = new EncounterAccomodation() {
    {
      setBed(getURI());
      setPeriod(period);
    }
  };

  public static Hospitalization hospitalization = new Hospitalization() {
    {
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
    }
  };

  public static Encounter encounter = new Encounter() {
    {
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
    }
  };

  private TestModels() {
  }
}
