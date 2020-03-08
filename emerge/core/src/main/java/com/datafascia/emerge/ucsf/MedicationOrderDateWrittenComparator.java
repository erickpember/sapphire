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

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import java.util.Comparator;
import java.util.Date;

/**
 * Compares the date written property of medication order resources.
 */
public class MedicationOrderDateWrittenComparator implements Comparator<MedicationOrder> {

  private static Date toDate(IDatatype value) {
    return ((DateTimeDt) value).getValue();
  }

  @Override
  public int compare(MedicationOrder left, MedicationOrder right) {
    if ((left == null || left.getDateWrittenElement() == null)
        && (right == null || right.getDateWrittenElement() == null)) {
      return 0;
    } else if ((left == null || left.getDateWrittenElement() == null)) {
      return -1;
    } else if ((right == null || right.getDateWrittenElement() == null)) {
      return 1;
    }

    return toDate(left.getDateWrittenElement()).compareTo(toDate(right.getDateWrittenElement()));
  }
}
