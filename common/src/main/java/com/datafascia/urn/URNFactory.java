// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.urn;

import com.google.common.base.Joiner;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;

import static java.net.URLEncoder.encode;

/**
 * Factory to build URN objects needed under various dataFascia namespaces
 */
@Slf4j
public class URNFactory {
  /** Namespace for Institution patient identifier */
  public static final String NS_INSTITUTION_PATIENT_ID = "df-institution-patientId-1";

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
  public static URI getInstitutionPatientId(String instId, String facilityId, String patientId)
    throws URISyntaxException, UnsupportedEncodingException {
    if (instId == null || facilityId == null || patientId == null) {
      throw new IllegalArgumentException("Institution patient idenfier parameters cannot be null");
    }

    Joiner join = Joiner.on(":");
    String path = join.join(encode(instId, "UTF-8"), encode(facilityId, "UTF-8"), encode(patientId,
        "UTF-8"));

    return new URI("urn", join.join(NS_INSTITUTION_PATIENT_ID, path), null);
  }
}
