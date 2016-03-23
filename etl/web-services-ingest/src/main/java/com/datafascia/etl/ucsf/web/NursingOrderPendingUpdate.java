// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import java.util.List;

/**
 * Container for a pending harms update of a nursing order.
 */
public class NursingOrderPendingUpdate {
  private final List<ProcedureRequest> requests;
  private final Encounter encounter;

  /**
   * Container for data pertaining to a nursing order harms update.
   * @param requests The procedure requests for the update.
   * @param encounter The encounter for the update.
   */
  public NursingOrderPendingUpdate(List<ProcedureRequest> requests, Encounter encounter) {
    this.requests = requests;
    this.encounter = encounter;
  }

  public List<ProcedureRequest> getRequests() {
    return requests;
  }

  public Encounter getEncounter() {
    return encounter;
  }
}
