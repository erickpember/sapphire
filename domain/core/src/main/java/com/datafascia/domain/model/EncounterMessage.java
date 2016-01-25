// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.nio.ByteBuffer;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HL7 message for an encounter.
 */
@Data
@NoArgsConstructor
public class EncounterMessage {

  private String id;
  private ByteBuffer payload;
}
