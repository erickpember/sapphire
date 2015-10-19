// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.datafascia.emerge.ucsf.EmergeDataFeed;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.HarmEvidenceBundle;
import com.datafascia.emerge.ucsf.persist.HarmEvidenceRepository;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Resource providing the Emerge data.
 */
@Path("/emerge/harmEvidence")
@Produces(MediaType.APPLICATION_JSON)
public class HarmEvidenceResource {

  @Inject
  private HarmEvidenceRepository harmEvidenceRepository;

  /**
   * Lists all harm evidence records.
   *
   * @return harm evidence bundle
   */
  @GET
  @Timed
  public HarmEvidenceBundle list() {
    List<HarmEvidence> records = harmEvidenceRepository.list();

    EmergeDataFeed emergeDataFeed = new EmergeDataFeed()
        .withTimeOfDataFeed(new Date())
        .withEmergePatients(records);

    HarmEvidenceBundle bundle = new HarmEvidenceBundle()
        .withEmergeDataFeed(emergeDataFeed);
    return bundle;
  }
}
