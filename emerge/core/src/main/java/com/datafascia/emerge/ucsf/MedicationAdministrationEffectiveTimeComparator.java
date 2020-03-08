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
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import java.util.Comparator;
import java.util.Date;

/**
 * Compares the effective time property of medication administrations.
 */
public class MedicationAdministrationEffectiveTimeComparator implements
    Comparator<MedicationAdministration> {

  private static int compare(Date left, Date right) {
    if (left == null && right == null) {
      return 0;
    } else if (left == null) {
      return -1;
    } else if (right == null) {
      return 1;
    }

    return left.compareTo(right);
  }


  @Override
  public int compare(MedicationAdministration left, MedicationAdministration right) {
    if (left == null && right == null) {
      return 0;
    } else if (left == null) {
      return -1;
    } else if (right == null) {
      return 1;
    }

    return compare(MedicationAdministrationUtils.getEffectiveTime(left),
        MedicationAdministrationUtils.getEffectiveTime(right));
  }
}
