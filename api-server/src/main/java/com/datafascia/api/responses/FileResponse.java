// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.responses;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to be used to stream file content as response
 */
@Slf4j
public class FileResponse implements StreamingOutput {
  private static final ObjectMapper mapper = Jackson.newObjectMapper();

  private URL resource;

  /**
   * Construct class using passed resource URL
   *
   * @param resource the resource file URL
   */
  public FileResponse(URL resource) {
    this.resource = resource;
  }

  @Override
  public void write(OutputStream output) throws IOException, WebApplicationException {
    log.debug("Streaming file {} contents", resource);
    final JsonGenerator generator = mapper.getFactory().createGenerator(output);
    generator.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

    try (InputStream in = resource.openStream()) {
      byte[] buf = new byte[512];
      while (true) {
        int numRead = in.read(buf);
        if (numRead <= 0) {
          break;
        }
        generator.writeRaw(new String(buf, 0, numRead, "UTF-8"));
      }
    }
    generator.close();
  }
}
