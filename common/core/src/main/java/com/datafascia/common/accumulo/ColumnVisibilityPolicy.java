// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
