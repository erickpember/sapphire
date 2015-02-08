// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.urn;

import com.google.common.base.Joiner;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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
   * @param ns the URN namespaceifier
   * @param values the URN values
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

  /**
   * @param urn the URN
   *
   * @return the namespace from the URN
   *
   * @throws java.net.URISyntaxException for illegal URN values
   */
  public static String namespace(String urn) throws URISyntaxException {
    String[] parts = urn.split(URN_SEP);
    if ((parts.length >= 2) && (parts[0].compareToIgnoreCase(URN) == 0)) {
      return parts[1];
    }

    throw new URISyntaxException(urn, "Invalid URN");
  }

  /**
   * @param urn the URN
   *
   * @return the path from the URN
   *
   * @throws java.net.URISyntaxException for illegal URN values
   */
  public static String path(String urn) throws URISyntaxException {
    String[] parts = urn.split(URN_SEP);
    if ((parts.length >= 3) && (parts[0].compareToIgnoreCase(URN) == 0)) {
      return parts[2];
    }

    throw new URISyntaxException(urn, "Invalid URN");
  }
}
