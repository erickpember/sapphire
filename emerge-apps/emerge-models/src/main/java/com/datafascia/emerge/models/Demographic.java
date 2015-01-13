// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class is used to hold the Demographic data for Emerge and can be serialized, deserialized to
 * and from a CSV file.
 */
@Slf4j @NoArgsConstructor @EqualsAndHashCode
public class Demographic {
  @JsonIgnore
  private static final ObjectReader reader;
  @JsonIgnore
  private static final ObjectWriter writer;

  static {
    CsvMapper mapper = new CsvMapper();
    mapper.configure(CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING, true);
    CsvSchema.Builder builder = CsvSchema.builder();
    for (String header : headers()) {
      builder.addColumn(header);
    }
    CsvSchema schema = builder.build();
    reader = mapper.reader(Demographic.class).with(schema);
    writer = mapper.writer(schema);
  }

  @Getter @Setter @JsonProperty(value = "Entry #", index = 0)
  private String entry;
  @Getter @Setter @JsonProperty(value = "Date Created", index = 1)
  private String  dateCreated;
  @Getter @Setter @JsonProperty(value = "Date Updated", index = 2)
  private String  dateUpdated;
  @Getter @Setter @JsonProperty(value = "IP Address", index = 3)
  private String ipAddress ;
  @Getter @Setter @JsonProperty(value = "Data Collection Date", index = 4)
  private String dataCollectionDate;
  @Getter @Setter @JsonProperty(value = "JHED ID", index = 5)
  private String jhedId ;
  @Getter @Setter @JsonProperty(value = "Subject Patient ID", index = 6)
  private String subjectPatientId;
  @Getter @Setter @JsonProperty(value = "Subject Patcom", index = 7)
  private String subjectPatcom ;
  @Getter @Setter @JsonProperty(value = "Patient Name", index = 8)
  private String patientName ;
  @Getter @Setter @JsonProperty(value = "SICU Admission Date", index = 9)
  private String sicuAdmissionDate;
  @Getter @Setter @JsonProperty(value = "Readmission", index = 10)
  private String readmission;
  @Getter @Setter @JsonProperty(value = "Patient Date of Birth", index = 11)
  private String  patientDateOfBirth;
  @Getter @Setter @JsonProperty(value = "Gender", index = 12)
  private String gender;
  @Getter @Setter @JsonProperty(value = "Race", index = 13)
  private String race;
  @Getter @Setter @JsonProperty(value = "Patient Admission Weight (kg)", index = 14)
  private String patientAdmissionWeightKg;
  @Getter @Setter @JsonProperty(value = "Patient Admission Height (cm)", index = 15)
  private String patientAdmissionHeightCm;
  @Getter @Setter @JsonProperty(value = "Prior to Hospital Stay", index = 16)
  private String priorToHospitalStay;
  @Getter @Setter @JsonProperty(value = "Highest-level Activity", index = 17)
  private String highestLevelActivity;
  @Getter @Setter @JsonProperty(value = "Screening Tool Used", index = 18)
  private String screeningToolUsed;
  @Getter @Setter @JsonProperty(value = "IVC Filter", index = 19)
  private String ivcFilter ;

  /**
   * @return the headers for the object
   */
  public static List<String> headers() {
    List<String> headers = new ArrayList<String>();
    for (Field f:Demographic.class.getDeclaredFields()) {
      if (f.isAnnotationPresent(JsonProperty.class)) {
        JsonProperty property = f.getAnnotation(JsonProperty.class);
        headers.add(property.index(), property.value());
      }
    }

    return headers;
  }

  /**
   * Create object from string using ',' as separators
   *
   * @param line the line to parse values from
   * @return Demographic instance created from string
   * @throws java.io.IOException
   */
  public static Demographic fromString(String line) throws IOException {
    return reader.readValue(line);
  }

  /**
   * Serialize the object to string using ',' as separator
   * @return string representation of Demographic instance
   * @throws com.fasterxml.jackson.core.JsonProcessingException
   */
  public String asString() throws JsonProcessingException {
    return writer.writeValueAsString(this).trim();
  }
}
