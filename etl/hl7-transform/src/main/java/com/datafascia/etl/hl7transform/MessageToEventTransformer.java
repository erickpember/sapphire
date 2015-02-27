// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import ca.uhn.hl7v2.model.Message;
import com.datafascia.domain.event.Event;
import java.net.URI;

/**
 * Transforms message to event.
 */
public interface MessageToEventTransformer {

  /**
   * Gets type of message this transformer accepts.
   *
   * @return message type
   */
  Class<? extends Message> getApplicableMessageType();

  /**
   * Transforms message to event.
   *
   * @param institutionId
   *     institution ID
   * @param facilityId
   *     facility ID
   * @param message
   *     to transform
   * @return event
   */
  Event transform(URI institutionId, URI facilityId, Message message);
}
