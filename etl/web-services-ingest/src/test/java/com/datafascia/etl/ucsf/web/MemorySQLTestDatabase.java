// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact

package com.datafascia.etl.ucsf.web;

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
    Statement stmt = connection.createStatement();
    stmt.execute("CREATE TABLE " + TABLE + "(RXCUI varchar(75), TTY varchar(75), " +
        "SAB varchar(75), STR varchar(75))");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('106258', 'SCD', " +
        "'RXNORM', 'Hydrocortisone 10 MG/ML Topical Cream')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('197590', 'SCD', " +
        "'RXNORM', 'Diazepam 2 MG Oral Tablet')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('197591', 'SCD', " +
        "'RXNORM', 'Diazepam 5 MG Oral Tablet')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('197807', 'SCD', " +
        "'RXNORM', 'Ibuprofen 800 MG Oral Tablet')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('206977', 'SCD', " +
        "'RXNORM', 'Fentanyl 0.05 MG/ML Injectable Solution')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('241999', 'SCD', " +
        "'RXNORM', 'Epoetin Alfa 20000 UNT/ML Injectable Solution')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('307696', 'SCD', " +
        "'RXNORM', 'Acetaminophen 80 MG Chewable Tablet')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('308191', 'SCD', " +
        "'RXNORM', 'Amoxicillin 500 MG Oral Capsule')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('309778', 'SCD', " +
        "'RXNORM', 'Glucose 50 MG/ML Injectable Solution')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('309806', 'SCD', " +
        "'RXNORM', 'Glucose 50 MG/ML / Sodium Chloride 0.0769 MEQ/ML Injectable Solution')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('309843', 'SCD', " +
        "'RXNORM', 'Diazepam 1 MG/ML Oral Solution')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('309845', 'SCD', " +
        "'RXNORM', 'Diazepam 5 MG/ML Injectable Solution')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('311702', 'SCD', " +
        "'RXNORM', 'Midazolam 5 MG/ML Injectable Solution')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('313002', 'SCD', " +
        "'RXNORM', 'Sodium Chloride 0.154 MEQ/ML Injectable Solution')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('702050', 'SCD', " +
        "'RXNORM', 'carbamide peroxide 65 MG/ML Otic Solution')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('993770', 'SCD', " +
        "'RXNORM', 'Acetaminophen 300 MG / Codeine Phosphate 15 MG Oral Tablet')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('1148399', 'SCD', " +
        "'RXNORM', '8 HR Acetaminophen 650 MG Extended Release Oral Tablet')");
    stmt.execute("INSERT INTO " + TABLE + "(RXCUI, TTY, SAB, STR)VALUES('1292740', 'SCD', " +
        "'RXNORM', 'Dopamine Hydrochloride 0.8 MG/ML Injectable Solution')");
    stmt.close();
    connection.commit();
  }
}
