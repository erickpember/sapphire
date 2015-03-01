// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.accumulo;

import lombok.NoArgsConstructor;
import org.apache.accumulo.core.security.ColumnVisibility;

/**
 * Supplies the same visibility expression throughout the lifetime of the application.
 */
@NoArgsConstructor
public class FixedColumnVisibilityPolicy implements ColumnVisibilityPolicy {

  private static final ColumnVisibility COLUMN_VISIBILITY = new ColumnVisibility("System");

  @Override
  public ColumnVisibility getColumnVisibility(String tableName, String columnQualifier) {
    return COLUMN_VISIBILITY;
  }
}
