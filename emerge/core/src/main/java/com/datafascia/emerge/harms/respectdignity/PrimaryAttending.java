// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.respectdignity;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.HumanNames;
import com.datafascia.emerge.ucsf.ProviderUtils;
import java.util.Date;

/**
 * Utilities for primary attending.
 */
public class PrimaryAttending {
  /**
   * Gets the current attending provider's period start.
   *
   * @param client The client to use.
   * @param encounterId The encounter to query.
   * @return The starting period of the current attending provider.
   */
  public static Date primaryServiceAttendingPeriod(ClientBuilder client, String encounterId) {
    Encounter.Participant primary = ProviderUtils.getCurrentPrimaryAttending(client, encounterId);
    return primary == null ? null : primary.getPeriod().getStart();
  }

  /**
   * Returns the name of the current primary attending for a given encounter.
   *
   * @param client The client to use.
   * @param encounterId The encounter to query.
   * @return The name of the current primary attending for the encounter.
   */
  public static String primaryAttendingParticipant(ClientBuilder client, String encounterId) {
    Encounter.Participant primary = ProviderUtils.getCurrentPrimaryAttending(client, encounterId);
    if (primary == null) {
      return null;
    }

    Practitioner practitioner
        = client.getPractitionerClient().getPractitioner(primary.getElementSpecificId());
    return practitioner == null ? null : HumanNames.toFullName(practitioner.getName());
  }

}
