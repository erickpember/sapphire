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
