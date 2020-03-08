// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
