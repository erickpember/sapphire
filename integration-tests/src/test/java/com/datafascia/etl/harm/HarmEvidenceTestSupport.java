// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import com.datafascia.api.services.ApiTestSupport;
import com.datafascia.common.inject.Injectors;
import com.datafascia.emerge.ucsf.persist.HarmEvidenceRepository;
import com.datafascia.etl.hl7.HL7MessageProcessor;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import javax.inject.Inject;
import org.testng.annotations.BeforeClass;

/**
 * Common implementation for tests of exported data
 */
public abstract class HarmEvidenceTestSupport extends ApiTestSupport {

  protected static final String PATIENT_IDENTIFIER = "97546762";

  @Inject
  private HL7MessageProcessor hl7MessageProcessor;

  @Inject
  protected HarmEvidenceRepository harmEvidenceRepository;

  @Inject
  protected Clock clock;

  @BeforeClass
  public void beforeHarmEvidenceTestSupport() throws Exception {
    Injectors.getInjector().injectMembers(this);
  }

  protected void processMessage(String hl7File) throws IOException {
    URL url = getClass().getResource(hl7File);
    String message = Resources.toString(url, StandardCharsets.UTF_8).replace('\n', '\r');
    hl7MessageProcessor.accept(message.getBytes(StandardCharsets.UTF_8));
  }
}
