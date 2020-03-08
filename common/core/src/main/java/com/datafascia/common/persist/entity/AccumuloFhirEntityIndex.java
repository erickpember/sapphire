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
package com.datafascia.common.persist.entity;

import com.datafascia.common.accumulo.AccumuloTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

/**
 * Maps search term to entity IDs.
 *
 * @param <E>
 *     entity type
 */
public class AccumuloFhirEntityIndex<E> implements FhirEntityIndex<E> {

  private static final String BIN_ROW_ID = "0000";
  private static final Value EMPTY_VALUE = new Value();

  private String indexTableName;
  private Function<E, String> termSupplier;
  private AccumuloTemplate accumuloTemplate;
  private BatchWriter writer;
  private boolean checkedEmpty;

  /**
   * Constructor
   *
   * @param indexName
   *     index name
   * @param termSupplier
   *     function to extract search term from entity
   * @param connector
   *     Accumulo connector
   * @param accumuloTemplate
   *     data access operations template
   */
  public AccumuloFhirEntityIndex(
      String indexName,
      Function<E, String> termSupplier,
      Connector connector,
      AccumuloTemplate accumuloTemplate) {

    this.indexTableName = indexName;
    this.termSupplier = termSupplier;
    this.accumuloTemplate = accumuloTemplate;

    accumuloTemplate.createTableIfNotExist(indexTableName);

    try {
      writer = connector.createBatchWriter(indexTableName, new BatchWriterConfig());
    } catch (TableNotFoundException e) {
      throw new IllegalStateException("Table " + indexTableName + " not found", e);
    }
  }

  @Override
  public synchronized boolean isEmpty() {
    if (checkedEmpty) {
      // Check for empty index only once.
      return false;
    }
    checkedEmpty = true;

    Scanner scanner = accumuloTemplate.createScanner(indexTableName);
    scanner.setRange(new Range());
    Iterator<Map.Entry<Key, Value>> iterator = scanner.iterator();
    boolean empty = !iterator.hasNext();
    scanner.close();
    return empty;
  }

  private void write(Mutation mutation) {
    if (mutation.size() > 0) {
      try {
        writer.addMutation(mutation);
        writer.flush();
      } catch (MutationsRejectedException e) {
        throw new IllegalStateException("Write failed", e);
      }
    }
  }

  @Override
  public void save(EntityId entityId, E oldObject, E newObject) {
    Mutation mutation = new Mutation(BIN_ROW_ID);

    String entityRowId = AccumuloFhirEntityStore.toRowId(entityId);
    String newTerm = termSupplier.apply(newObject);

    if (oldObject != null) {
      String oldTerm = termSupplier.apply(oldObject);
      if (oldTerm != null && !oldTerm.equals(newTerm)) {
        mutation.putDelete(oldTerm, entityRowId);
      }
    }

    if (newTerm != null) {
      mutation.put(newTerm, entityRowId, EMPTY_VALUE);
    }

    write(mutation);
  }

  @Override
  public List<String> search(String term) {
    List<String> entityIds = new ArrayList<>();

    Scanner scanner = accumuloTemplate.createScanner(indexTableName);
    scanner.setRange(Range.exact(BIN_ROW_ID, term));
    for (Map.Entry<Key, Value> entry : scanner) {
      entityIds.add(entry.getKey().getColumnQualifier().toString());
    }
    scanner.close();

    return entityIds;
  }

  @Override
  public void delete(EntityId entityId, E object) {
    Mutation mutation = new Mutation(BIN_ROW_ID);

    String entityRowId = AccumuloFhirEntityStore.toRowId(entityId);
    String term = termSupplier.apply(object);
    if (term != null) {
      mutation.putDelete(term, entityRowId);
    }

    write(mutation);
  }
}
