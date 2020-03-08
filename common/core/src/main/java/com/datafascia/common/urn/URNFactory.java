// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.common.urn;

import com.google.common.base.Joiner;
import java.io.UnsupportedEncodingException;
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
  /** Namespace for dataFascia Oid identifier */
  public static final String NS_OID = "oid";
  /** Namespace for dataFascia patient identifier */
  public static final String NS_PATIENT_ID = "df-patientId-1";
  /** Namespace for dataFascia patient encounter identifier */
  public static final String NS_ENCOUNTER_ID = "df-encounterId-1";
  /** Namespace for dataFascia appointment identifier */
  public static final String NS_APPOINTMENT_ID = "df-appointmentId-1";
  /** Namespace for dataFascia schedule identifier */
  public static final String NS_SCHEDULE_ID = "df-scheduleId-1";
  /** Namespace for dataFascia schedule slot identifier */
  public static final String NS_SLOT_ID = "df-slotId-1";
  /** Namespace for dataFascia order identifier */
  public static final String NS_ORDER_ID = "df-orderId-1";
  /** Namespace for dataFascia device component identifier */
  public static final String NS_DEVICE_COMPONENT_ID = "df-device-componentId-1";
  /** Namespace for dataFascia device component production specification identifier */
  public static final String NS_DEVICE_COMPONENT_SPECIFICATION_ID
      = "df-device-component-specificationId-1";
  /** Namespace for dataFascia device metric identifier */
  public static final String NS_DEVICE_METRIC_ID = "df-device-metricId-1";
  /** Namespace for dataFascia patient episode of care identifier */
  public static final String NS_EPISODE_OF_CARE_ID = "df-episode-of-careId-1";
  /** Namespace for dataFascia patient observation identifier */
  public static final String NS_OBSERVATION_ID = "df-observationId-1";
  /** Namespace for dataFascia patient referral request identifier */
  public static final String NS_REFERRAL_REQUEST_ID = "df-referral-requestId-1";
  /** Namespace for dataFascia patient hospitalization identifier */
  public static final String NS_HOSPITALIZATION_ID = "df-hospitalizationId-1";
  /** Namespace for dataFascia practitioner identifier */
  public static final String NS_PRACTITIONER_ID = "df-practitionerId-1";
  /** Namespace for dataFascia patient-related person identifier */
  public static final String NS_RELATED_PERSON_ID = "df-related-personId-1";
  /** Namespace for dataFascia medication prescription identifier */
  public static final String NS_PRESCRIPTION_ID = "df-prescriptionId-1";
  /** Namespace for dataFascia body site identifier */
  public static final String NS_BODY_SITE_ID = "df-body-site-1";
  /** Namespace for dataFascia healthcare device identifier */
  public static final String NS_DEVICE_ID = "df-deviceId-1";
  /** Namespace for dataFascia group identifier */
  public static final String NS_GROUP_ID = "df-groupId-1";
  /** Namespace for dataFascia imaging study identifier */
  public static final String NS_IMAGING_STUDY_ID = "df-imaging-studyId-1";
  /** Namespace for dataFascia immunization identifier */
  public static final String NS_IMMUNIZATION_ID = "df-immunizationId-1";
  /** Namespace for dataFascia imaging study identifier */
  public static final String NS_PROCEDURE_ID = "df-procedureId-1";
  /** Namespace for dataFascia specimen identifier */
  public static final String NS_SPECIMEN_ID = "df-specimenId-1";
  /** Namespace for dataFascia specimen container identifier */
  public static final String NS_SPECIMEN_CONTAINER_ID = "df-specimen-containerId-1";
  /** Namespace for dataFascia diagnostic report identifier */
  public static final String NS_DIAGNOSTIC_REPORT_ID = "df-diagnostic-reportId-1";
  /** Namespace for dataFascia diagnostic order identifier */
  public static final String NS_DIAGNOSTIC_ORDER_ID = "df-diagnostic-orderId-1";
  /** Namespace for dataFascia document reference identifier */
  public static final String NS_DOCUMENT_REFERENCE_ID = "df-document-referenceId-1";
  /** Namespace for dataFascia medication administration identifier */
  public static final String NS_MEDICATION_ID = "df-medicationId-1";
  /** Namespace for dataFascia medication administration identifier */
  public static final String NS_MEDICATION_ADMINISTRATION_ID = "df-medication-administrationId-1";
  /** Namespace for dataFascia medication dispense identifier */
  public static final String NS_MEDICATION_DISPENSE_ID = "df-medication-dispenseId-1";
  /** Namespace for dataFascia medication statement identifier */
  public static final String NS_MEDICATION_STATEMENT_ID = "df-medication-statementId-1";
  /** Namespace for dataFascia healthcare service identifier */
  public static final String NS_HEALTHCARE_SERVICE_ID = "df-healthcare-serviceId-1";
  /** Namespace for dataFascia location identifier */
  public static final String NS_LOCATION_ID = "df-locationId-1";
  /** Namespace for dataFascia substance identifier */
  public static final String NS_SUBSTANCE_ID = "df-substanceId-1";
  /** Namespace for dataFascia organization identifier */
  public static final String NS_ORGANIZATION_ID = "df-organizationId-1";
  /** Namespace for dataFascia condition identifier */
  public static final String NS_CONDITION_ID = "df-conditionId-1";
  /** Namespace for dataFascia model types */
  public static final String NS_MODEL_TYPE = "df-model-1";
  /** URN prefix for dataFascia model types */
  public static final String MODEL_PREFIX = URN + URN_SEP + NS_MODEL_TYPE + URN_SEP;

  /** Encoding */
  private static final String UTF8 = "UTF-8";

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

  private URNFactory() {
  }
}
