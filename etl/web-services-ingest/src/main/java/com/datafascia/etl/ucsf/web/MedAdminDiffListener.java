// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web;

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;

/**
 * A callback for diffs found in medications.
 */
public interface MedAdminDiffListener {
  /**
   * The type of diff found.
   */
  enum ElementType {
    ORDER, ADMIN
  }

  /**
   * A diff in the medication data.
   *
   * @param type The element the diff was in.
   * @param field The field that changed.
   * @param id The ID of what was changed.
   * @param oldData The old value.
   * @param newData The new value.
   */
  public void diff(ElementType type, String field, String id, String oldData, String newData);

  /**
   * New order found.
   *
   * @param order The data itself.
   */
  public void newOrder(MedicationOrder order);

  /**
   * New admin found.
   *
   * @param admin The data itself.
   */
  public void newAdmin(MedicationAdministration admin);
}
