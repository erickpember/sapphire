// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.common.persist.Id;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for observation model.
 */
public class ObservationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testObservation() throws IOException, URISyntaxException {
    Observation decoded = (Observation) geneticEncodeDecodeTest(TestModels.observation);
    assertEquals(decoded.getId(), Id.of("1234"));
    assertEquals(decoded.getName(), TestModels.codeable);
    assertEquals(decoded.getValues(), TestModels.observationValue);
    assertEquals(decoded.getInterpretation(), ObservationInterpretation.A);
    assertEquals(decoded.getComments(), "The patient is alive.");
    assertEquals(decoded.getApplies(), TestModels.period);
    assertEquals(decoded.getIssued(), TestModels.getDate());
    assertEquals(decoded.getStatus(), ObservationStatus.Final);
    assertEquals(decoded.getReliability(), ObservationReliability.Ok);
    assertEquals(decoded.getSite(), TestModels.codeable);
    assertEquals(decoded.getMethod(), TestModels.codeable);
    assertEquals(decoded.getIdentifier(), TestModels.codeable);
    assertEquals(decoded.getSubject(), TestModels.getURI());
    assertEquals(decoded.getSpecimen(), TestModels.getURI());
    assertEquals(decoded.getPerformer(), TestModels.getURI());
    assertEquals(decoded.getRange(), Arrays.asList(TestModels.referenceRange,
        TestModels.referenceRange));
    assertEquals(decoded.getRelatedTo(), Arrays.asList(TestModels.related, TestModels.related));
  }
}
