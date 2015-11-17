// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl;

import ca.uhn.fhir.model.dstu2.composite.AnnotationDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import ca.uhn.fhir.rest.api.MethodOutcome;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.services.ApiTestSupport;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.etl.ucsf.web.NursingOrdersProcessor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.hl7.fhir.instance.model.api.IIdType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Integration tests for the NursingOrderProcessor.
 */
@Slf4j
public class NursingOrderProcessorIT extends ApiTestSupport {
  private NursingOrdersProcessor processor;
  private ClientBuilder clientBuilder;
  private Encounter transientEncounter;

  @BeforeClass
  public void setup() throws Exception {
    transientEncounter = createAnEncounterWithApi();
  }

  @AfterClass
  public void close() throws Exception {
    client.delete().resource(transientEncounter).execute();
  }

  private Encounter createAnEncounterWithApi() {
    Encounter encounter1 = new Encounter();
    encounter1.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("2085202");
    encounter1.setStatus(EncounterStateEnum.IN_PROGRESS);
    MethodOutcome outcome = client.create().resource(encounter1)
        .encodedJson().execute();
    IIdType id = outcome.getId();
    encounter1.setId(id);
    return encounter1;
  }

  int enqueueCount = 0;

  @Test
  public void testOnTrigger() throws Exception {
    Thread.sleep(2000);

    TestRunner runner = TestRunners.newTestRunner(NursingOrdersProcessor.class);
    clientBuilder = new ClientBuilder();

    // Positive tests of new data and diffs.
    runner.enqueue(addContent("GetOrdersByUnit.json"));
    runner.enqueue(addContent("GetOrdersByUnit-2.json"));
    runner.enqueue(addContent("GetOrdersByUnit-3.json"));
    runner.enqueue(addContent("GetOrdersByUnit-4.json"));
    runner.enqueue(addContent("GetOrdersByUnit-5.json"));
    runner.enqueue(addContent("GetOrdersByUnit-6.json"));
    runner.run(enqueueCount);
    runner.assertQueueEmpty();
    runner.assertAllFlowFilesTransferred(NursingOrdersProcessor.SUCCESS);

    for (String key : expectedOrders.keySet()) {
      ExpectedOrder expectedOrder = expectedOrders.get(key);

      ProcedureRequest storedRequest = clientBuilder.getProcedureRequestClient()
          .read(key, "2085202").get();

      assertNotNull(storedRequest);
      assertEquals(storedRequest.getEncounter().getReference().getIdPart(), expectedOrder
          .getEncounter());
      assertEquals(storedRequest.getCode().getCodingFirstRep().getCode(), expectedOrder
          .getProcedureId());
      assertEquals(storedRequest.getCode().getText(), expectedOrder
          .getDescription());
      assertEquals(storedRequest.getIdentifierFirstRep().getValue(), expectedOrder.getOrderId());

      PeriodDt period = (PeriodDt) storedRequest.getScheduled();
      assertEquals(period.getStart(), expectedOrder.getStartDate());
      assertEquals(period.getEnd(), expectedOrder.getDiscontinuedDate());

      for (AnnotationDt note : storedRequest.getNotes()) {
        assertTrue(expectedOrder.getQuestions().contains(note.getText()));
      }
    }
  }

  private InputStream addContent(String filename) throws FileNotFoundException {
    enqueueCount++;
    return new FileInputStream("src/test/resources/nursingOrderProcessor/" + filename);
  }

  private HashMap<String, ExpectedOrder> expectedOrders = new HashMap<String, ExpectedOrder>() {
    {
      put("1689469", new ExpectedOrder() {
        {
          setEncounter("2085202");
          setProcedureId("87881");
          setDescription("No VTE Prevention Indicated");
          setOrderId("1689469");
          setStartDate(new Date(1439398800000l));
          setDiscontinuedDate(new Date(1439527220000l));
          setStatus(ProcedureRequestStatusEnum.COMPLETED);
          setQuestions(new ArrayList<String>() {
            {
              add("No VTEP because patient at very low risk for VTE (VTE score < 4 and walking " +
                  "unassisted or will be discharged within 24 hours)");
            }
          });
        }
      });
      put("1689484", new ExpectedOrder() {
        {
          setEncounter("2085202");
          setProcedureId("7936");
          setDescription("Maintain sequential compression device");
          setOrderId("1689484");
          setStartDate(new Date(1439485200000l));
          setStatus(ProcedureRequestStatusEnum.PROPOSED);
          setQuestions(new ArrayList<>());
        }
      });
      put("1689485", new ExpectedOrder() {
        {
          setEncounter("2085202");
          setProcedureId("7820");
          setDescription("Place sequential compression device");
          setOrderId("1689485");
          setStartDate(new Date(1439485200000l));
          setStatus(ProcedureRequestStatusEnum.PROPOSED);
          setQuestions(new ArrayList<>());
        }
      });
      put("1689486", new ExpectedOrder() {
        {
          setEncounter("2085202");
          setProcedureId("40999");
          setDescription("Remove sequential compression device");
          setOrderId("1689486");
          setStartDate(new Date(1439485200000l));
          setStatus(ProcedureRequestStatusEnum.PROPOSED);
          setQuestions(new ArrayList<>());
        }
      });
      put("1689487", new ExpectedOrder() {
        {
          setEncounter("2085202");
          setProcedureId("40739");
          setDescription("Head of bed flat");
          setOrderId("1689487");
          setStartDate(new Date(1439485200000l));
          setStatus(ProcedureRequestStatusEnum.PROPOSED);
          setQuestions(new ArrayList<>());
        }
      });
      put("1689488", new ExpectedOrder() {
        {
          setEncounter("2085202");
          setProcedureId("57652");
          setDescription("Keep prone");
          setOrderId("1689488");
          setStartDate(new Date(1439485200000l));
          setStatus(ProcedureRequestStatusEnum.PROPOSED);
          setQuestions(new ArrayList<>());
        }
      });
      put("1689490", new ExpectedOrder() {
        {
          setEncounter("2085202");
          setProcedureId("124838");
          setDescription("RASS = Target RASS -3: Moderate Sedation (acceptable range -4 to -2)");
          setOrderId("1689490");
          setStartDate(new Date(1439485200000l));
          setDiscontinuedDate(new Date(1439527295000l));
          setStatus(ProcedureRequestStatusEnum.COMPLETED);
          setQuestions(new ArrayList<String>() {
            {
              add("Target RASS -3: Moderate Sedation (acceptable range -4 to -2)");
            }
          });
        }
      });
      put("1689478", new ExpectedOrder() {
        {
          setEncounter("2085202");
          setProcedureId("87881");
          setDescription("No VTE Prevention Indicated");
          setOrderId("1689478");
          setStartDate(new Date(1439485200000l));
          setStatus(ProcedureRequestStatusEnum.ACCEPTED);
          setQuestions(new ArrayList<String>() {
            {
              add("No additional mechanical/pharmacologic VTEP because patient is already " +
                  "therapeutically anticoagulated (e.g., warfarin, heparin, etc.)");
            }
          });
        }
      });
      put("1689477", new ExpectedOrder() {
        {
          setEncounter("2085202");
          setProcedureId("143391");
          setDescription("Bed Rest with HOB <= 30 Degrees Continuous");
          setOrderId("1689477");
          setStartDate(new Date(1439485200000l));
          setDiscontinuedDate(new Date(1439527424000l));
          setStatus(ProcedureRequestStatusEnum.COMPLETED);
          setQuestions(new ArrayList<String>() {
            {
              add("Bathroom Privileges");
              add("May stand to void");
              add("Other (specify)");
            }
          });
        }
      });
      put("1689492", new ExpectedOrder() {
        {
          setEncounter("2085202");
          setProcedureId("124838");
          setDescription("RASS = Target RASS -5: Unarousable (acceptable range -5 to -4)");
          setOrderId("1689492");
          setStartDate(new Date(1439485200000l));
          setStatus(ProcedureRequestStatusEnum.ACCEPTED);
          setQuestions(new ArrayList<String>() {
            {
              add("Target RASS -5: Unarousable (acceptable range -5 to -4)");
            }
          });
        }
      });
      put("1691295", new ExpectedOrder() {
        {
          setEncounter("2085202");
          setProcedureId("143391");
          setDescription("Bed Rest with HOB <= 30 Degrees Once");
          setOrderId("1691295");
          setStartDate(new Date(1439485200000l));
          setStatus(ProcedureRequestStatusEnum.ACCEPTED);
          setQuestions(new ArrayList<String>() {
            {
              add("Bathroom Privileges");
              add("May stand to void");
              add("Other (specify)");
              add("Other (specify)");
              add("May stand to void");
            }
          });
        }
      });
    }
  };

  /**
   * Encapsulates an expected order.
   */
  @Data
  public static class ExpectedOrder {
    private String encounter;
    private String description;
    private String orderId;
    private String procedureId;
    private ProcedureRequestStatusEnum status;
    private Date startDate;
    private Date discontinuedDate;
    private List<String> questions;
  }
}
