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
package com.datafascia.etl.ucsf.web.util;

import com.datafascia.etl.ucsf.web.UcsfWebGetProcessor;
import java.time.Instant;
import java.util.Comparator;
import org.json.simple.JSONObject;

/**
 * A comparator class for medication order JSON blobs.
 */
public class JSONObjectDateTimeOrderedComparator implements Comparator<JSONObject> {
  /**
   * Compares two medication order JSON blob dates.
   *
   * @param o1 The first blob.
   * @param o2 The second blob.
   * @return The comparator result.
   */
  @Override
  public int compare(JSONObject o1, JSONObject o2) {
    String ucsfTime1 = o1.get("DateTimeOrdered").toString();
    Instant dateTimeOrdered1 = UcsfWebGetProcessor.epicDateToInstant(ucsfTime1);
    String ucsfTime2 = o2.get("DateTimeOrdered").toString();
    Instant dateTimeOrdered2 = UcsfWebGetProcessor.epicDateToInstant(ucsfTime2);

    return dateTimeOrdered1.compareTo(dateTimeOrdered2);
  }
}
