// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.message.ADT_A01;
import ca.uhn.hl7v2.model.v231.message.ADT_A03;
import ca.uhn.hl7v2.model.v231.message.ADT_A04;
import ca.uhn.hl7v2.model.v231.message.ADT_A08;
import com.google.common.collect.Sets;
import java.util.Set;
import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

/**
 * Handles patient admit message.
 */
public class HandleAdmitMessage extends BaseFilter {
  private static final long serialVersionUID = 1L;

  public static final String ID = HandleAdmitMessage.class.getSimpleName();

  private static final Set<Class<?>> RELEVANT_MESSAGE_TYPES = Sets.newHashSet(
      ADT_A01.class,
      ADT_A03.class,
      ADT_A04.class,
      ADT_A08.class);

  @Override
  public boolean isKeep(TridentTuple tuple) {
    Message message = (Message) tuple.getValueByField(F.MESSAGE);

    if (!RELEVANT_MESSAGE_TYPES.contains(message.getClass())) {
      return false;
    }

    return true;
  }
}
