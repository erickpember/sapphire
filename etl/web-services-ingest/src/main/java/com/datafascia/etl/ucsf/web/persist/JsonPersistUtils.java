// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web.persist;

import com.datafascia.etl.ucsf.web.MedAdminDiffListener;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.hadoop.io.Text;

/**
 * Methods related to storing and retrieving Json.
 */
@Slf4j
public class JsonPersistUtils {
  /**
   * Persists the Json representation of an incoming Medication Administration.
   *
   * @param type      Type of Medication Administration.
   * @param id        Accumulo row id.
   * @param textValue Json Medication Adminsitration payload.
   * @param connector Database connector.
   * @param tableName Destination database table.
   * @throws          MutationsRejectedException Failure to write to db.
   * @throws          TableNotFoundException Table not found.
   */
  public static void persistJson(MedAdminDiffListener.ElementType type, String id, String textValue,
      Connector connector, String tableName)
      throws MutationsRejectedException, TableNotFoundException {
    Text rowID = new Text(type.toString() + "-" + id);
    Text colFam = new Text("Ucsf");
    Text colQual = new Text("MedAdmin");
    ColumnVisibility colVis = new ColumnVisibility("System");
    long timestamp = System.currentTimeMillis();

    Value value = null;
    try {
      value = new Value(textValue.getBytes("UTF8"));
    } catch (UnsupportedEncodingException ex) {
      log.error("UTF8 is not supported?", ex);
    }

    Mutation mutation = new Mutation(rowID);
    mutation.put(colFam, colQual, colVis, timestamp, value);

    BatchWriter writer = connector.createBatchWriter(tableName, new BatchWriterConfig());
    writer.addMutation(mutation);
    writer.close();
  }

  /**
   * Retrieves Json representation of a Medication Administration from the database.
   *
   * @param type           Type of Medication Administration.
   * @param id             Row ID of the record.
   * @param connector      Database connection.
   * @param tableName      Source table in database.
   * @param authorizations Database access authorization.
   * @return               Json representation of a Medication Administration.
   * @throws               TableNotFoundException Table not found.
   */
  public static String fetchJson(MedAdminDiffListener.ElementType type, String id,
      Connector connector,
      String tableName, Authorizations authorizations)
      throws TableNotFoundException {
    Text rowId = new Text(type.toString() + "-" + id);
    Scanner scan = connector.createScanner(tableName, authorizations);

    scan.setRange(new Range(rowId, rowId));
    for (Map.Entry<Key, Value> entry : scan) {
      try {
        return new String(entry.getValue().get(), "UTF8");
      } catch (UnsupportedEncodingException ex) {
        log.error("UTF8 is not supported?", ex);
      }
    }
    return null;
  }
}
