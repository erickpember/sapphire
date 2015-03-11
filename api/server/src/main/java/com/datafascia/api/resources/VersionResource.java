// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.datafascia.common.api.ApiParams;
import com.datafascia.models.Version;
import com.google.common.base.Optional;
import java.util.concurrent.atomic.AtomicLong;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

/**
 * Resource object to model detailed information on the server itself, including version etc. Should
 * be useful just for running quick test
 */
@Slf4j @Path("/version") @Produces(MediaType.APPLICATION_JSON)
public class VersionResource {
  /** Default package */
  public static final String DEFAULT_PACKAGE = "com.datafascia.api.services";

  private final AtomicLong counter;

  /**
   * default constructor
   */
  public VersionResource() {
    this.counter = new AtomicLong();
  }

  /**
   * @param packageName the name of package to return version of
   *
   * @return version information associated with package
   */
  @GET @Timed
  public Version version(@QueryParam(ApiParams.PACKAGE) Optional<String> packageName) {
    return new Version(counter.incrementAndGet(), packageName.or(DEFAULT_PACKAGE));
  }
}
