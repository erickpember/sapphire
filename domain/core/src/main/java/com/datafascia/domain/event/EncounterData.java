// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import com.datafascia.common.avro.InstantEncoding;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.avro.reflect.AvroEncode;

/**
 * Encounter data included when admitting patient.
 */
@AllArgsConstructor @Builder @Data @NoArgsConstructor
public class EncounterData {

  /** institution encounter identifier, for example, value from HL7 field PV1-19 */
  private String identifier;

  @AvroEncode(using = InstantEncoding.class)
  private Instant admitTime;

  @AvroEncode(using = InstantEncoding.class)
  private Instant dischargeTime;
}
