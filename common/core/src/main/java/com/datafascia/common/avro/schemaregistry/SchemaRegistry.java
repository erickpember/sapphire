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
package com.datafascia.common.avro.schemaregistry;

/**
 * Associates a schema to an identifier. Messages can include the schema
 * identifier so receiving applications can retrieve the schema from the schema
 * registry.
 *
 * @param <K>
 *     schema identifier type
 * @param <S>
 *     schema type
 */
public interface SchemaRegistry<K, S> {

  /**
   * Registers a schema in a topic. If the schema is already registered, then
   * returns the already registered schema identifier, otherwise adds the
   * schema to the registry.
   *
   * @param topic
   *     topic name
   * @param schema
   *     to register
   * @return schema identifier
   */
  K putSchema(String topic, S schema);

  /**
   * Gets a schema for a topic and schema identifier.
   *
   * @param topic
   *     topic name
   * @param schemaId
   *     schema identifier
   * @return schema
   * @throws IllegalArgumentException
   *     if schema not found
   */
  S getSchema(String topic, K schemaId);
}
