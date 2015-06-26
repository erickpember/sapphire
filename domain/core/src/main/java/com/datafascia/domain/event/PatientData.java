// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import com.datafascia.common.avro.LanguageCodeEncoding;
import com.datafascia.common.avro.LocalDateEncoding;
import com.datafascia.domain.fhir.RaceEnum;
import com.neovisionaries.i18n.LanguageCode;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.avro.reflect.AvroEncode;

/**
 * Patient data included when admitting patient.
 */
@AllArgsConstructor @Builder @Data @NoArgsConstructor
public class PatientData {

  private String institutionPatientId;
  private String accountNumber;
  private String firstName;
  private String middleName;
  private String lastName;

  @AvroEncode(using = AdministrativeGenderEnumEncoding.class)
  private AdministrativeGenderEnum gender;

  @AvroEncode(using = LocalDateEncoding.class)
  private LocalDate birthDate;

  @AvroEncode(using = MaritalStatusCodesEnumEncoding.class)
  private MaritalStatusCodesEnum maritalStatus;

  @AvroEncode(using = RaceEnumEncoding.class)
  private RaceEnum race;

  @AvroEncode(using = LanguageCodeEncoding.class)
  private LanguageCode language;
}
