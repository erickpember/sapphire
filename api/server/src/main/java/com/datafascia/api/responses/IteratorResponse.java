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
package com.datafascia.api.responses;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to be used to stream items
 *
 * @param <T> the type to stream back
 */
@Slf4j
public class IteratorResponse<T> implements StreamingOutput {
  private static final ObjectMapper mapper = Jackson.newObjectMapper();

  private final Iterator<T> iter;

  /**
   * Construct class using passed iterator
   *
   * @param iter the iteraror
   */
  public IteratorResponse(Iterator<T> iter) {
    this.iter = iter;
  }

  @Override
  public void write(OutputStream output) throws IOException, WebApplicationException {
    final JsonGenerator generator = mapper.getFactory().createGenerator(output);
    generator.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    generator.writeStartArray();
    while (iter.hasNext()) {
      generator.writeObject(iter.next());
    }
    generator.writeEndArray();
    generator.close();
  }
}
