// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import com.datafascia.domain.model.IngestMessage;
import java.nio.charset.StandardCharsets;
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

  public static final Fields OUTPUT_FIELDS = new Fields(F.INSTITUTION_ID, F.FACILITY_ID, F.MESSAGE);

  @Inject
  private transient Parser parser;

  @Override
  public void execute(TridentTuple tuple, TridentCollector collector) {
    IngestMessage ingestMessage = (IngestMessage) tuple.getValueByField(F.INGEST_MESSAGE);
    Message message;
    String hl7 = null;
    try {
      hl7 = new String(ingestMessage.getPayload().array(), StandardCharsets.UTF_8);
      message = parser.parse(hl7);
    } catch (HL7Exception e) {
      log.error("Error parsing HL7", e);
      log.debug("This HL7 message could not be parsed: {}", hl7);
      return;
    }
    log.debug("Transform parsed HL7 to message:{}", message);

    collector.emit(
        new Values(ingestMessage.getInstitution(), ingestMessage.getFacility(), message));
  }
}
