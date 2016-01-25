// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.EncounterMessageRepository;
import com.datafascia.etl.UrlToFetchedTimeMap;
import com.datafascia.etl.hl7.HL7MessageProcessor;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import javax.inject.Inject;

/**
 * Replays messages for the encounter.
 */
public class ReplayMessages implements Consumer<String> {

  @Inject
  private UrlToFetchedTimeMap urlToFetchedTimeMap;

  @Inject
  private EncounterMessageRepository encounterMessageRepository;

  @Inject
  private HL7MessageProcessor hl7MessageProcessor;

  private void processMessage(byte[] bytes) {
    String hl7 = new String(bytes, StandardCharsets.UTF_8);
    hl7MessageProcessor.accept(hl7);
  }

  /**
   * Replays messages for the encounter. Also forces processors that retrieve data from web services
   * to refresh all data.
   *
   * @param encounterIdentifier
   *     encounter identifier
   */
  public void accept(String encounterIdentifier) {
    // Force web service ingest processors to refresh all data.
    urlToFetchedTimeMap.clear();

    Id<Encounter> encounterId = Id.of(encounterIdentifier);
    encounterMessageRepository.findByEncounterId(encounterId)
        .stream()
        .forEach(message -> processMessage(message.getPayload().array()));
  }
}
