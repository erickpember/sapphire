// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import java.io.IOException;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.admin.TableOperations;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.TableNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to allow bulk import of data into an Accumulo instance.
 */
@Slf4j
public class AccumuloImport {
  /**
   * Import the files from the directory into the Accumulo instance
   *
   * @param connect the connector object for the Accumulo instance
   * @param tableName the table name to import data into. The table will be created if non-existent
   * @param path the directory to import files from
   * @param failure the directory to write failure to
   */
  public static void importData(Connector connect, String tableName, String path, String failure)
      throws AccumuloException, AccumuloSecurityException, IOException, TableExistsException,
      TableNotFoundException {
    log.info("Importing data from: " + path + ", into Accumulo table: " + tableName);
    TableOperations tableOps = connect.tableOperations();
    createTable(tableOps, tableName);
    tableOps.importDirectory(tableName, path, failure, false);
  }

  /**
   * Check and create table
   *
   * @param tableOps the table operations object
   * @param tableName the table name to import data into. The table will be created if non-existent
   */
  private static void createTable(TableOperations tableOps, String tableName) throws
      AccumuloException, AccumuloSecurityException, TableExistsException {
    if (!tableOps.exists(tableName)) {
      tableOps.create(tableName);
    }
  }
}
