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
package com.datafascia.common.accumulo;

import org.apache.accumulo.core.security.ColumnVisibility;

/**
 * Policy determines the visibility expression to write when writing an entry.
 */
public interface ColumnVisibilityPolicy {

  /**
   * Gets visibility expression to write when writing an entry.
   *
   * @param tableName
   *     table name
   * @param columnQualifier
   *     column qualifier
   * @return visibility expression
   */
  ColumnVisibility getColumnVisibility(String tableName, String columnQualifier);
}
