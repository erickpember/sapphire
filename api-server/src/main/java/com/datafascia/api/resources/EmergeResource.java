// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.datafascia.api.responses.FileResponse;
import com.google.common.io.Resources;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import lombok.extern.slf4j.Slf4j;

/**
 * Resource object to return the Emerge experimental JSON file.
 */
@Slf4j @Path("/emerge") @Produces(MediaType.APPLICATION_JSON)
public class EmergeResource {
  @GET @Timed
  public FileResponse emerge() {
    return new FileResponse(Resources.getResource("emerge.json"));
  }
}
