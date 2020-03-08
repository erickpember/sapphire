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

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.admin.TableOperations;

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
   *
   * @throws java.io.IOException reading or writing error to Accumulo
   * @throws org.apache.accumulo.core.client.AccumuloException underlying Accumulo calls
   * @throws org.apache.accumulo.core.client.AccumuloSecurityException underlying Accumulo calls
   * @throws org.apache.accumulo.core.client.TableExistsException table already exists
   * @throws org.apache.accumulo.core.client.TableNotFoundException this should not be thrown as
   *     the table is being created
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
   *
   * @throws org.apache.accumulo.core.client.AccumuloException underlying Accumulo calls
   * @throws org.apache.accumulo.core.client.AccumuloSecurityException underlying Accumulo calls
   * @throws org.apache.accumulo.core.client.TableExistsException table already exists
   */
  private static void createTable(TableOperations tableOps, String tableName) throws
      AccumuloException, AccumuloSecurityException, TableExistsException {
    if (!tableOps.exists(tableName)) {
      tableOps.create(tableName);
    }
  }

  private AccumuloImport() {
  }
}
