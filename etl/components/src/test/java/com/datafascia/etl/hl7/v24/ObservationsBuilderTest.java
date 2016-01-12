// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7.v24;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import com.datafascia.etl.hl7.HL7MessageProcessorTestSupport;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.inject.Inject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


/**
 * {@link ObservationsBuilder} test
 */
public class ObservationsBuilderTest extends HL7MessageProcessorTestSupport {

  private static final String OBX_PATH_PATTERN
      = "/PATIENT_RESULT/ORDER_OBSERVATION(%1$d)/OBSERVATION(%2$d)/OBX";
  private static final String NTE_PATH_PATTERN
      = "/PATIENT_RESULT/ORDER_OBSERVATION(%1$d)/OBSERVATION(%2$d)/NTE(%3$d)";

  @Inject
  private Parser parser;

  @Test
  public void should_read_ADT_A03() throws Exception {
    ObservationsBuilder observationsBuilder = new ObservationsBuilder(
        readMessage("ADT_A03.hl7"), "/OBX(%2$d)", null);
    assertTrue(observationsBuilder.hasObservations());
    List<Observation> observations = observationsBuilder.toObservations();
    assertEquals(observations.size(), 2);
  }

  @Test
  public void should_read_ORU_R01() throws Exception {
    ObservationsBuilder observationsBuilder = new ObservationsBuilder(
        readMessage("ORU_R01.hl7"), OBX_PATH_PATTERN, NTE_PATH_PATTERN);
    assertTrue(observationsBuilder.hasObservations());
    List<Observation> observations = observationsBuilder.toObservations();
    assertEquals(observations.size(), 3);
  }

  protected Message readMessage(String hl7File) throws HL7Exception, IOException {
    URL url = Resources.getResource(getClass(), hl7File);
    String hl7 = Resources.toString(url, StandardCharsets.UTF_8).replace('\n', '\r');

    return parser.parse(hl7);
  }
}
