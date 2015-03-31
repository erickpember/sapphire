// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.avro.reflect.Nullable;

/**
 * Avro-friendly POJO for OBX data.
 * Elements are Marked as @Nullable where the cardinality for the segment starts with 0..
 */
@AllArgsConstructor @Builder @Data @NoArgsConstructor
public class ObservationData {
  /**
   * The type of message from which this observation came.
   */
  private ObservationType observationType;

  /**
   * The version of HL7 this came from.
   */
  private String hl7Version;

  /**
   * Matching NTE segments
   */
  @Nullable
  private List<String> comments;

  /**
   * OBX.2
   */
  private String valueType;

  /**
   * OBX.3
   */
  private String id;

  /**
   * OBX.4
   */
  @Nullable
  private String subId;

  /**
   * OBX.5
   */
  private List<String> value;

  /**
   * OBX.6
   */
  @Nullable
  private String valueUnits;

  /**
   * OBX.7
   */
  @Nullable
  private String referenceRange;

  /**
   * OBX.8
   */
  @Nullable
  private List<String> abnormalFlags;

  /**
   * OBX.9
   */
  @Nullable
  private String probability;

  /**
   * OBX.10
   */
  @Nullable
  private String natureOfAbnormalTest;

  /**
   * OBX.11
   */
  private String resultStatus;

  /**
   * OBX.12
   */
  @Nullable
  private String effectiveDateOfLastNormalObservation;

  /**
   * OBX.13
   */
  @Nullable
  private String userDefinedAccessChecks;

  /**
   * OBX.14
   */
  @Nullable
  private String observationDateAndTime;

  /**
   * OBX.15
   */
  @Nullable
  private String producersId;

  /**
   * OBX.16
   */
  @Nullable
  private String responsibleObserver;

  /**
   * OBX.17
   */
  @Nullable
  private List<String> observationMethod;
}
