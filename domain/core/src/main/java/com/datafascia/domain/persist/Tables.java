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
package com.datafascia.domain.persist;

/**
 * Database table names
 */
public class Tables {

  /** entity table name prefix */
  public static final String ENTITY_PREFIX = "Entity";

  public static final String ENCOUNTER = "Encounter";
  public static final String INGEST_MESSAGE = "IngestMessage";

  // Private constructor disallows creating instances of this class
  private Tables() {
  }
}
