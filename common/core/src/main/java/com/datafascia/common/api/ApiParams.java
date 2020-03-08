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
// limitations under the License.'
package com.datafascia.common.api;

import lombok.extern.slf4j.Slf4j;

/**
 * Defines the parameter names for Datafascia API as it is shared among multiple classes
 */
@Slf4j
public class ApiParams {
  /** Starting identifier */
  public static final String START = "start";

  /** Active flag for patient */
  public static final String ACTIVE = "active";

  /** Maximum count for a list */
  public static final String COUNT = "count";

  /** Name of package */
  public static final String PACKAGE = "package";

  /** Patient identifier */
  public static final String PATIENT_ID = "patientId";

  /** Start time */
  public static final String START_TIME = "startTime";

  /** End time */
  public static final String END_TIME = "endTime";
}
