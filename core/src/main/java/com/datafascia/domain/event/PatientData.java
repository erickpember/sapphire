// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import com.datafascia.common.avro.LocalDateAsStringEncoding;
import com.datafascia.common.avro.LanguageCodeEncoding;
import com.datafascia.models.Gender;
import com.datafascia.models.MaritalStatus;
import com.datafascia.models.Race;
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
public class PatientData implements EventData {

  private String patientId;
  private String accountNumber;
  private String firstName;
  private String middleName;
  private String lastName;

  @AvroEncode(using = GenderEncoding.class)
  private Gender gender;

  @AvroEncode(using = LocalDateAsStringEncoding.class)
  private LocalDate birthDate;

  @AvroEncode(using = MaritalStatusEncoding.class)
  private MaritalStatus maritalStatus;

  @AvroEncode(using = RaceEncoding.class)
  private Race race;

  @AvroEncode(using = LanguageCodeEncoding.class)
  private LanguageCode language;
}
