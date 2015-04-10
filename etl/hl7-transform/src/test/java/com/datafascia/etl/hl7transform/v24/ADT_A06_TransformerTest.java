// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform.v24;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ADT_A06;
import ca.uhn.hl7v2.parser.Parser;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.EventType;
import com.datafascia.domain.event.ObservationData;
import com.datafascia.domain.event.ObservationListData;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * This tests parsing of OBX from a hl7 file to Events.
 *
 */
@Slf4j
public class ADT_A06_TransformerTest {

  private static final String HL7_OBX_FILE = "ADT_A06_OBXtest.hl7";
  private static final HapiContext context = new DefaultHapiContext();
  private static final Parser parser = context.getGenericParser();

  /**
   * Test of getApplicableMessageType method, of class ADT_A06_Transformer.
   */
  @Test
  public void testGetApplicableMessageType() {
    ADT_A06_Transformer transformer = new ADT_A06_Transformer();

    assertEquals(transformer.getApplicableMessageType(), ADT_A06.class);
  }

  /**
   * Test of transform method, of class ADT_A06_Transformer.
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

    ADT_A06_Transformer transformer = new ADT_A06_Transformer();
    List<Event> events = transformer.transform(new URI("http://institution"),
        new URI("http://facility"), hapiMsg);

    boolean obxCompleted = false;

    for (Event event : events) {
      log.debug("Transform transformed Message to Event. institutionId:{}, facilityId:{},"
          + " type:{}, data:{}", event.getInstitutionId(), event.getFacilityId(), event.getType(),
          event.getData());

      if (event.getType().equals(EventType.OBSERVATIONS_ADD)) {
        assertNotNull(event.getData());
        ObservationListData obxListData = (ObservationListData) event.getData();
        List<ObservationData> obxList = obxListData.getObservations();
        assertEquals(obxList.size(), 1, "Wrong size of observations collected.");
        ObservationData observation = obxList.get(0);

        // A more thorough test of hl7 file parsing to event is done in ObxTransformTest.
        assertEquals(observation.getValueType(), "NM");
        assertEquals(observation.getId(), "WT^WEIGHT");

        obxCompleted = true;
      } else {
        fail("Unknown even type found:" + event.getType());
      }
    }
    assertTrue(obxCompleted, "OBX segment was not successfully processed");
  }
}
