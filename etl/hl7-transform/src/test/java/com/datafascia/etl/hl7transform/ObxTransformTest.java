// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.segment.NTE;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.model.v24.message.ADT_A03;
import ca.uhn.hl7v2.model.v24.segment.OBX;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.Terser;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.ObservationData;
import com.datafascia.domain.event.ObservationType;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static org.testng.Assert.assertEquals;

/**
 * Test for OBX transform.
 */
@Slf4j
public class ObxTransformTest {
  private static final HapiContext context = new DefaultHapiContext();
  private static final Parser p = context.getGenericParser();
  private static final String segmentRoot = "/RESPONSE/ORDER_OBSERVATION/OBSERVATION";
  private static final TestObxV24Transformer transformer24 = new TestObxV24Transformer();
  private static final TestObxV23Transformer transformer23 = new TestObxV23Transformer();
  private static final String V24 = "2.4";
  private static final String V23 = "2.3";
  private static String currentFile;

  @Test
  public void testObx() throws IOException, HL7Exception {
    List<File> files = new ArrayList<>();
    files.addAll(Arrays.asList(new File("src/test/resources/ADT").listFiles()));
    files.addAll(Arrays.asList(new File("src/test/resources/ORU").listFiles()));

    for (File file : files) {
      if (file.isFile()) {
        parseFile(file.getAbsolutePath());
      }
    }
  }

  private static void parseFile(String filename) throws IOException, HL7Exception {
    currentFile = filename;
    Message hapiMsg = p.parse(content(filename));
    Terser terser = new Terser(hapiMsg);
    String hl7type = terser.get("/MSH-9-1");
    String hl7subtype = terser.get("/MSH-9-2");
    handleRoutes(hl7type, hl7subtype, hapiMsg, terser);
  }

  private static void handleRoutes(String hl7type, String hl7subtype, Message hapiMsg, Terser
      terser) throws HL7Exception {
    /*
     * The test data we have now has all ADT messages in version 2.4 format and all ORU messages in
     * version 2.3 format.
     */
    if (hl7type.equals("ADT") && hapiMsg.getVersion().equals(V24)) {
      if (hl7subtype.equals("A01")) {
        handleV24A01((ADT_A01) hapiMsg);
      } else if (hl7subtype.equals("A03")) {
        handleV24A03((ADT_A03) hapiMsg);
      }
    } else if (hl7type.equals("ORU") && hl7subtype.equals("R01")
        && hapiMsg.getVersion().equals(V23)) {
      handleV23R01(terser);
    } else {
      log.info("Unhandled message. Type:" + hl7type + " Subtype:" + hl7subtype + " Version: "
          + hapiMsg.getVersion());
    }
  }

  private static void handleV24A01(ADT_A01 a01) throws HL7Exception {
    for (int i = 0; i < a01.getOBXAll().size(); i++) {
      OBX obx = a01.getOBXAll().get(i);
      ObservationData obdata = transformer24.testObxData(obx, ObservationType.A01);

      // Test the values of a known A01 message.
      if (currentFile.endsWith("1420235232060.dat.hl7")) {
        if (i == 0) {
          assertEquals(obdata.getValueType(), "NM");
          assertEquals(obdata.getId(), "BW^BIRTH WEIGHT");
          assertEquals(obdata.getSubId(), "1");
          assertEquals(obdata.getValue().get(0), "63.49");
          assertEquals(obdata.getValueUnits(), "oz");
        } else if (i == 1) {
          assertEquals(obdata.getValueType(), "NM");
          assertEquals(obdata.getId(), "HT^HEIGHT");
          assertEquals(obdata.getValue().get(0), "17.72");
          assertEquals(obdata.getValueUnits(), "in");
          assertEquals(obdata.getObservationDateAndTime(), "20150102");
        } else if (i == 2) {
          assertEquals(obdata.getValueType(), "NM");
          assertEquals(obdata.getId(), "WT^WEIGHT");
          assertEquals(obdata.getValue().get(0), "1.8");
          assertEquals(obdata.getValueUnits(), "kg");
          assertEquals(obdata.getObservationDateAndTime(), "20150102");
        }
      }
    }

  }

  private static void handleV24A03(ADT_A03 a03) throws HL7Exception {
    for (int i = 0; i < a03.getOBXAll().size(); i++) {
      OBX obx = a03.getOBXAll().get(i);
      ObservationData obdata = transformer24.testObxData(obx, ObservationType.A03);

      // Test the values of a known A03 message.
      if (currentFile.endsWith("1424369138736.dat.hl7")) {
        if (i == 0) {
          assertEquals(obdata.getValueType(), "NM");
          assertEquals(obdata.getId(), "HT^HEIGHT");
          assertEquals(obdata.getValue().get(0), "69.02");
          assertEquals(obdata.getValueUnits(), "in");
          assertEquals(obdata.getObservationDateAndTime(), "20150219");
        } else if (i == 1) {
          assertEquals(obdata.getValueType(), "NM");
          assertEquals(obdata.getId(), "WT^WEIGHT");
          assertEquals(obdata.getValue().get(0), "70");
          assertEquals(obdata.getValueUnits(), "kg");
          assertEquals(obdata.getObservationDateAndTime(), "20150219");
        }
      }
    }
  }

  private static void handleV23R01(Terser terser) throws HL7Exception {
    for (int i = 0; i < 10000; i++) {
      String insert = i == 0 ? "" : "(" + Integer.toString(i) + ")";
      String obxSegmentPath = segmentRoot + insert + "/OBX";
      log.info("Getting segment: " + obxSegmentPath);

      ca.uhn.hl7v2.model.v23.segment.OBX obx = (ca.uhn.hl7v2.model.v23.segment.OBX) terser
          .getSegment(obxSegmentPath);

      // If null, it encodes to an empty OBX segment.
      if (obx.encode().equals("OBX")) {
        break;
      }

      List<NTE> ntes = new ArrayList<>();

      // Iterate NTEs for the OBX.
      for (int j = 0; j < 10000; j++) {
        String nteinsert = "(" + Integer.toString(j) + ")";
        if (j == 0) {
          nteinsert = "";
        }
        String nteSegmentPath = segmentRoot + insert + "/NTE" + nteinsert;
        NTE nte = (NTE) terser.getSegment(nteSegmentPath);
        if (nte.encode().equals("NTE")) {
          break;
        }
        ntes.add(nte);
      }

      ObservationData obdata = transformer23.testObxData(obx, ntes, ObservationType.ORU);

      // Test some known ORU contents.
      if (currentFile.endsWith("1423254262610.dat.hl7")) {
        if (i == 0) {
          log.info("obdata: " + obdata.toString());
          assertEquals(obdata.getValueType(), "NM");
          assertEquals(obdata.getId(), "GLA^GLIADIN AB IgA, DEAMIDATED^SQ_LABP^63453-5^Gliadin " +
              "peptide IgA Ser EIA-aCnc^LN");
          assertEquals(obdata.getSubId(), "1");
          assertEquals(obdata.getValue().get(0), "20.0");
          assertEquals(obdata.getValueUnits(), "CU");
          assertEquals(obdata.getReferenceRange(), "<20");
          assertEquals(obdata.getAbnormalFlags().get(0), "H");
          assertEquals(obdata.getResultStatus(), "C");
          assertEquals(obdata.getObservationDateAndTime(), "201502061224");
          assertEquals(obdata.getProducersId(), "CB");
          assertEquals(obdata.getResponsibleObserver(), "17191^HO^WAI-KIT");
          assertEquals(obdata.getComments().get(0), "Interpretive Ranges:");
          assertEquals(obdata.getComments().get(1), "NEGATIVE: <20.0");
          assertEquals(obdata.getComments().get(2), "WEAK POSITIVE: 20.0 TO 30.0");
          assertEquals(obdata.getComments().get(3), "POSITIVE: >30.0");
          assertEquals(obdata.getComments().get(4), "CORRECTED ON 02/06 AT 1224: " +
              "PREVIOUSLY REPORTED AS: 150.0 Interpretive Ranges: NEGATIVE: <20.0 WEAK POSITIVE: " +
              "20.0 TO 30.0 POSITIVE: >30.0");
        } else if (i == 1) {
          assertEquals(obdata.getValue().get(0), "850.0");
          assertEquals(obdata.getComments().get(0), "Interpretive Ranges:");
          assertEquals(obdata.getComments().get(1), "NEGATIVE: <20.0");
          assertEquals(obdata.getComments().get(2), "WEAK POSITIVE: 20.0 TO 30.0");
          assertEquals(obdata.getComments().get(3), "POSITIVE: >30.0");
          assertEquals(obdata.getComments().get(4), "CORRECTED ON 02/06 AT 1224: " +
              "PREVIOUSLY REPORTED AS: >1200.0 Interpretive Ranges: NEGATIVE: <20.0 WEAK " +
              "POSITIVE: 20.0 TO 30.0 POSITIVE: >30.0");
        }
      }
    }
  }

  private static class TestObxV24Transformer
      extends com.datafascia.etl.hl7transform.v24.BaseTransformer {

    public ObservationData testObxData(OBX obx, ObservationType type) throws HL7Exception {
      return this.toObservationData(V24, obx, null, type);
    }

    @Override
    public Class<? extends Message> getApplicableMessageType() {
      return null;
    }

    @Override
    public ArrayList<Event> transform(URI institutionId, URI facilityId, Message message) {
      return null;
    }
  }

  private static class TestObxV23Transformer
      extends com.datafascia.etl.hl7transform.v23.BaseTransformer {

    public ObservationData testObxData(ca.uhn.hl7v2.model.v23.segment.OBX obx, List<NTE> ntes,
          ObservationType type) throws HL7Exception {
      return this.toObservationData(V23, obx, ntes, type);
    }

    @Override
    public Class<? extends Message> getApplicableMessageType() {
      return null;
    }

    @Override
    public ArrayList<Event> transform(URI institutionId, URI facilityId, Message message) {
      return null;
    }
  }

  private static String content(String file) throws IOException {
    return new String(readAllBytes(get(file)), StandardCharsets.UTF_8).replace('\n', '\r');
  }
}
