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
package com.datafascia.api.resources;

import com.datafascia.domain.model.IngestMessage;
import com.datafascia.domain.persist.IngestMessageRepository;
import com.google.common.base.Strings;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPOutputStream;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

/**
 * Resource providing ingest messages.
 */
@Path("/ingest/message")
@Produces(MediaType.APPLICATION_OCTET_STREAM)
@Slf4j
public class IngestMessageResource {

  @Inject
  private IngestMessageRepository ingestMessageRepository;

  @Context
  private HttpServletResponse response;

  private Optional<Instant> parseDateTime(String input) {
    if (Strings.isNullOrEmpty(input)) {
      return Optional.empty();
    }

    try {
      Instant instant = Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(input));
      return Optional.of(instant);
    } catch (DateTimeParseException e) {
      throw new WebApplicationException(
          String.format("Invalid date time [%s]", input), Response.Status.BAD_REQUEST);
    }
  }

  private Instant getDefaultTimeLower() {
    return Instant.now().minus(1, ChronoUnit.HOURS);
  }

  private void generateArchive(Instant timeLower, OutputStream output) throws IOException {
    GZIPOutputStream gzipOutput = new GZIPOutputStream(output);
    TarArchiveOutputStream tarOutput = new TarArchiveOutputStream(gzipOutput);

    int count = 0;
    List<IngestMessage> messages = ingestMessageRepository.list(timeLower, 100_000);
    for (IngestMessage message : messages) {
      String fileName = String.format("%05d", count++);
      TarArchiveEntry file = new TarArchiveEntry(fileName);
      file.setModTime(Date.from(message.getTimestamp()));
      byte[] payload = message.getPayload().array();
      file.setSize(payload.length);
      tarOutput.putArchiveEntry(file);
      tarOutput.write(payload);
      tarOutput.closeArchiveEntry();
    }

    tarOutput.finish();
    tarOutput.flush();
    gzipOutput.finish();
    gzipOutput.flush();
  }

  /**
   * Lists ingest messages.
   *
   * @param timeMin
   *     ingest time lower bound (inclusive)
   * @return harm evidence bundle
   */
  @GET
  public StreamingOutput list(@QueryParam("timeMin") String timeMin) {
    Instant timeLower = parseDateTime(timeMin).orElse(getDefaultTimeLower());

    response.setHeader("Content-Disposition", "attachment; filename=message.tar.gz");
    return new StreamingOutput() {
      @Override
      public void write(OutputStream output) throws IOException, WebApplicationException {
        generateArchive(timeLower, output);
      }
    };
  }
}
