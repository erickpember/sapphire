// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.avro.schemaregistry;

import org.apache.avro.Schema;

/**
 * Avro schema registry. A schema identifier is a 64-bit fingerprint calculated
 * from the schema.
 */
public interface AvroSchemaRegistry extends SchemaRegistry<Long, Schema> {
}
