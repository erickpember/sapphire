// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform.v24;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;
import com.datafascia.domain.event.AddObservationsData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.EventType;
import com.datafascia.domain.event.ObservationData;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Tests parsing of OBX segments from an ORU message to events.
 */
@Slf4j
public class ORU_R01_TransformerTest {

  private static final String HL7_OBX_FILE = "ORU_R01_OBXtest.hl7";

  private static Parser parser;

  @BeforeClass
  public static void beforeClass() {
    HapiContext context = new DefaultHapiContext();
    context.setModelClassFactory(new CanonicalModelClassFactory("2.4"));

    parser = context.getPipeParser();
  }

  /**
   * Test of getApplicableMessageType method, of class ORU_R01_Transformer.
   */
  @Test
  public void testGetApplicableMessageType() {
    ORU_R01_Transformer transformer = new ORU_R01_Transformer();

    assertEquals(transformer.getApplicableMessageType(), ORU_R01.class);
  }

  /**
   * Test of transform method, of class ORU_R01_Transformer.
   *
   * @throws java.io.IOException Failure to find test file.
   * @throws ca.uhn.hl7v2.HL7Exception Failure to parse HL7.
   * @throws java.net.URISyntaxException Failure to create URI.
   */
  @Test
  public void testTransform() throws IOException, HL7Exception, URISyntaxException {
    String payload = Resources.toString(Resources.getResource(HL7_OBX_FILE),
        StandardCharsets.UTF_8).replace('\n', '\r');

    Message hapiMsg = parser.parse(payload);

    ORU_R01_Transformer transformer = new ORU_R01_Transformer();
    List<Event> events = transformer.transform(new URI("http://institution"),
        new URI("http://facility"), hapiMsg);

    boolean obxCompleted = false;

    for (Event event : events) {
      log.debug("Transform transformed Message to Event. institutionId:{}, facilityId:{},"
          + " type:{}, data:{}", event.getInstitutionId(), event.getFacilityId(), event.getType(),
          event.getData());

      if (event.getType().equals(EventType.OBSERVATIONS_ADD)) {
        assertNotNull(event.getData());
        AddObservationsData addObservationsData = (AddObservationsData) event.getData();
        List<ObservationData> obxList = addObservationsData.getObservations();
        assertEquals(obxList.size(), 2, "Wrong size of observations collected.");
        ObservationData observation = obxList.get(0);

        // A more thorough test of hl7 file parsing to event is done in ObxTransformTest.
        assertEquals(observation.getValueType(), "NM");
        assertEquals(observation.getId(), "GLA^GLIADIN AB IgA, DEAMIDATED^SQ_LABP^63453-5^Gliadin p"
            + "eptide IgA Ser EIA-aCnc^LN");

        assertEquals(observation.getComments().size(), 5);
        assertEquals(observation.getComments().get(0), "Interpretive Ranges:");

        observation = obxList.get(1);

        // A more thorough test of hl7 file parsing to event is done in ObxTransformTest.
        assertEquals(observation.getValueType(), "NM");
        assertEquals(observation.getId(), "GLG^GLIADIN AB IgG, DEAMIDATED^SQ_LABP^63459-2^Gliadin p"
            + "eptide IgG Ser EIA-aCnc^LN");
        assertEquals(observation.getComments().size(), 5);
        assertEquals(observation.getComments().get(3), "POSITIVE: >30.0");

        obxCompleted = true;
      } else {
        fail("Unknown event type found: " + event.getType());
      }
    }
    assertTrue(obxCompleted, "OBX segment was not successfully processed");
  }
}
