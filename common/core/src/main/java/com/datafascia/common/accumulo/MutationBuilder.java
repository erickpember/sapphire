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

import com.datafascia.common.jackson.DFObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.ColumnVisibility;

/**
 * Builds mutation.
 */
public class MutationBuilder {

  private static final ObjectMapper OBJECT_MAPPER = DFObjectMapper.objectMapper();

  private ColumnVisibilityPolicy columnVisibilityPolicy;
  private String tableName;
  private String columnFamily;
  private Mutation mutation;

  MutationBuilder(String tableName, String rowId, ColumnVisibilityPolicy columnVisibilityPolicy) {
    this.tableName = tableName;
    this.columnVisibilityPolicy = columnVisibilityPolicy;
    mutation = new Mutation(rowId);
  }

  /**
   * Sets column family for write operations put in mutation.
   *
   * @param columnFamily
   *     column family
   * @return builder
   */
  public MutationBuilder columnFamily(String columnFamily) {
    this.columnFamily = columnFamily;
    return this;
  }

  /**
   * Puts entry write operation into mutation.
   *
   * @param columnQualifier
   *     column qualifier
   * @param value
   *     value to write
   * @return builder
   */
  public MutationBuilder put(String columnQualifier, Value value) {
    if (value != null) {
      ColumnVisibility columnVisibility = columnVisibilityPolicy.getColumnVisibility(
          tableName, columnQualifier);
      mutation.put(columnFamily, columnQualifier, columnVisibility, value);
    }

    return this;
  }

  private static byte[] encode(Object value) {
    try {
      return OBJECT_MAPPER.writeValueAsBytes(value);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(String.format("Cannot convert %s to JSON", value), e);
    }
  }

  /**
   * Puts entry write operation into mutation.
   *
   * @param columnQualifier
   *     column qualifier
   * @param value
   *     value to write
   * @return builder
   */
  public MutationBuilder put(String columnQualifier, Object value) {
    if (value != null) {
      put(columnQualifier, new Value(encode(value)));
    }

    return this;
  }

  Mutation build() {
    return mutation;
  }
}
