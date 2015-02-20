// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7ingest;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import com.datafascia.message.RawMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Map;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all HL7 messages and writes to queue
 */
@Slf4j @AllArgsConstructor
public class HL7Receiver implements ReceivingApplication {
  private Producer<byte[], byte[]> queue;
  private String queueName;
  private URI institution;
  private URI facility;
  private URI department;
  private URI source;
  private URI payloadType;

  @Override
  public boolean canProcess(Message message) {
    return true;
  }

  @Override
  public Message processMessage(Message hl7, Map<String, Object> metadata)
      throws ReceivingApplicationException, HL7Exception {
    RawMessage msg = new RawMessage();
    msg.setTimestamp(Instant.now());
    msg.setInstitution(institution);
    msg.setFacility(facility);
    msg.setDepartment(department);
    msg.setSource(source);
    msg.setPayloadType(payloadType);
    msg.setPayload(hl7.encode());

    try {
      ObjectMapper mapper = new ObjectMapper();
      KeyedMessage<byte[], byte[]> entry =
          new KeyedMessage<>(queueName, mapper.writeValueAsBytes(msg));
      queue.send(entry);

      return hl7.generateACK();
    } catch (IOException e) {
      throw new HL7Exception(e);
    }
  }
}
