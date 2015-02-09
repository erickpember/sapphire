// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.responses;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to be used to stream items
 *
 * @param <T> the type to stream back
 */
@Slf4j
public class IteratorResponse<T> implements StreamingOutput {
  private static final ObjectMapper mapper = Jackson.newObjectMapper();

  private Iterator<T> iter;

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
