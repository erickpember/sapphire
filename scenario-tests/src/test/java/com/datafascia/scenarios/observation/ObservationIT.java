// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.scenarios.observation;

import com.datafascia.api.client.DatafasciaApi;
import com.datafascia.api.client.DatafasciaApiBuilder;
import com.datafascia.domain.model.Observation;
import com.datafascia.shell.Main;
import com.google.common.io.Resources;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Acceptance test for observation processing.
 */
@Slf4j
public class ObservationIT {

  private static final String INGEST_HL7_COMMAND = "ingest-hl7-files";
  private static final String PATIENT_ID = "urn:df-patientId-1:87552037";

  private static String mllpHost;
  private static String mllpPort;
  private static DatafasciaApi apiClient;

  @BeforeClass
  public static void beforeClass() throws Exception {
    mllpHost = System.getProperty("mllpHost");
    mllpPort = System.getProperty("mllpPort");
    log.info("MLLP host: {}, port: {}", mllpHost, mllpPort);

    String apiURI = System.getProperty("apiURI");
    String user = System.getProperty("user");
    String password = System.getProperty("password");
    log.info("API URI: {}", apiURI);
    log.info("User name: {}, password: {}", user, password);

    apiClient = DatafasciaApiBuilder.endpoint(new URI(apiURI), user, password);
  }

  private void ingestHL7Message(String filename, int delaySeconds) throws Exception {
    String resourceFile = Resources.getResource(ObservationIT.class, filename).getPath();
    String[] shellArgs = {
        INGEST_HL7_COMMAND, "--files", resourceFile, "-h", mllpHost, "-p", mllpPort };
    Main.main(shellArgs);
    TimeUnit.SECONDS.sleep(delaySeconds);
  }

  @Test
  public void should_save_observation() throws Exception {
    // Given a patient was admitted
    apiClient.deleteEncounters(PATIENT_ID);
    apiClient.deletePatient(PATIENT_ID);
    ingestHL7Message("ADT_A01.hl7", 3);

    // When an observation is ingested
    ingestHL7Message("ORU_R01.hl7", 3);

    // Then the observation is saved
    List<Observation> observations = apiClient.findObservations(PATIENT_ID, null, null);
    assertEquals(observations.size(), 5);

    for (Observation observation : observations) {
      switch (observation.getName().getCodings().get(0)) {
        case "%AS^ANTIBODY SCREEN^SQ_LABP^890-4^Bld gp Ab Scn SerPl Ql^LN":
          assertEquals(observation.getValue().getString(), "NEG");
          break;
      }
    }
  }
}
