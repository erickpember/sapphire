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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to be used to stream file content as response
 */
@Slf4j
public class FileResponse implements StreamingOutput {
  private static final ObjectMapper mapper = Jackson.newObjectMapper();

  private final URL resource;

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
