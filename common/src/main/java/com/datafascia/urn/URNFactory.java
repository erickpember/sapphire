// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.urn;

import com.google.common.base.Joiner;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;

import static java.net.URLEncoder.encode;

/**
 * Factory to build URN objects needed under various dataFascia namespaces
 */
@Slf4j
public class URNFactory {
  /** URN scheme identifier */
  public static final String URN = "urn";
  /** URN separator */
  public static final String URN_SEP = ":";
  /** Namespace for Institution patient identifier */
  public static final String NS_INSTITUTION_PATIENT_ID = "df-institution-patientId-1";
  /** Namespace for dataFascia patient identifier */
  public static final String NS_PATIENT_ID = "df-patientId-1";
  /** Namespace for dataFascia patient encounter identifier */
  public static final String NS_ENCOUNTER_ID = "df-observationId-1";
  /** Namespace for dataFascia patient observation identifier */
  public static final String NS_OBSERVATION_ID = "df-encounterId-1";
  /** Namespace for dataFascia patient hospitalization identifier */
  public static final String NS_HOSPITALIZATION_ID = "df-hospitalizationId-1";

  /** Encoding */
  private static final String UTF8 = "UTF-8";

  /**
   * The URN of this type have the schema:
   *
   * urn:df-institution-patientId-1:[institution id]:[facility id]:[institution patient id]
   *
   * @param instId the institution identifier
   * @param facilityId the facility identifier
   * @param patientId the patient identifier
   *
   * @return URN of type df-institution-patientId-1
   */
  public static URI institutionPatientId(String instId, String facilityId, String patientId) {
    if (instId == null || facilityId == null || patientId == null) {
      throw new IllegalArgumentException("Institution patient identifiers cannot be null");
    }
    try {
      return new URI(urn(NS_INSTITUTION_PATIENT_ID, instId, facilityId, patientId));
    } catch(URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * The URN of this type have the schema:
   *
   * urn:df-patientId-1:[dataFascia patient id]
   *
   * @param patientId the patient identifier
   *
   * @return URN of type df-patientId-1
   */
  public static URI patientId(String patientId) {
    if (patientId == null) {
      throw new IllegalArgumentException("Patient identifier cannot be null");
    }

    try {
      return new URI(urn(NS_PATIENT_ID, patientId));
    } catch(URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @param ns the URN namespaceifier
   * @param value the URN value
   *
   * @return string with URN of asked type
   */
  public static String urn(String ns, String... values) {
    if ((ns == null) || (values == null)) {
      throw new IllegalArgumentException("Null arguments cannot be passed for URN construction");
    }
    try {
      List<String> paths = new ArrayList<>();
      paths.add(URN);
      paths.add(encode(ns, UTF8));
      for (String value : values) {
        paths.add(encode(value, UTF8));
      }

      return Joiner.on(URN_SEP).join(paths);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
