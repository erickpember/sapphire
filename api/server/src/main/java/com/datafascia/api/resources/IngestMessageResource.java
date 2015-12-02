// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
    TarArchiveOutputStream tarOutput = new TarArchiveOutputStream(new GZIPOutputStream(output));

    int count = 0;
    List<IngestMessage> messages = ingestMessageRepository.list(timeLower, 10000);
    for (IngestMessage message : messages) {
      String fileName = String.format("%04d", count++);
      TarArchiveEntry file = new TarArchiveEntry(fileName);
      byte[] payload = message.getPayload().array();
      file.setSize(payload.length);
      tarOutput.putArchiveEntry(file);
      tarOutput.write(payload);
      tarOutput.closeArchiveEntry();
    }

    tarOutput.close();
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
