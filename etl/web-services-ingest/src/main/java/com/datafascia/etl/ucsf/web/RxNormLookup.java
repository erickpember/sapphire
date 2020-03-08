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
package com.datafascia.etl.ucsf.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Given a rxcui code, looks up the normalized medication name in RxNorm form.
 */
@Slf4j
public class RxNormLookup {
  private final Map<Integer, String> stringCache = new HashMap<>();
  private Connection connection = null;
  private Statement statement = null;
  private ResultSet resultSet = null;
  private final String table;
  private final String username;
  private final String password;
  private final String jdbcUrl;

  /**
   * Create a new RxNorm db.
   *
   * @param jdbcUrl The JDBC URL of the db to connect.
   * @param table The table to connect to.
   * @param username The username to connect with.
   * @param password The password to use.
   * @throws SQLException if there is an error with SQL.
   */
  public RxNormLookup(String jdbcUrl, String table, String username, String password) throws
      SQLException {
    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("JDBC driver not found", e);
    }
    this.username = username;
    this.password = password;
    this.jdbcUrl = jdbcUrl;
    this.table = table;
    connect();
  }

  private void connect() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
    connection = DriverManager.getConnection(jdbcUrl, username, password);

    if (statement != null && !statement.isClosed()) {
      statement.close();
    }
    statement = connection.createStatement();
  }

  /**
   * Get the associated string for an Rx CUI.
   *
   * @param rxcui The cui to lookup.
   * @return The string for the Rx.
   * @throws SQLException if there is an error with SQL.
   */
  public String getRxString(int rxcui) throws SQLException {
    if (!connection.isValid(0) || connection.isClosed() || statement.isClosed()) {
      connect();
    }

    String rxStr = stringCache.get(rxcui);
    if (rxStr != null) {
      return rxStr;
    }

    resultSet = statement.executeQuery("SELECT * FROM " + table + " where RXCUI = " + rxcui
        + " and TTY = 'SCD' and SAB = 'RXNORM';"
    );
    if (resultSet.next()) {
      rxStr = resultSet.getString("STR");
    }
    resultSet.close();

    stringCache.put(rxcui, rxStr);
    return rxStr;
  }

  /**
   * Close the connection to SQL.
   *
   * @throws SQLException if there is an error with SQL.
   */
  public void shutdown() throws SQLException {
    statement.close();
    connection.close();
  }
}
