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
package com.datafascia.common.avro;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;

/**
 * Writes an Instant to a long and reads an Instant from a long.
 * The long stores the number of milliseconds since 1970-01-01T00:00:00Z.
 */
public class InstantEncoding extends CustomEncoding<Instant> {

  /**
   * Constructor
   */
  public InstantEncoding() {
    Schema longSchema = Schema.create(Schema.Type.LONG);
    longSchema.addProp("CustomEncoding", InstantEncoding.class.getSimpleName());

    schema = Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.NULL), longSchema));
  }

  @Override
  protected void write(Object datum, Encoder out) throws IOException {
    if (datum == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeLong(((Instant) datum).toEpochMilli());
    }
  }

  @Override
  protected Instant read(Object reuse, Decoder in) throws IOException {
    int unionIndex = in.readIndex();
    switch (unionIndex) {
      case 0:
        in.readNull();
        return null;
      case 1:
        return Instant.ofEpochMilli(in.readLong());
      default:
        throw new AvroTypeException("Unknown union index " + unionIndex);
    }
  }
}
