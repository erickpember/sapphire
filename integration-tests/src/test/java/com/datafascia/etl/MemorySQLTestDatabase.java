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
package com.datafascia.etl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A simple in-memory SQL database for testing. Populated with known mappings of SCDs to drug names.
 */
public class MemorySQLTestDatabase {
  public static final String JDBCURL =
      "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
  public static final String USER = "testuser";
  public static final String PASSWORD = "testpassword";
  public static final String TABLE = "RXNCONSO";

  public static void populateDb() throws SQLException {
    Connection connection = DriverManager.getConnection(JDBCURL, USER, PASSWORD);
    connection.setAutoCommit(false);
    try (Statement statement = connection.createStatement()) {
      statement.execute("CREATE TABLE " + TABLE + "(RXCUI varchar(75), TTY varchar(75), " +
          "SAB varchar(75), STR varchar(75))");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('106258', 'SCD', " +
          "'RXNORM', 'Hydrocortisone 10 MG/ML Topical Cream')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('197590', 'SCD', " +
          "'RXNORM', 'Diazepam 2 MG Oral Tablet')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('197591', 'SCD', " +
          "'RXNORM', 'Diazepam 5 MG Oral Tablet')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('197807', 'SCD', " +
          "'RXNORM', 'Ibuprofen 800 MG Oral Tablet')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('206977', 'SCD', " +
          "'RXNORM', 'Fentanyl 0.05 MG/ML Injectable Solution')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('241999', 'SCD', " +
          "'RXNORM', 'Epoetin Alfa 20000 UNT/ML Injectable Solution')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('307696', 'SCD', " +
          "'RXNORM', 'Acetaminophen 80 MG Chewable Tablet')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('308191', 'SCD', " +
          "'RXNORM', 'Amoxicillin 500 MG Oral Capsule')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('309778', 'SCD', " +
          "'RXNORM', 'Glucose 50 MG/ML Injectable Solution')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('309806', 'SCD', " +
          "'RXNORM', 'Glucose 50 MG/ML / Sodium Chloride 0.0769 MEQ/ML Injectable Solution')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('309843', 'SCD', " +
          "'RXNORM', 'Diazepam 1 MG/ML Oral Solution')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('309845', 'SCD', " +
          "'RXNORM', 'Diazepam 5 MG/ML Injectable Solution')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('311702', 'SCD', " +
          "'RXNORM', 'Midazolam 5 MG/ML Injectable Solution')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('313002', 'SCD', " +
          "'RXNORM', 'Sodium Chloride 0.154 MEQ/ML Injectable Solution')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('702050', 'SCD', " +
          "'RXNORM', 'carbamide peroxide 65 MG/ML Otic Solution')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('993770', 'SCD', " +
          "'RXNORM', 'Acetaminophen 300 MG / Codeine Phosphate 15 MG Oral Tablet')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('1148399', 'SCD', " +
          "'RXNORM', '8 HR Acetaminophen 650 MG Extended Release Oral Tablet')");
      statement.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('1292740', 'SCD', " +
          "'RXNORM', 'Dopamine Hydrochloride 0.8 MG/ML Injectable Solution')");
    }
    connection.commit();
  }
}
