// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for the ImagingStudy model.
 */
public class ImagingStudyTest extends ModelTestBase {
  @Test
  public <T extends Object> void testImagingStudy() throws IOException, URISyntaxException {
    ImagingStudy decoded = (ImagingStudy) geneticEncodeDecodeTest(TestModels.imagingStudy);

    assertEquals(decoded.getNumberOfInstances(), new BigDecimal(9001));
    assertEquals(decoded.getNumberOfSeries(), new BigDecimal(9002));
    assertEquals(decoded.getId(), Id.of("Id"));
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getInterpreterId(), Id.of("Interpreter"));
    assertEquals(decoded.getReferrerId(), Id.of("Referrer"));
    assertEquals(decoded.getAvailability(), ImagingStudyAvailability.NEARLINE);
    assertEquals(decoded.getSeries(), TestModels.imagingStudySeries);
    assertEquals(decoded.getStarted(), TestModels.getDateTime());
    assertEquals(decoded.getModalityList(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getProcedures(), Arrays.asList(TestModels.coding));
    assertEquals(decoded.getOrderIds(), Arrays.asList(Id.of("Orders")));
    assertEquals(decoded.getOid(), Id.of("2.3.4.5"));
    assertEquals(decoded.getClinicalInformation(), "clinicalInformation");
    assertEquals(decoded.getDescription(), "description");
    assertEquals(decoded.getAccession(), TestModels.getURI());
    assertEquals(decoded.getUrl(), TestModels.getURI());
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("accession");
    jsonProperties.add("availability");
    jsonProperties.add("clinicalInformation");
    jsonProperties.add("description");
    jsonProperties.add("@id");
    jsonProperties.add("interpreterId");
    jsonProperties.add("modalityList");
    jsonProperties.add("numberOfInstances");
    jsonProperties.add("numberOfSeries");
    jsonProperties.add("oid");
    jsonProperties.add("orderIds");
    jsonProperties.add("patientId");
    jsonProperties.add("procedures");
    jsonProperties.add("referrerId");
    jsonProperties.add("series");
    jsonProperties.add("started");
    jsonProperties.add("url");

    geneticJsonContainsFieldsTest(TestModels.imagingStudy, jsonProperties);
  }
}
