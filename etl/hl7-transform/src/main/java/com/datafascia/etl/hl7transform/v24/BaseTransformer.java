// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.model.v24.datatype.TSComponentOne;
import ca.uhn.hl7v2.model.v24.datatype.XPN;
import ca.uhn.hl7v2.model.v24.segment.PID;
import com.datafascia.domain.event.PatientData;
import com.datafascia.etl.hl7transform.MessageToEventTransformer;
import com.datafascia.models.Gender;
import com.datafascia.models.MaritalStatus;
import com.datafascia.models.Race;
import com.neovisionaries.i18n.LanguageAlpha3Code;
import com.neovisionaries.i18n.LanguageCode;
import java.time.LocalDate;

/**
 * Implements common methods for transformers.
 */
public abstract class BaseTransformer implements MessageToEventTransformer {

  protected PatientData toPatientData(PID pid) throws HL7Exception {
    return PatientData.builder()
        .patientId(
            pid.getPatientIdentifierList(0).getID().getValue())
        .accountNumber(pid.getPatientAccountNumber().getID().getValue())
        .fullName(toFullName(pid.getPatientName(0)))
        .address(
            pid.getPatientAddress(0).getStreetAddress().getStreetOrMailingAddress().getValue())
        .gender(
            toGender(pid.getAdministrativeSex().getValue()))
        .birthDate(
            toBirthDate(pid.getDateTimeOfBirth().getTimeOfAnEvent()))
        .maritalStatus(
            toMaritalStatus(pid.getMaritalStatus().getIdentifier().getValue()))
        .race(
            toRace(pid.getRace(0).getIdentifier().getValue()))
        .language(
            toLanguage(pid.getPrimaryLanguage().getIdentifier().getValue()))
        .build();
  }

  private String toFullName(XPN patientName) throws HL7Exception {
    StringBuilder fullName = new StringBuilder(patientName.getGivenName().getValueOrEmpty());
    append(fullName, patientName.getSecondAndFurtherGivenNamesOrInitialsThereof());
    append(fullName, patientName.getFamilyName().getSurname());
    return fullName.toString();
  }

  private void append(StringBuilder output, ST st) throws HL7Exception {
    if (!st.isEmpty()) {
      if (output.length() > 0) {
        output.append(' ');
      }
      output.append(st.getValue());
    }
  }

  private Gender toGender(String code) {
    switch (code) {
      case "F":
        return Gender.Female;
      case "M":
        return Gender.Male;
      default:
        return Gender.Unknown;
    }
  }

  private LocalDate toBirthDate(TSComponentOne birthDate) throws HL7Exception {
    return LocalDate.of(birthDate.getYear(), birthDate.getMonth(), birthDate.getDay());
  }

  private MaritalStatus toMaritalStatus(String code) {
    switch (code) {
      case "A":
        return MaritalStatus.LegallySeparated;
      case "D":
        return MaritalStatus.Divorced;
      case "M":
        return MaritalStatus.Married;
      case "P":
        return MaritalStatus.DomesticPartner;
      case "S":
        return MaritalStatus.NeverMarried;
      case "W":
        return MaritalStatus.Widowed;
      default:
        return null;
    }
  }

  private Race toRace(String code) {
    switch (code) {
      case "1002-5":
        return Race.AmericanIndian;
      case "2028-9":
        return Race.Asian;
      case "2076-8":
        return Race.PacificIslander;
      case "2054-5":
        return Race.Black;
      case "2106-3":
        return Race.White;
      case "2131-1":
        return Race.Other;
      default:
        return Race.Unknown;
    }
  }

  private LanguageCode toLanguage(String code) {
    LanguageAlpha3Code language = LanguageAlpha3Code.getByCodeIgnoreCase(code);
    return (language != null) ? language.getAlpha2() : null;
  }
}
