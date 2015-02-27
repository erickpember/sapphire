// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event which changes state in the system.
 */
@AllArgsConstructor @Builder @Data @NoArgsConstructor
public class Event {

  private URI institutionId;
  private URI facilityId;
  private EventType type;
  private EventData data;
}
