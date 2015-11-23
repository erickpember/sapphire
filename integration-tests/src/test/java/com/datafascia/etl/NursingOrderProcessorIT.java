// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl;

import ca.uhn.fhir.model.dstu2.composite.AnnotationDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.services.ApiTestSupport;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.Languages;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.etl.ucsf.web.NursingOrdersProcessor;
import com.neovisionaries.i18n.LanguageCode;
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
  private UnitedStatesPatient testPatient;
  private Encounter testEncounter;

  @BeforeClass
  public void setup() throws Exception {
    testPatient = createPatient();
    testEncounter = createEncounter(testPatient);
  }

  @AfterClass
  public void close() throws Exception {
    client.delete().resource(testEncounter).execute();
    client.delete().resource(testPatient).execute();
  }

  private UnitedStatesPatient createPatient() {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT)
        .setValue(NursingOrderProcessorIT.class.getSimpleName() + "1");
    patient.addName()
        .addGiven("ECMNOTES").addFamily("TEST");
    patient.addCommunication()
        .setPreferred(true).setLanguage(Languages.createLanguage(LanguageCode.en));
    patient
        .setRace(RaceEnum.AMERICAN_INDIAN)
        .setMaritalStatus(MaritalStatusCodesEnum.A)
        .setGender(AdministrativeGenderEnum.FEMALE)
        .setBirthDate(new DateDt("1977-01-01"))
        .setActive(true);

    MethodOutcome outcome = client.create().resource(patient)
        .encodedJson().execute();
    patient.setId(outcome.getId());
    return patient;
  }

  private Encounter createEncounter(UnitedStatesPatient patient) {
    Encounter encounter = new Encounter()
        .setPatient(new ResourceReferenceDt(patient))
        .setStatus(EncounterStateEnum.IN_PROGRESS);
    encounter.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER)
        .setValue("2085202");

    MethodOutcome outcome = client.create().resource(encounter)
        .encodedJson().execute();
    encounter.setId(outcome.getId());
    return encounter;
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
          setStartDate(new Date(1439449200000l));
          setDiscontinuedDate(new Date(1439577620000l));
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
          setStartDate(new Date(1439535600000l));
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
          setStartDate(new Date(1439535600000l));
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
          setStartDate(new Date(1439535600000l));
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
          setStartDate(new Date(1439535600000l));
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
          setStartDate(new Date(1439535600000l));
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
          setStartDate(new Date(1439535600000l));
          setDiscontinuedDate(new Date(1439577695000l));
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
          setStartDate(new Date(1439535600000l));
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
          setStartDate(new Date(1439535600000l));
          setDiscontinuedDate(new Date(1439577824000l));
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
          setStartDate(new Date(1439535600000l));
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
          setStartDate(new Date(1439535600000l));
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
