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
 * Supplies the same visibility expression throughout the lifetime of the application.
 */
public class FixedColumnVisibilityPolicy implements ColumnVisibilityPolicy {

  private final ColumnVisibility columnVisibility;

  /**
   * Constructs policy that supplies given visibility expression.
   *
   * @param columnVisibility
   *     visibility expression
   */
  public FixedColumnVisibilityPolicy(ColumnVisibility columnVisibility) {
    this.columnVisibility = columnVisibility;
  }

  /**
   * Constructs policy that supplies empty visibility expression.
   */
  public FixedColumnVisibilityPolicy() {
    this(new ColumnVisibility());
  }

  @Override
  public ColumnVisibility getColumnVisibility(String tableName, String columnQualifier) {
    return columnVisibility;
  }
}
