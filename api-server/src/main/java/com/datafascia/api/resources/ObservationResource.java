// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.datafascia.api.authenticator.User;
import com.datafascia.dao.ObservationDao;
import com.datafascia.models.Observation;
import com.google.common.base.Strings;
import io.dropwizard.auth.Auth;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Observation API endpoint
 */
@Path("/observation")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ObservationResource {

  private static final String AUTHORIZATIONS = "System";

  private ObservationDao observationDao;

  @Inject
  public ObservationResource(ObservationDao observationDao) {
    this.observationDao = observationDao;
  }

  /**
   * Finds observations for the patient in the time range specified by the open interval
   * [startCaptureTime, endCaptureTime).  That is, each found item must have a time less than the
   * upper bound endpoint.
   *
   * @param patientId
   *     patient identifier
   * @param startCaptureTimeString
   *     minimum capture time to include
   * @param endCaptureTimeString
   *     upper bound of capture time to include
   * @return collection of observations, empty if none found
   */
  @GET
  @Timed
  public Collection<Observation> findObservations(
      @Auth User user,
      @QueryParam("patientId") String patientId,
      @QueryParam("startCaptureTime") String startCaptureTimeString,
      @QueryParam("endCaptureTime") String endCaptureTimeString)
  {
    if (Strings.isNullOrEmpty(patientId) ||
        Strings.isNullOrEmpty(startCaptureTimeString) ||
        Strings.isNullOrEmpty(endCaptureTimeString)) {
      throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }
    Instant startCaptureTime = parseDateTime(startCaptureTimeString);
    Instant endCaptureTime = parseDateTime(endCaptureTimeString);

    return observationDao.findObservationsByPatientId(
        patientId, startCaptureTime, endCaptureTime, AUTHORIZATIONS);
  }

  private Instant parseDateTime(String input) {
    return DateTimeFormatter.ISO_DATE_TIME.parse(input, Instant::from);
  }
}
