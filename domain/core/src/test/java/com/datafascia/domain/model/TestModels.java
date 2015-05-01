// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.LanguageCode;
import java.awt.Image;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

  public static CodeableConcept codeable = new CodeableConcept() {
    {
      setCode("Codeable");
      setText("Concept");
    }
  };

  public static RelatedPerson contact = new RelatedPerson() {
    {
      setAddress(address);
      setName(name);
      setGender(Gender.UNDIFFERENTIATED);
      setBirthDate(getDate());
      setPhoto(TestModels.getPhoto());
      setRelationship(codeable);
    }
  };

  public static Patient patient = new Patient() {
    {
      setActive(true);
      setAddress(address);
      setBirthDate(getDate());
      setPhoto(TestModels.getPhoto());
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

  public static Interval<Instant> period = new Interval<Instant>() {
    {
      setStartInclusive(getDateTime());
      setEndExclusive(getDateTime());
    }
  };

  public static ContactPoint contactPoint = new ContactPoint() {
    {
      setSystem(ContactPointSystem.EMAIL);
      setValue("zootsidenticaltwinsisterdingo@anthrax.castle");
      setUse(ContactPointUse.WORK);
      setPeriod(period);
    }
  };

  public static Position position = new Position() {
    {
      setLatitude(new BigDecimal(56.185158));
      setLongitude(new BigDecimal(-4.050253));
      setAltitude(new BigDecimal(109));
    }
  };

  public static Organization organization = new Organization() {
    {
      setId(Id.of("id"));
      setName("Tautology Club");
      setType(codeable);
      setTelecoms(Arrays.asList(contactPoint));
      setAddresses(Arrays.asList(address));
      setPartOfId(Id.of("partOf"));
      setLocationIds(Arrays.asList(Id.of("location")));
      setActive(true);
    }
  };

  public static Location location = new Location() {
    {
      setId(Id.of("1234"));
      setName("Castle Anthrax");
      setDescription("Look for a grail-shaped beacon");
      setMode(LocationMode.INSTANCE);
      setType(codeable);
      setTelecoms(Arrays.asList(contactPoint));
      setAddress(address);
      setPhysicalType(codeable);
      setPosition(position);
      setManagingOrganizationId(Id.of("organization"));
      setPartOfId(Id.of("partOf"));
      setStatus(LocationStatus.INACTIVE);
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

  public static NumericQuantity numericQuantity
      = new NumericQuantity(new BigDecimal("3.1"), UCUM.METER);

  public static Interval<NumericQuantity> numericInterval = new Interval<>(numericQuantity,
      numericQuantity);

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

  public static BodySite bodySite = new BodySite() {
    {
      setId(Id.of("1234"));
      setPatientId(Id.of("5678"));
      setCode(codeable);
      setModifier(codeable);
      setImages(Arrays.asList(getPhoto()));
    }
  };

  public static BooleanOrCodeableConcept booleanOrCodeableConcept = new BooleanOrCodeableConcept() {
    {
      setBool(false);
      setCode(codeable);
    }
  };

  public static Range range = new Range() {
    {
      setLow(numericQuantity);
      setHigh(numericQuantity);
    }
  };

  public static ConditionOnset conditionOnset = new ConditionOnset() {
    {
      setOnsetDateTime(getDateTime());
      setOnsetAge(numericQuantity);
      setOnsetPeriod(period);
      setOnsetRange(range);
      setOnsetString("onset");
    }
  };

  public static ConditionStage conditionStage = new ConditionStage() {
    {
      setSummary(codeable);
      setAssessments(Arrays.asList(getURI()));
    }
  };

  public static ConditionEvidence conditionEvidence = new ConditionEvidence() {
    {
      setCode(codeable);
      setDetails(Arrays.asList(getURI()));
    }
  };

  public static ConditionLocation conditionLocation = new ConditionLocation() {
    {
      setCode(codeable);
      setBodySiteId(Id.of("bodySite"));
    }
  };

  public static ConditionDueToTarget conditionDueToTarget = new ConditionDueToTarget() {
    {
      setConditionId(Id.of("Condition"));
      setImmunizationId(Id.of("Immunization"));
      setMedicationAdministrationId(Id.of("MedicationAdministration"));
      setMedicationStatementId(Id.of("MedicationStatement"));
      setProcedureId(Id.of("Procedure"));
    }
  };

  public static ConditionDueTo conditionDueTo = new ConditionDueTo() {
    {
      setCode(codeable);
      setTarget(conditionDueToTarget);
    }
  };

  public static ConditionOccurredFollowingTarget conditionOccurredFollowingTarget
      = new ConditionOccurredFollowingTarget() {
        {
          setConditionId(Id.of("Condition"));
          setImmunizationId(Id.of("Immunization"));
          setMedicationAdministrationId(Id.of("MedicationAdministration"));
          setMedicationStatementId(Id.of("MedicationStatement"));
          setProcedureId(Id.of("Procedure"));
        }
      };

  public static ConditionOccurredFollowing conditionOccurredFollowing
      = new ConditionOccurredFollowing() {
        {
          setCode(codeable);
          setTarget(conditionOccurredFollowingTarget);
        }
      };

  public static ConditionAbatement conditionAbatement = new ConditionAbatement() {
    {
      setAbatementBoolean(false);
      setAbatementPeriod(period);
      setAbatementDate(getDate());
      setAbatementAge(numericQuantity);
      setAbatementRange(range);
      setAbatementString("abatement");
    }
  };

  public static ConditionAssessment conditionAssessment = new ConditionAssessment() {
    {
      setConditionId(Id.of("Condition"));
      setImmunizationId(Id.of("Immunization"));
      setMedicationAdministrationId(Id.of("MedicationAdministration"));
      setMedicationStatementId(Id.of("MedicationStatement"));
      setProcedureId(Id.of("Procedure"));
    }
  };

  public static Condition condition = new Condition() {
    {
      setId(Id.of("id"));
      setPatientId(Id.of("patient"));
      setEncounterId(Id.of("encounter"));
      setAsserterId(Id.of("asserter"));
      setDateAsserted(getDate());
      setCode(codeable);
      setCategory(codeable);
      setClinicalStatus(ConditionClinicalStatus.REFUTED);
      setSeverity(codeable);
      setDateWritten(getDateTime());
      setCertainty(codeable);
      setOnset(conditionOnset);
      setAbatement(conditionAbatement);
      setStage(conditionStage);
      setEvidence(Arrays.asList(conditionEvidence));
      setLocations(Arrays.asList(conditionLocation));
      setDueTo(Arrays.asList(conditionDueTo));
      setOccurredFollowing(Arrays.asList(conditionOccurredFollowing));
      setNotes("notes");
    }
  };

  public static Content content = new Content() {
    {
      setItemId(Id.of("item"));
      setAmount(numericQuantity);
    }
  };

  public static Device device = new Device() {
    {
      setType(codeable);
      setContact(contactPoint);
      setStatus(ContactPartyType.PATINF);
      setId(Id.of("id"));
      setPatientId(Id.of("patient"));
      setExpiry(getDateTime());
      setManufactureDate(getDateTime());
      setLocation(location);
      setOwnerId(Id.of("organization"));
      setLotNumber("lot number");
      setManufacturer("manufacturer");
      setModel("model");
      setUdi("udi");
      setVersion("version");
      setUrl(getURI());
    }
  };

  public static Duration duration = Duration.of(2, ChronoUnit.HOURS);

  public static HealthcareServiceType healthcareServiceType = new HealthcareServiceType() {
    {
      setType(codeable);
      setSpecialties(Arrays.asList(codeable));
    }
  };

  public static HealthcareServiceAvailableTime healthcareServiceAvailableTime
      = new HealthcareServiceAvailableTime() {
        {
          setDaysOfWeek(Arrays.asList(DayOfWeek.SUNDAY));
          setAllDay(true);
          setAvailableStartTime(duration);
          setAvailableEndTime(duration);
        }
      };

  public static HealthcareServiceNotAvailable healthcareServiceNotAvailable
      = new HealthcareServiceNotAvailable() {
        {
          setDescription("lunch break");
          setDuring(period);
        }
      };

  public static HealthcareService healthcareService = new HealthcareService() {
    {
      setAppointmentRequired(false);
      setEligibility(codeable);
      setServiceCategory(codeable);
      setId(Id.of("id"));
      setLocationId(Id.of("location"));
      setProvidedById(Id.of("providedBy"));
      setPhoto(TestModels.getPhoto());
      setCharacteristic(Arrays.asList(TestModels.codeable));
      setReferralMethods(Arrays.asList(TestModels.codeable));
      setServiceProvisionCodes(Arrays.asList(TestModels.codeable));
      setTelecoms(Arrays.asList(TestModels.contactPoint));
      setAvailableTimes(Arrays.asList(TestModels.healthcareServiceAvailableTime));
      setNotAvailableTimes(Arrays.asList(TestModels.healthcareServiceNotAvailable));
      setServiceTypes(Arrays.asList(TestModels.healthcareServiceType));
      setProgramNames(Arrays.asList("rub some dirt on it care"));
      setAvailabilityExceptions("not on my lunch break");
      setComment("don't flush paper towels");
      setEligibilityNote("dogs aren't eligible");
      setExtraDetails("except for puppies");
      setPublicKey("cEvin");
      setServiceName("secret");
    }
  };

  public static IngredientItem ingredientItem = new IngredientItem() {
    {
      setMedicationId(Id.of("medication"));
      setSubstanceId(Id.of("substance"));
    }
  };

  public static Ingredient ingredient = new Ingredient() {
    {
      setAmount(ratio);
      setItem(ingredientItem);
    }
  };

  public static ProductBatch productBatch = new ProductBatch() {
    {
      setLotNumber("lotNumber");
      setExpirationDate(getDateTime());
    }
  };

  public static Product product = new Product() {
    {
      setForm(codeable);
      setIngredients(Arrays.asList(ingredient));
      setBatches(Arrays.asList(productBatch));
    }
  };

  public static MedicationPackage medicationPackage = new MedicationPackage() {
    {
      setContainer(codeable);
      setContents(Arrays.asList(content));
    }
  };

  public static Medication medication = new Medication() {
    {
      setIsBrand(false);
      setCode(codeable);
      setId(Id.of("id"));
      setPackage(medicationPackage);
      setManufacturerId(Id.of("manufacturer"));
      setProduct(product);
      setKind("kind");
      setName("name");
    }
  };

  public static Schedule schedule = new Schedule() {
    {
      setRepeatDuration(new BigDecimal(9001));
      setRepeatEnd(getDateTime());
      setRepeatCount(9001);
      setRepeatFrequency(2600);
      setEvent(period);
      setRepeatWhen(ScheduleEventType.WAKE);
      setRepeatUnits(ScheduleTimeUnit.WK);
    }
  };

  public static MedicationPrescriptionSubstitution medicationPrescriptionSubstitution
      = new MedicationPrescriptionSubstitution() {
        {
          setType(codeable);
          setReason(codeable);
        }
      };

  public static MedicationPrescriptionDosageInstruction medicationPrescriptionDosageInstruction
      = new MedicationPrescriptionDosageInstruction() {
        {
          setAsNeeded(booleanOrCodeableConcept);
          setAdditionalInstructions(codeable);
          setMethod(codeable);
          setRoute(codeable);
          setSite(codeable);
          setDoseQuantity(numericQuantity);
          setDoseRange(range);
          setMaxDosePerPeriod(ratio);
          setRate(ratio);
          setTiming(schedule);
          setText("text");
        }
      };

  public static MedicationDispenseDosageInstruction medicationDispenseDosageInstruction
      = new MedicationDispenseDosageInstruction() {
        {
          setAsNeeded(booleanOrCodeableConcept);
          setAdditionalInstructions(codeable);
          setMethod(codeable);
          setRoute(codeable);
          setSite(codeable);
          setDoseQuantity(numericQuantity);
          setDoseRange(range);
          setMaxDosePerPeriod(ratio);
          setRate(ratio);
          setTiming(schedule);
        }
      };

  public static MedicationPrescriptionDispense medicationPrescriptionDispense
      = new MedicationPrescriptionDispense() {
        {
          setMedicationId(Id.of("medication"));
          setNumberOfRepeatsAllowed(9001);
          setValidityPeriod(period);
          setDispenseQuantity(numericQuantity);
          setExpectedSupplyDuration(numericQuantity);

        }
      };

  public static MedicationPrescriptionReason medicationPrescriptionReason
      = new MedicationPrescriptionReason() {
        {
          setCode(codeable);
          setConditionId(Id.of("condition"));
        }
      };

  public static MedicationPrescription medicationPrescription = new MedicationPrescription() {
    {
      setEncounterId(Id.of("encounter"));
      setMedicationId(Id.of("medication"));
      setId(Id.of("id"));
      setPatientId(Id.of("patient"));
      setPrescriberId(Id.of("prescriber"));
      setDateWritten(getDateTime());
      setDosageInstructions(Arrays.asList(medicationPrescriptionDosageInstruction));
      setDispense(medicationPrescriptionDispense);
      setReason(medicationPrescriptionReason);
      setStatus(MedicationPrescriptionStatus.ACTIVE);
      setSubstitution(medicationPrescriptionSubstitution);
    }
  };

  public static MedicationAdministrationDosage medicationAdministrationDosage
      = new MedicationAdministrationDosage() {
        {
          setMethod(codeable);
          setRoute(codeable);
          setSite(codeable);
          setQuantity(numericQuantity);
          setRate(ratio);
          setText("text");
        }
      };

  public static MedicationAdministration medicationAdministration
      = new MedicationAdministration() {
        {
          setWasNotGiven(true);
          setDeviceId(Id.of("device"));
          setEncounterId(Id.of("encounter"));
          setId(Id.of("id"));
          setMedicationId(Id.of("medication"));
          setPrescriptionId(Id.of("prescription"));
          setPatientId(Id.of("patient"));
          setPractitionerId(Id.of("practitioner"));
          setEffectiveTimePeriod(period);
          setReasonsGiven(Arrays.asList(codeable));
          setReasonsNotGiven(Arrays.asList(codeable));
          setDosage(medicationAdministrationDosage);
          setStatus(MedicationAdministrationStatus.IN_PROGRESS);
        }
      };

  public static ReferenceRange referenceRange = new ReferenceRange() {
    {
      setMeaning(codeable);
      setAge(numericInterval);
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

  public static OrganizationContact organizationContact = new OrganizationContact() {
    {
      setAddress(address);
      setTelecoms(Arrays.asList(contactPoint));
      setName(name);
      setPurpose("it passes butter");
    }
  };

  public static PractitionerRole practitionerRole = new PractitionerRole() {
    {
      setRole(codeable);
      setSpecialty(codeable);
      setLocationId(Id.of("location"));
      setManagingOrganizationId(Id.of("managingOrganization"));
      setPeriod(period);
      setHealthcareServiceIds(Arrays.asList(Id.of("healthcareService")));
    }
  };

  public static Qualification qualification = new Qualification() {
    {
      setCode(codeable);
      setPeriod(period);
      setIssuerId(Id.of("issuer"));
    }
  };

  public static Practitioner practitioner = new Practitioner() {
    {
      setId(Id.of("id"));
      setPractitionerRoles(Arrays.asList(practitionerRole));
      setTelecoms(Arrays.asList(contactPoint));
      setCommunications(Arrays.asList(LanguageCode.es));
      setQualifications(Arrays.asList(TestModels.qualification));
    }
  };

  public static SubstanceIngredient substanceIngredient = new SubstanceIngredient() {
    {
      setSubstanceId(Id.of("substance"));
      setQuantity(ratio);
    }
  };

  public static Substance substance = new Substance() {
    {
      setType(codeable);
      setId(Id.of("id"));
      setExpiry(getDateTime());
      setIngredients(Arrays.asList(substanceIngredient));
      setQuantity(numericQuantity);
      setDescription("description");
    }
  };

  public static MedicationDispenseReceiver medicationDispenseReceiver
      = new MedicationDispenseReceiver() {
        {
          setPatientId(Id.of("Patient"));
          setPractitionerId(Id.of("Practitioner"));
        }
      };

  public static MedicationDispense medicationDispense = new MedicationDispense() {
    {
      setSubstitutionReasons(Arrays.asList(codeable));
      setSubstitutionType(codeable);
      setType(codeable);
      setDestinationId(Id.of("Destination"));
      setId(Id.of("id"));
      setMedicationId(Id.of("Medication"));
      setPatientId(Id.of("Patient"));
      setDispenserId(Id.of("Dispenser"));
      setWhenHandedOver(getDateTime());
      setWhenPrepared(getDateTime());
      setAuthorizingPrescriptionIds(Arrays.asList(Id.of("AuthorizingPrescriptions")));
      setSubstitutionResponsiblePartyIds(Arrays.asList(Id.of("SubstitutionResponsibleParties")));
      setDosageInstructions(medicationDispenseDosageInstruction);
      setReceivers(Arrays.asList(medicationDispenseReceiver));
      setStatus(MedicationDispenseStatus.ENTERED_IN_ERROR);
      setDaysSupply(numericQuantity);
      setQuantity(numericQuantity);
      setNote("note");
    }
  };

  public static ImagingStudySeriesInstance imagingStudySeriesInstance
      = new ImagingStudySeriesInstance() {
        {
          setNumber(new BigDecimal(9001));
          setContent(Arrays.asList(attachment));
          setSopClassOid(Id.of("2.3.4.5"));
          setUid(Id.of("2.3.4.5"));
          setTitle("title");
          setType("type");
        }
      };

  public static Coding coding = new Coding() {
    {
      setPrimary(true);
      setCode("code");
      setDisplay("display");
      setVersion("version");
      setSystem(getURI());
    }
  };

  public static ImagingStudySeries imagingStudySeries = new ImagingStudySeries() {
    {
      setNumber(new BigDecimal(9001));
      setNumberOfInstances(new BigDecimal(9002));
      setBodySite(coding);
      setLaterality(coding);
      setAvailability(ImagingStudyAvailability.NEARLINE);
      setDateTime(getDateTime());
      setInstances(Arrays.asList(imagingStudySeriesInstance));
      setUid(Id.of("2.3.4.5"));
      setDescription("description");
      setModality("modality");
      setUrl(getURI());
    }
  };

  public static ImagingStudy imagingStudy = new ImagingStudy() {
    {
      setNumberOfInstances(new BigDecimal(9001));
      setNumberOfSeries(new BigDecimal(9002));
      setId(Id.of("Id"));
      setPatientId(Id.of("Patient"));
      setInterpreterId(Id.of("Interpreter"));
      setReferrerId(Id.of("Referrer"));
      setAvailability(ImagingStudyAvailability.NEARLINE);
      setSeries(imagingStudySeries);
      setStarted(getDateTime());
      setModalityList(Arrays.asList(codeable));
      setProcedures(Arrays.asList(coding));
      setOrderIds(Arrays.asList(Id.of("Orders")));
      setOid(Id.of("2.3.4.5"));
      setClinicalInformation("clinicalInformation");
      setDescription("description");
      setAccession(getURI());
      setUrl(getURI());
    }
  };

  public static DiagnosticImage diagnosticImage = new DiagnosticImage() {
    {
      setLink(getPhoto());
      setComment("comment");
    }
  };

  public static DiagnosticOrderEventActor diagnosticOrderEventActor
      = new DiagnosticOrderEventActor() {
        {
          setDeviceId(Id.of("Device"));
          setPractitionerId(Id.of("Practitioner"));
        }
      };

  public static DiagnosticOrderEvent diagnosticOrderEvent = new DiagnosticOrderEvent() {
    {
      setDescription(codeable);
      setActor(diagnosticOrderEventActor);
      setStatus(DiagnosticOrderEventStatus.ACCEPTED);
      setDateTime(TestModels.getDateTime());
    }
  };

  public static DiagnosticOrderItem diagnosticOrderItem = new DiagnosticOrderItem() {
    {
      setBodySiteCodeableConcept(codeable);
      setCode(codeable);
      setEvent(diagnosticOrderEvent);
      setStatus(DiagnosticOrderItemStatus.ACCEPTED);
      setBodySiteReferenceId(Id.of("BodySiteReference"));
      setSpecimens(Arrays.asList(Id.of("Specimens")));
    }
  };

  public static SpecimenContainer specimenContainer = new SpecimenContainer() {
    {
      setAdditiveCodeableConcept(codeable);
      setType(codeable);
      setId(Id.of("id"));
      setAdditiveReferenceId(Id.of("additiveReference"));
      setCapacity(numericQuantity);
      setSpecimenQuantity(numericQuantity);
      setDescription("description");
    }
  };

  public static SpecimenTreatment specimenTreatment = new SpecimenTreatment() {
    {
      setProcedure(codeable);
      setAdditives(Arrays.asList(Id.of("additives")));
      setDescription("description");
    }
  };

  public static SpecimenSubject specimenSubject = new SpecimenSubject() {
    {
      setDeviceId(Id.of("Device"));
      setGroupId(Id.of("Group"));
      setPatientId(Id.of("Patient"));
      setSubstanceId(Id.of("Substance"));
    }
  };

  public static Specimen specimen = new Specimen() {
    {
      setCollectionBodySiteCodableConcept(codeable);
      setCollectionMethod(codeable);
      setType(codeable);
      setCollectionBodySiteReferenceId(Id.of("BodySite"));
      setCollectorId(Id.of("Practitioner"));
      setId(Id.of("Specimen"));
      setCollectedDateTime(getDateTime());
      setReceivedTime(getDateTime());
      setCollectedPeriod(period);
      setContainers(Arrays.asList(specimenContainer));
      setParentIds(Arrays.asList(Id.of("parents")));
      setCollectionComments(Arrays.asList("collectionComments"));
      setCollectedQuantity(numericQuantity);
      setSubject(specimenSubject);
      setTreatments(specimenTreatment);
      setAccessionIdentifier("accessionIdentifier");
    }
  };

  public static DocumentReferenceContext documentReferenceContext = new DocumentReferenceContext() {
    {
      setFacilityType(codeable);
      setPracticeSetting(codeable);
      setSourcePatientInfoId(Id.of("Patient"));
      setPeriod(period);
      setEvents(Arrays.asList(codeable));
      setRelated(Arrays.asList(getURI()));
    }
  };

  public static DocumentReferenceRelatesTo documentReferenceRelatesTo
      = new DocumentReferenceRelatesTo() {
        {
          setCode(DocumentRelationshipType.APPENDS);
          setTargetId(Id.of("Target"));
        }
      };

  public static DocumentReferenceAuthenticator documentReferenceAuthenticator
      = new DocumentReferenceAuthenticator() {
        {
          setOrganizationId(Id.of("Organization"));
          setPractitionerId(Id.of("Practitioner"));
        }
      };

  public static DocumentReferenceAuthor documentReferenceAuthor = new DocumentReferenceAuthor() {
    {
      setDeviceId(Id.of("Device"));
      setOrganizationId(Id.of("Organization"));
      setPatientId(Id.of("Patient"));
      setPractitionerId(Id.of("Practitioner"));
      setRelatedPersonId(Id.of("RelatedPerson"));
    }
  };

  public static DocumentReferenceSubject documentReferenceSubject = new DocumentReferenceSubject() {
    {
      setDeviceId(Id.of("Device"));
      setGroupId(Id.of("Group"));
      setPatientId(Id.of("Patient"));
      setPractitionerId(Id.of("Practitioner"));
    }
  };

  public static DocumentReference documentReference = new DocumentReference() {
    {
      setConfidentiality(codeable);
      setDocumentReferenceClass(codeable);
      setType(codeable);
      setAuthenticator(documentReferenceAuthenticator);
      setContext(documentReferenceContext);
      setStatus(DocumentReferenceStatus.CURRENT);
      setSubject(documentReferenceSubject);
      setId(Id.of("DocumentReference"));
      setCustodianId(Id.of("Organization"));
      setCreated(getDateTime());
      setIndexed(getDateTime());
      setContents(Arrays.asList(attachment));
      setAuthors(Arrays.asList(documentReferenceAuthor));
      setRelatesTo(Arrays.asList(documentReferenceRelatesTo));
      setFormats(Arrays.asList(getURI()));
      setDocStatus(ReferredDocumentStatus.AMENDED);
      setDescription("description");
    }
  };

  public static DiagnosticOrderSupportingInformation diagnosticOrderSupportingInformation
      = new DiagnosticOrderSupportingInformation() {
        {
          setConditionId(Id.of("Condition"));
          setDocumentReferenceId(Id.of("DocumentReference"));
          setObservationId(Id.of("Observation"));
        }
      };

  public static DiagnosticPerformer diagnosticPerformer = new DiagnosticPerformer() {
    {
      setOrganizationId(Id.of("Organization"));
      setPractitionerId(Id.of("Practitioner"));
    }
  };

  public static DiagnosticSubject diagnosticSubject = new DiagnosticSubject() {
    {
      setDeviceId(Id.of("Device"));
      setGroupId(Id.of("Group"));
      setLocationId(Id.of("Location"));
      setPatientId(Id.of("Patient"));
    }
  };

  public static DiagnosticReport diagnosticReport = new DiagnosticReport() {
    {
      setCodedDiagnoses(Arrays.asList(codeable));
      setName(codeable);
      setServiceCategory(codeable);
      setPerformer(diagnosticPerformer);
      setStatus(DiagnosticReportStatus.APPENDED);
      setSubject(diagnosticSubject);
      setId(Id.of("DiagnosticReport"));
      setEncounterId(Id.of("Encounter"));
      setDiagnosticDateTime(getDateTime());
      setIssued(getDateTime());
      setDiagnosticPeriod(period);
      setPresentedForms(Arrays.asList(attachment));
      setImages(Arrays.asList(diagnosticImage));
      setRequestDetailIds(Arrays.asList(Id.of("RequestDetails")));
      setImagingStudy(Arrays.asList(imagingStudy));
      setSpecimens(Arrays.asList(specimen));
      setResultId(Id.of("Result"));
      setConclusion("conclusion");
    }
  };

  public static ImmunizationReaction immunizationReaction = new ImmunizationReaction() {
    {
      setReported(true);
      setDetailId(Id.of("Observation"));
      setDate(getDateTime());
    }
  };

  public static ImmunizationVaccinationProtocol immunizationVaccinationProtocol
      = new ImmunizationVaccinationProtocol() {
        {
          setDoseSequence(new BigDecimal(9001));
          setSeriesDoses(new BigDecimal(9002));
          setDoseStatus(codeable);
          setDoseStatusReason(codeable);
          setDoseTarget(codeable);
          setAuthorityId(Id.of("Organization"));
          setDescription("description");
          setSeries("series");
        }
      };

  public static Immunization immunization = new Immunization() {
    {
      setReported(true);
      setWasNotGiven(true);
      setRoute(codeable);
      setSite(codeable);
      setVaccineType(codeable);
      setEncounterId(Id.of("Encounter"));
      setId(Id.of("Immunization"));
      setLocationId(Id.of("Location"));
      setManufacturerId(Id.of("Organization"));
      setPatientId(Id.of("Patient"));
      setPerformerId(Id.of("Practitioner"));
      setRequesterId(Id.of("Practitioner"));
      setDate(getDateTime());
      setReasonsGiven(Arrays.asList(codeable));
      setReasonsNotGiven(Arrays.asList(codeable));
      setReactions(Arrays.asList(immunizationReaction));
      setVaccinationProtocols(Arrays.asList(immunizationVaccinationProtocol));
      setExpirationDate(TestModels.getDate());
      setDoseQuantity(numericQuantity);
      setLotNumber("lotNumber");
    }
  };

  public static MedicationStatementDosage medicationStatementDosage
      = new MedicationStatementDosage() {
        {
          setAsNeededBoolean(true);
          setAsNeededCodeableConcept(codeable);
          setMethod(codeable);
          setRoute(codeable);
          setSite(codeable);
          setQuantity(numericQuantity);
          setMaxDosePerPeriod(ratio);
          setRate(ratio);
          setSchedule(schedule);
          setText("text");
        }
      };

  public static MedicationStatementInformationSource medicationStatementInformationSource
      = new MedicationStatementInformationSource() {
        {
          setPatientId(Id.of("Patient"));
          setPractitionerId(Id.of("Practitioner"));
          setRelatedPersonId(Id.of("RelatedPerson"));
        }
      };

  public static MedicationStatement medicationStatement = new MedicationStatement() {
    {
      setWasNotGiven(true);
      setReasonForUseCodeableConcept(codeable);
      setReasonForUseReferenceId(Id.of("Condition"));
      setMedicationId(Id.of("Medication"));
      setId(Id.of("MedicationStatement"));
      setPatientId(Id.of("Patient"));
      setDateAsserted(getDateTime());
      setEffectiveDateTime(getDateTime());
      setEffectivePeriod(period);
      setReasonsNotGiven(Arrays.asList(codeable));
      setDosages(Arrays.asList(medicationStatementDosage));
      setInformationSource(medicationStatementInformationSource);
      setStatus(MedicationStatementStatus.COMPLETED);
      setNote("note");
    }
  };

  public static GroupCharacteristic groupCharacteristic = new GroupCharacteristic() {
    {
      setExclude(true);
      setValueBoolean(true);
      setCode(codeable);
      setValueCodeableConcept(codeable);
      setValueQuantity(numericQuantity);
      setValueRange(range);
    }
  };

  public static GroupMember groupMember = new GroupMember() {
    {
      setDeviceId(Id.of("Device"));
      setMedicationId(Id.of("Medication"));
      setPatientId(Id.of("Patient"));
      setPractitionerId(Id.of("Practitioner"));
      setSubstanceId(Id.of("Substance"));
    }
  };

  public static Group group = new Group() {
    {
      setQuantity(new BigDecimal(9001));
      setActual(true);
      setCode(codeable);
      setType(GroupType.ANIMAL);
      setId(Id.of("Group"));
      setCharacteristics(Arrays.asList(groupCharacteristic));
      setMembers(Arrays.asList(groupMember));
      setName("name");
    }
  };

  public static DiagnosticOrderSubject diagnosticOrderSubject = new DiagnosticOrderSubject() {
    {
      setDeviceId(Id.of("Device"));
      setGroupId(Id.of("Group"));
      setLocationId(Id.of("Location"));
      setPatientId(Id.of("Patient"));
    }
  };

  public static DiagnosticOrder diagnosticOrder = new DiagnosticOrder() {
    {
      setPriority(DiagnosticOrderPriority.ASAP);
      setStatus(DiagnosticOrderStatus.ACCEPTED);
      setSubject(diagnosticOrderSubject);
      setSupportingInformation(diagnosticOrderSupportingInformation);
      setId(Id.of("DiagnosticOrder"));
      setEncounterId(Id.of("Encounter"));
      setOrdererId(Id.of("Practitioner"));
      setEvents(Arrays.asList(diagnosticOrderEvent));
      setItems(Arrays.asList(diagnosticOrderItem));
      setSpecimenIds(Arrays.asList(Id.of("Specimen")));
      setClinicalNotes("clinicalNotes");
    }
  };

  public static ProcedureDevice procedureDevice = new ProcedureDevice() {
    {
      setAction(codeable);
      setManipulatedId(Id.of("Device"));
    }
  };

  public static ProcedureUsedItem procedureUsedItem = new ProcedureUsedItem() {
    {
      setDeviceId(Id.of("Device"));
      setMedicationId(Id.of("Medication"));
      setSubstanceId(Id.of("Substance"));
    }
  };

  public static ProcedurePerformer procedurePerformer = new ProcedurePerformer() {
    {
      setRole(codeable);
      setPatientId(Id.of("Patient"));
      setPractitionerId(Id.of("Practitioner"));
      setRelatedPersonId(Id.of("RelatedPerson"));
    }
  };

  public static ProcedureBodySite procedureBodySite = new ProcedureBodySite() {
    {
      setSiteCodeableConcept(codeable);
      setSiteReferenceId(Id.of("BodySite"));
    }
  };

  public static ProcedureRelatedItemTarget procedureRelatedItemTarget
      = new ProcedureRelatedItemTarget() {
        {
          setConditionId(Id.of("Condition"));
          setDiagnosticReportId(Id.of("DiagnosticReport"));
          setImagingStudyId(Id.of("ImagingStudy"));
          setImmunizationId(Id.of("Immunization"));
          setMedicationAdministrationId(Id.of("MedicationAdministration"));
          setMedicationDispenseId(Id.of("MedicationDispense"));
          setMedicationPrescriptionId(Id.of("MedicationPrescription"));
          setMedicationStatementId(Id.of("MedicationStatement"));
          setObservationId(Id.of("Observation"));
          setProcedureId(Id.of("Procedure"));
        }
      };

  public static ProcedureRelatedItem procedureRelatedItem = new ProcedureRelatedItem() {
    {
      setTarget(procedureRelatedItemTarget);
      setType(ProcedureRelatedItemType.BECAUSE_OF);
    }
  };

  public static Procedure procedure = new Procedure() {
    {
      setCategory(codeable);
      setOutcome(codeable);
      setType(codeable);
      setEncounterId(Id.of("Encounter"));
      setLocationId(Id.of("Location"));
      setPatientId(Id.of("Patient"));
      setId(Id.of("Procedure"));
      setPerformedDateTime(getDateTime());
      setPerformedPeriod(period);
      setComplications(Arrays.asList(codeable));
      setFollowups(Arrays.asList(codeable));
      setIndications(Arrays.asList(codeable));
      setReportIds(Arrays.asList(Id.of("DiagnosticReport")));
      setBodySites(Arrays.asList(procedureBodySite));
      setDevices(Arrays.asList(procedureDevice));
      setPerformers(Arrays.asList(procedurePerformer));
      setRelatedItems(Arrays.asList(procedureRelatedItem));
      setStatus(ProcedureStatus.ABORTED);
      setUsed(procedureUsedItem);
      setNotes("string");
    }
  };

  private TestModels() {
  }
}
