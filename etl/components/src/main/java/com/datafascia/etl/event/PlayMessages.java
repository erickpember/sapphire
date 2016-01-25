// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.model.EncounterMessage;
import com.datafascia.domain.persist.EncounterMessageRepository;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.etl.hl7.HL7MessageProcessor;
import com.google.common.base.Strings;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Plays pending HL7 messages for the encounter.
 */
@Slf4j
public class PlayMessages implements Consumer<String> {

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private EncounterMessageRepository messageRepository;

  @Inject
  private HL7MessageProcessor hl7MessageProcessor;

  /**
   * Sets last processsed message ID for in-progress encounters.
   */
  public void initializeLastProcessedMessageIds() {
    encounterRepository.list(Optional.of(EncounterStateEnum.IN_PROGRESS))
        .stream()
        .forEach(encounter ->
            messageRepository.initializeLastProcessedMessageId(
                Id.of(encounter.getId().getIdPart())));
  }

  private void processMessage(byte[] bytes) {
    String hl7 = new String(bytes, StandardCharsets.UTF_8);

    String msh = hl7.split("\r", 2)[0];
    log.info("Processing message {}", msh);

    hl7MessageProcessor.accept(hl7);
  }

  /**
   * Plays pending HL7 messages for the encounter.
   *
   * @param encounterIdentifier
   *     encounter identifier
   */
  public void accept(String encounterIdentifier) {
    Id<Encounter> encounterId = Id.of(encounterIdentifier);
    List<EncounterMessage> messages = messageRepository.findByEncounterId(encounterId);
    log.info("Processing {} messages for encounter {}", messages.size(), encounterIdentifier);

    String lastProcessedMessageId = null;
    for (EncounterMessage message : messages) {
      processMessage(message.getPayload().array());
      lastProcessedMessageId = message.getId();
    }

    if (!Strings.isNullOrEmpty(lastProcessedMessageId)) {
      messageRepository.saveLastProcessedMessageId(encounterId, lastProcessedMessageId);
    }
  }
}
