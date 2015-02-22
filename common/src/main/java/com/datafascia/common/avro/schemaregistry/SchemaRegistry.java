// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
