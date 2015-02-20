// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import com.datafascia.domain.model.IngestMessage;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * Parses message.
 */
@Slf4j
public class ParseMessage extends BaseFunction {
  private static final long serialVersionUID = 1L;

  public static final String ID = ParseMessage.class.getSimpleName();

  public static final Fields OUTPUT_FIELDS = new Fields(F.MESSAGE);

  @Inject
  private transient Parser parser;

  @Override
  public void execute(TridentTuple tuple, TridentCollector collector) {
    IngestMessage ingestMessage = (IngestMessage) tuple.getValueByField(F.INGEST_MESSAGE);

    Message message;
    try {
      message = parser.parse(ingestMessage.getPayload());
    } catch (HL7Exception e) {
      log.error("Error parsing HL7", e);
      return;
    }

    collector.emit(new Values(message));
  }
}
