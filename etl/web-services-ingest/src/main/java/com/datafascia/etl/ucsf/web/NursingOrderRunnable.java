// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web;

import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceUpdater;
import lombok.extern.slf4j.Slf4j;

/**
 * A thread for handling nursing order harms updates for a given encounter.
 */
@Slf4j
public class NursingOrderRunnable implements Runnable {

  private final NursingOrderPendingUpdate pendingUpdate;
  private final HarmEvidenceUpdater harmEvidenceUpdater;

  /**
   * Creates a thread to handle a nursing order harms update.
   * @param pendingUpdate The data for the update.
   * @param harmEvidenceUpdater The harms updater to use.
   */
  public NursingOrderRunnable(NursingOrderPendingUpdate pendingUpdate,
      HarmEvidenceUpdater harmEvidenceUpdater) {
    this.pendingUpdate = pendingUpdate;
    this.harmEvidenceUpdater = harmEvidenceUpdater;
  }

  @Override
  public void run() {
    NursingOrderPendingUpdate update = pendingUpdate;

    do {
      for (ProcedureRequest request : update.getRequests()) {
        log.info("Updating nursing harms evidence for " + update);
        harmEvidenceUpdater.updateProcedureRequest(request, pendingUpdate.getEncounter());

        // Once it's done, it should check if another is pending for the encounter.
        update = findUpdate();
      }
    } while (update != null);
  }

  private NursingOrderPendingUpdate findUpdate() {
    for (NursingOrderPendingUpdate update: NursingOrdersTransformer.waitingList) {
      if (update.getEncounter().getIdentifierFirstRep().getValue()
          .equals(pendingUpdate.getEncounter().getIdentifierFirstRep().getValue())) {
        NursingOrdersTransformer.waitingList.remove(update);
        return update;
      }
    }
    return null;
  }
}
