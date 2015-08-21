// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
