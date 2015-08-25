// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.composite.RangeDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationPrescription;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import com.datafascia.api.services.ApiIT;
import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.AuthorizationsSupplier;
import com.datafascia.common.accumulo.ColumnVisibilityPolicy;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.accumulo.FixedAuthorizationsSupplier;
import com.datafascia.common.accumulo.FixedColumnVisibilityPolicy;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import com.datafascia.common.persist.entity.AccumuloFhirEntityStore;
import com.datafascia.common.persist.entity.AccumuloReflectEntityStore;
import com.datafascia.common.persist.entity.FhirEntityStore;
import com.datafascia.common.persist.entity.ReflectEntityStore;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.persist.Tables;
import com.datafascia.etl.ucsf.web.MedAdminDiffListener;
import com.datafascia.etl.ucsf.web.MedAdminDiffProcessor;
import com.datafascia.etl.ucsf.web.UcsfMedicationUtils;
import com.datafascia.etl.ucsf.web.UcsfWebGetProcessor;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * A test class for the MedAdminDiffProcessor
 */
@Slf4j
public class MedAdminDiffProcessorIT extends ApiIT implements MedAdminDiffListener {
  private static final String TABLE = "testTable";
  private MedAdminDiffProcessor processor;
  private TestModule testModule;

  // Setup accumulo injections
  private static class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(AuthorizationsSupplier.class).to(FixedAuthorizationsSupplier.class);
      bind(AvroSchemaRegistry.class).to(MemorySchemaRegistry.class).in(Singleton.class);
      bind(ColumnVisibilityPolicy.class).to(FixedColumnVisibilityPolicy.class);
      bind(FhirContext.class).in(Singleton.class);
      bind(FhirEntityStore.class).to(AccumuloFhirEntityStore.class).in(Singleton.class);

      bindConstant().annotatedWith(Names.named("entityTableNamePrefix")).to(Tables.ENTITY_PREFIX);
    }

    @Provides
    @Singleton
    public Connector connector(ConnectorFactory factory) {
      return factory.getConnector();
    }

    @Provides
    @Singleton
    public ConnectorFactory connectorFactory() {
      return new ConnectorFactory(AccumuloConfiguration.builder()
          .instance(ConnectorFactory.MOCK_INSTANCE)
          .zooKeepers("")
          .user("root")
          .password("secret")
          .build());
    }

    @Provides
    @Singleton
    public ReflectEntityStore reflectEntityStore(
        AvroSchemaRegistry schemaRegistry, AccumuloTemplate accumuloTemplate) {

      return new AccumuloReflectEntityStore(schemaRegistry, Tables.ENTITY_PREFIX, accumuloTemplate);
    }
  }

  @Inject
  private AccumuloTemplate accumuloTemplate;

  @Inject
  private AccumuloReflectEntityStore entityStore;

  @BeforeClass
  public void beforeRepositoryTestSupport() throws Exception {
    testModule = new TestModule();
    Injector injector = Guice.createInjector(testModule);
    injector.injectMembers(this);
  }

  @BeforeClass
  public void setup() throws Exception {
    MemorySQLTestDatabase.populateDb();
    transientEncounter = createAnEncounterWithApi();
  }

  Encounter transientEncounter = null;
  @AfterClass
  public void close() throws Exception {
    client.delete().resource(transientEncounter).execute();
  }

  protected void scan() {
    Scanner scanner = accumuloTemplate.createScanner(entityStore.getDataTableName());
    try {
      for (Map.Entry<Key, Value> entry : scanner) {
        System.out.format(
            "%s %s %s %s%n",
            entry.getKey().getRow(),
            entry.getKey().getColumnFamily(),
            entry.getKey().getColumnQualifier(),
            entry.getValue());
      }
    } finally {
      scanner.close();
    }
  }

  @Test
  public void testOnTrigger() throws Exception {
    Thread.sleep(2000);

    TestRunner runner = TestRunners.newTestRunner(MedAdminDiffProcessor.class);
    runner.setProperty(MedAdminDiffProcessor.ACCUMULOTABLE, TABLE);
    runner.setProperty(MedAdminDiffProcessor.RXNORMDB, MemorySQLTestDatabase.JDBCURL);
    runner.setProperty(MedAdminDiffProcessor.RXNORMTABLE, MemorySQLTestDatabase.TABLE);
    runner.setProperty(MedAdminDiffProcessor.RXNORMDBUSERNAME, MemorySQLTestDatabase.USER);
    runner.setProperty(MedAdminDiffProcessor.RXNORMDBPASSWORD, MemorySQLTestDatabase.PASSWORD);
    runner.setProperty(MedAdminDiffProcessor.FHIR_SERVER, fhirServer);
    runner.setProperty(MedAdminDiffProcessor.FHIR_USERNAME, FHIR_USERNAME);
    runner.setProperty(MedAdminDiffProcessor.FHIR_PASSWORD, FHIR_PASSWORD);

    processor = (MedAdminDiffProcessor) runner.getProcessor();
    processor.setDiffListener(this);
    processor.setConnector(testModule.connector(testModule.connectorFactory()));
    processor.setClient(client);

    // Positive tests of new data and diffs.
    runner.enqueue(addContent("GetMedAdminUnit{questionMark}ListID=15860.json"));
    runner.enqueue(addContent("GetMedAdminUnit{questionMark}ListID=15860&FromDate=2015-07-14T06" +
        "{colon}14{colon}50Z.json"));
    runner.enqueue(addContent("GetMedAdminUnit{questionMark}ListID=15860&FromDate=2015-07-14T06" +
        "{colon}19{colon}28Z.json"));
    runner.enqueue(addContent("GetMedAdminUnit{questionMark}ListID=15860&FromDate=2015-07-14T06" +
        "{colon}22{colon}27Z.json"));
    runner.enqueue(addContent("GetMedAdminUnit{questionMark}ListID=15860&FromDate=2015-07-14T06" +
        "{colon}27{colon}36Z.json"));
    runner.enqueue(addContent("GetMedAdminUnit{questionMark}ListID=15860&FromDate=2015-07-14T06" +
        "{colon}37{colon}45Z.json"));
    runner.enqueue(addContent("GetMedAdminUnit{questionMark}ListID=15860&FromDate=2015-07-14T06" +
        "{colon}38{colon}54Z.json"));
    runner.enqueue(addContent("GetMedAdminUnit{questionMark}ListID=15860&FromDate=2015-07-14T06" +
        "{colon}40{colon}30Z.json"));
    UcsfMedicationUtils.getEncounter("5041113", client);
    runner.run(enqueueCount);
    runner.assertQueueEmpty();
    runner.assertAllFlowFilesTransferred(MedAdminDiffProcessor.SUCCESS);

    // Errors from the webservices come back as HTML. Make sure we trigger a failure on those.
    enqueueCount = 0;
    try {
      testFailure(runner, "invalid.json");
      fail("The expected ProcessException was not throw.");
    } catch (Throwable ex) {
      assertEquals(ex.getCause().getMessage(), "Unexpected character (<) at position 0.");
    }

    // Make sure all the test cases were used.
    for (String key : expectedAdmins.keySet()) {
      if (!countedAdmins.contains(key)) {
        log.error("DIDN'T COUNT ADMIN " + key + "!!!");
      }
    }

    assertEquals(expectedAdminsCount, expectedAdmins.keySet().size());
    assertEquals(expectedPrescriptionsCount, expectedPrescriptions.keySet().size());
    assertEquals(diffsIndex, diffs.length);
  }

  private void testFailure(TestRunner runner, String file) throws Exception {
    runner.clearTransferState();
    runner.enqueue(addContent(file));
    runner.run(enqueueCount);
    runner.assertQueueEmpty();
    runner.assertAllFlowFilesTransferred(MedAdminDiffProcessor.FAILURE);
  }

  @Override
  public void diff(ElementType type, String field, String id, String oldData, String newData) {
    assertEquals(diffs[diffsIndex],
        TestDiff.builder()
            .type(type)
            .field(field)
            .id(id)
            .oldData(oldData)
            .newData(newData).build());
    diffsIndex++;
  }

  int diffsIndex = 0;
  TestDiff[] diffs = new TestDiff[]{
      TestDiff.builder()
          .type(ElementType.ORDER)
          .field("OrderStatus")
          .id("159301153")
          .oldData("2^Sent")
          .newData("5^Completed").build(),
      TestDiff.builder()
          .type(ElementType.ORDER)
          .field("OrderedDose")
          .id("159301150")
          .oldData("50")
          .newData("100").build(),
      TestDiff.builder()
          .type(ElementType.ORDER)
          .field("OrderStatus")
          .id("159301152")
          .oldData("2^Sent")
          .newData("9^Discontinued").build()
  };

  @Override
  public void newOrder(MedicationPrescription order) {
    String orderIdentifier = order.getIdentifierFirstRep().getValue();
    TestNewPrescription expected = expectedPrescriptions.get(orderIdentifier);

    if (expected == null) {
      log.error("No test case defined for order " + orderIdentifier);
      return;
    }

    MedicationPrescription.DosageInstruction expectedDosage = order.addDosageInstruction();

    if (expected.orderedDose != null) {
      if (expected.orderedDose.contains("-")) {
        String[] ratioParts = expected.orderedDose.split("-");
        RangeDt range = new RangeDt();
        QuantityDt low = new QuantityDt(Long.parseLong(ratioParts[0]));
        QuantityDt high = new QuantityDt(Long.parseLong(ratioParts[1]));
        low.setUnits(expected.orderedDoseUnits);
        high.setUnits(expected.orderedDoseUnits);
        range.setLow(low);
        range.setHigh(high);
        expectedDosage.setDose(range);
      } else {
        QuantityDt quantityDt = new QuantityDt();
        quantityDt.setUnits(expected.orderedDoseUnits);
        quantityDt.setValue(new BigDecimal(expected.orderedDose));
        expectedDosage.setDose(quantityDt);
      }
    }

    assertEquals(order.getIdentifierFirstRep().getValue(), expected.identifier);
    assertEquals(order.getDateWritten(), new DateTimeDt(Date.from(
        UcsfWebGetProcessor.epicDateToInstant(expected.dateTimeOrdered))).getValue());
    assertEquals(order.getStatus().toUpperCase(), expected.orderStatus);
    if (expectedDosage.getDose() != null) {
      MedicationPrescription.DosageInstruction dosage = order.getDosageInstructionFirstRep();

      if (expectedDosage.getDose() instanceof RangeDt) {
        RangeDt doseRange = (RangeDt) dosage.getDose();
        RangeDt expectedRange = (RangeDt) expectedDosage.getDose();
        assertEquals(doseRange.getHigh().getValue(), expectedRange.getHigh().getValue());
        assertEquals(doseRange.getLow().getValue(), expectedRange.getLow().getValue());
        assertEquals(doseRange.getHigh().getUnits(), expectedRange.getHigh().getUnits());
      } else if (expectedDosage.getDose() instanceof QuantityDt) {
        QuantityDt doseQuantity = (QuantityDt) dosage.getDose();
        assertEquals(doseQuantity.getValue(), ((QuantityDt) expectedDosage.getDose()).getValue());
        assertEquals(doseQuantity.getUnits(), ((QuantityDt) expectedDosage.getDose()).getUnits());
      }
    }
    expectedPrescriptionsCount++;
  }

  @Override
  public void newAdmin(MedicationAdministration admin) {
    TestNewAdmin expected = expectedAdmins.get(admin.getIdentifierFirstRep().getValue());
    if (expected == null) {
      log.error("No test case defined for admin " + admin.getIdentifierFirstRep().getValue());
      return;
    }
    if (expected.scd != null && !expected.scd.equals("")) {
      if (admin.getMedication().getReference().getIdPart() == null) {
        log.warn("No medication given for admin " + admin.getIdentifierFirstRep().getValue() +
            ". Was expecting " + expected.scd + " aka " + expected.drugName);
      } else {
        Medication medication = UcsfMedicationUtils.getMedication(admin.getMedication()
            .getReference().getIdPart(), client);
        assertEquals(medication.getCode().getCodingFirstRep().getCode(), expected.scd);
        assertEquals(medication.getName(), expected.drugName);
      }
    }
    assertEquals(admin.getDosage().getQuantity().getValue().toString(), expected.doseQuantity);
    assertEquals(admin.getDosage().getQuantity().getUnits(), expected.doseUnit);
    assertEquals(admin.getEncounter().getReference().getIdPart(), expected.encounterId);
    assertEquals(admin.getIdentifierFirstRep().getValue(), expected.identifier);
    assertEquals(admin.getIdentifierFirstRep().getValue().split("-")[0], expected.orderId);
    DateTimeDt time = (DateTimeDt) admin.getEffectiveTime();
    try {
      assertEquals(time.getValue().toInstant(),
          UcsfWebGetProcessor.epicDateToInstant(expected.timeTaken));
    } catch (AssertionError e) {
      log.error("Bad time for " + admin.getIdentifierFirstRep().getValue());
      throw e;
    }
    expectedAdminsCount++;
    countedAdmins.add(admin.getIdentifierFirstRep().getValue());
  }

  ArrayList<String> countedAdmins = new ArrayList<>();

  int enqueueCount = 0;

  public InputStream addContent(String filename) throws FileNotFoundException {
    enqueueCount++;
    return new FileInputStream("src/test/resources/medAdminDiffProcessor/" + filename);
  }

  int expectedAdminsCount = 0;
  HashMap<String, TestNewAdmin> expectedAdmins = new HashMap<String, TestNewAdmin>() {
    {
      put("159301111-1", new TestNewAdmin().builder()
          .doseQuantity("800")
          .doseUnit("mg")
          .drugName("Ibuprofen 800 MG Oral Tablet")
          .encounterId("5041113")
          .identifier("159301111-1")
          .orderId("159301111")
          .scd("197807")
          .timeTaken("/Date(1433149260000-0700)/").build());
      put("159301112-31", new TestNewAdmin().builder()
          .doseQuantity("50")
          .doseUnit("mcg")
          .drugName("Fentanyl 0.05 MG/ML Injectable Solution")
          .encounterId("5041113")
          .identifier("159301112-31")
          .orderId("159301112")
          .scd("206977")
          .timeTaken("/Date(1434012420000-0700)/").build());
      put("159301113-1", new TestNewAdmin().builder()
          .doseQuantity("4")
          .doseUnit("mg")
          .drugName("Diazepam 5 MG/ML Injectable Solution")
          .encounterId("5041113")
          .identifier("159301113-1")
          .orderId("159301113")
          .scd("309845")
          .timeTaken("/Date(1433500080000-0700)/").build());
      put("159301114-19", new TestNewAdmin().builder()
          .doseQuantity("5")
          .doseUnit("mg")
          .drugName("Diazepam 1 MG/ML Oral Solution")
          .encounterId("5041113")
          .identifier("159301114-19")
          .orderId("159301114")
          .scd("309843")
          .timeTaken("/Date(1434011520000-0700)/").build());
      put("159301114-94", new TestNewAdmin().builder()
          .doseQuantity("5")
          .doseUnit("mg")
          .drugName("Diazepam 1 MG/ML Oral Solution")
          .encounterId("5041113")
          .identifier("159301114-94")
          .orderId("159301114")
          .scd("309843")
          .timeTaken("/Date(1436175000000-0700)/").build());
      put("159301114-118", new TestNewAdmin().builder()
          .doseQuantity("5")
          .doseUnit("mg")
          .drugName("Diazepam 1 MG/ML Oral Solution")
          .encounterId("5041113")
          .identifier("159301114-118")
          .orderId("159301114")
          .scd("309843")
          .timeTaken("/Date(1436864400000-0700)/").build());
      put("159301116-1", new TestNewAdmin().builder()
          .doseQuantity("2")
          .doseUnit("mg")
          .drugName("Diazepam 2 MG Oral Tablet")
          .encounterId("5041113")
          .identifier("159301116-1")
          .orderId("159301116")
          .scd("197590")
          .timeTaken("/Date(1433500500000-0700)/").build());
      put("159301116-2", new TestNewAdmin().builder()
          .doseQuantity("2")
          .doseUnit("mg")
          .drugName("Diazepam 2 MG Oral Tablet")
          .encounterId("5041113")
          .identifier("159301116-2")
          .orderId("159301116")
          .scd("197590")
          .timeTaken("/Date(1433501040000-0700)/").build());
      put("159301116-3", new TestNewAdmin().builder()
          .doseQuantity("2")
          .doseUnit("mg")
          .drugName("Diazepam 2 MG Oral Tablet")
          .encounterId("5041113")
          .identifier("159301116-3")
          .orderId("159301116")
          .scd("197590")
          .timeTaken("/Date(1434013140000-0700)/").build());
      put("159301116-4", new TestNewAdmin().builder()
          .doseQuantity("2")
          .doseUnit("mg")
          .drugName("Diazepam 2 MG Oral Tablet")
          .encounterId("5041113")
          .identifier("159301116-4")
          .orderId("159301116")
          .scd("197590")
          .timeTaken("/Date(1436186100000-0700)/").build());
      put("159301117-1", new TestNewAdmin().builder()
          .doseQuantity("650")
          .doseUnit("mg")
          .drugName("8 HR Acetaminophen 650 MG Extended Release Oral Tablet")
          .encounterId("5041113")
          .identifier("159301117-1")
          .orderId("159301117")
          .scd("1148399")
          .timeTaken("/Date(1434016800000-0700)/").build());
      put("159301119-1", new TestNewAdmin().builder()
          .doseQuantity("80")
          .doseUnit("mg")
          .drugName("Acetaminophen 80 MG Chewable Tablet")
          .encounterId("5041113")
          .identifier("159301119-1")
          .orderId("159301119")
          .scd("307696")
          .timeTaken("/Date(1434013200000-0700)/").build());
      put("159301119-2", new TestNewAdmin().builder()
          .doseQuantity("80")
          .doseUnit("mg")
          .drugName("Acetaminophen 80 MG Chewable Tablet")
          .encounterId("5041113")
          .identifier("159301119-2")
          .orderId("159301119")
          .scd("307696")
          .timeTaken("/Date(1434081600000-0700)/").build());
      put("159301119-3", new TestNewAdmin().builder()
          .doseQuantity("80")
          .doseUnit("mg")
          .drugName("Acetaminophen 80 MG Chewable Tablet")
          .encounterId("5041113")
          .identifier("159301119-3")
          .orderId("159301119")
          .scd("307696")
          .timeTaken("/Date(1436792220000-0700)/").build());
      put("159301145-1", new TestNewAdmin().builder()
          .doseQuantity("2")
          .doseUnit("mg")
          .drugName("Diazepam 2 MG Oral Tablet")
          .encounterId("5041113")
          .identifier("159301145-1")
          .orderId("159301145")
          .scd("197590")
          .timeTaken("/Date(1436792460000-0700)/").build());
      put("159301146-1", new TestNewAdmin().builder()
          .doseQuantity("5")
          .doseUnit("drop")
          .drugName("carbamide peroxide 65 MG/ML Otic Solution")
          .encounterId("5041113")
          .identifier("159301146-1")
          .orderId("159301146")
          .scd("702050")
          .timeTaken("/Date(1436792460000-0700)/").build());
      put("159301153-1", new TestNewAdmin().builder()
          .doseQuantity("500")
          .doseUnit("mL")
          .drugName("")
          .encounterId("5041113")
          .identifier("159301153-1")
          .orderId("159301153")
          .scd("")
          .timeTaken("/Date(1436880000000-0700)/").build());
      put("159301119-1", new TestNewAdmin().builder()
          .doseQuantity("80")
          .doseUnit("mg")
          .drugName("Acetaminophen 80 MG Chewable Tablet")
          .encounterId("5041113")
          .identifier("159301119-1")
          .orderId("159301119")
          .scd("307696")
          .timeTaken("/Date(1434013200000-0700)/").build());
      put("159301119-2", new TestNewAdmin().builder()
          .doseQuantity("80")
          .doseUnit("mg")
          .drugName("Acetaminophen 80 MG Chewable Tablet")
          .encounterId("5041113")
          .identifier("159301119-2")
          .orderId("159301119")
          .scd("307696")
          .timeTaken("/Date(1434081600000-0700)/").build());
      put("159301119-3", new TestNewAdmin().builder()
          .doseQuantity("80")
          .doseUnit("mg")
          .drugName("Acetaminophen 80 MG Chewable Tablet")
          .encounterId("5041113")
          .identifier("159301119-3")
          .orderId("159301119")
          .scd("307696")
          .timeTaken("/Date(1436792220000-0700)/").build());
      put("159301119-4", new TestNewAdmin().builder()
          .doseQuantity("80")
          .doseUnit("mg")
          .drugName("Acetaminophen 80 MG Chewable Tablet")
          .encounterId("5041113")
          .identifier("159301119-4")
          .orderId("159301119")
          .scd("307696")
          .timeTaken("/Date(1436880360000-0700)/").build());
      put("159301150-1", new TestNewAdmin().builder()
          .doseQuantity("50")
          .doseUnit("mL/hr")
          .drugName("Glucose 50 MG/ML / Sodium Chloride 0.0769 MEQ/ML Injectable Solution")
          .encounterId("5041113")
          .identifier("159301150-1")
          .orderId("159301150")
          .scd("309806")
          .timeTaken("/Date(1436880060000-0700)/").build());
      put("159301152-1", new TestNewAdmin().builder()
          .doseQuantity("2")
          .doseUnit("mcg/kg/min")
          .drugName("Dopamine Hydrochloride 0.8 MG/ML Injectable Solution")
          .encounterId("5041113")
          .identifier("159301152-1")
          .orderId("159301152")
          .scd("1292740")
          .timeTaken("/Date(1436880060000-0700)/").build());
      put("159301154-1", new TestNewAdmin().builder()
          .doseQuantity("1")
          .doseUnit("mcg/kg/min")
          .drugName("Dopamine Hydrochloride 0.8 MG/ML Injectable Solution")
          .encounterId("5041113")
          .identifier("159301154-1")
          .orderId("159301154")
          .scd("1292740")
          .timeTaken("/Date(1436881080000-0700)/").build());
      put("159301152-1", new TestNewAdmin().builder()
          .doseQuantity("2")
          .doseUnit("mcg/kg/min")
          .drugName("Dopamine Hydrochloride 0.8 MG/ML Injectable Solution")
          .encounterId("5041113")
          .identifier("159301152-1")
          .orderId("159301152")
          .scd("1292740")
          .timeTaken("/Date(1436880060000-0700)/").build());
    }
  };

  int expectedPrescriptionsCount = 0;
  HashMap<String, TestNewPrescription> expectedPrescriptions = new HashMap<String,
      TestNewPrescription>() {
    {
      put("159301110", new TestNewPrescription().builder()
          .identifier("159301110")
          .dateTimeOrdered("/Date(1432896430000-0700)/")
          .orderStatus("STOPPED")
          .orderedDose("1")
          .orderedDoseUnits("tablet")
          .build());
      put("159301111", new TestNewPrescription().builder()
          .identifier("159301111")
          .dateTimeOrdered("/Date(1433149283000-0700)/")
          .orderStatus("ACTIVE")
          .orderedDose("800")
          .orderedDoseUnits("mg")
          .build());
      put("159301112", new TestNewPrescription().builder()
          .identifier("159301112")
          .dateTimeOrdered("/Date(1433150574000-0700)/")
          .orderStatus("ACTIVE")
          .orderedDose("50")
          .orderedDoseUnits("mcg")
          .build());
      put("159301113", new TestNewPrescription().builder()
          .identifier("159301113")
          .dateTimeOrdered("/Date(1433499153000-0700)/")
          .orderStatus("ACTIVE")
          .orderedDose("2")
          .orderedDoseUnits("mg")
          .build());
      put("159301114", new TestNewPrescription().builder()
          .identifier("159301114")
          .dateTimeOrdered("/Date(1433499300000-0700)/")
          .orderStatus("ACTIVE")
          .orderedDose("5")
          .orderedDoseUnits("mg")
          .build());
      put("159301115", new TestNewPrescription().builder()
          .identifier("159301115")
          .dateTimeOrdered("/Date(1433499567000-0700)/")
          .orderStatus("STOPPED")
          .orderedDose("10")
          .orderedDoseUnits("mg")
          .build());
      put("159301116", new TestNewPrescription().builder()
          .identifier("159301116")
          .dateTimeOrdered("/Date(1433499692000-0700)/")
          .orderStatus("ACTIVE")
          .orderedDose("2")
          .orderedDoseUnits("mg")
          .build());
      put("159301117", new TestNewPrescription().builder()
          .identifier("159301117")
          .dateTimeOrdered("/Date(1434013133000-0700)/")
          .orderStatus("COMPLETED")
          .orderedDose("650")
          .orderedDoseUnits("mg")
          .build());
      put("159301118", new TestNewPrescription().builder()
          .identifier("159301118")
          .dateTimeOrdered("/Date(1434013430000-0700)/")
          .orderStatus("ACTIVE")
          .orderedDoseUnits("")
          .build());
      put("159301119", new TestNewPrescription().builder()
          .identifier("159301119")
          .dateTimeOrdered("/Date(1434013689000-0700)/")
          .orderStatus("ACTIVE")
          .orderedDose("80")
          .orderedDoseUnits("mg")
          .build());
      put("159301120", new TestNewPrescription().builder()
          .identifier("159301120")
          .dateTimeOrdered("/Date(1434112582000-0700)/")
          .frequency("Twice Daily")
          .orderStatus("ACTIVE")
          .orderedDose("500")
          .orderedDoseUnits("mg")
          .route("Oral")
          .build());
      put("159301127", new TestNewPrescription().builder()
          .identifier("159301127")
          .dateTimeOrdered("/Date(1436361637000-0700)/")
          .orderStatus("ACTIVE")
          .orderedDose("0-20")
          .orderedDoseUnits("mg/hr")
          .build());
      put("159301128", new TestNewPrescription().builder()
          .identifier("159301128")
          .dateTimeOrdered("/Date(1436361638000-0700)/")
          .orderStatus("ACTIVE")
          .orderedDose("5-10")
          .orderedDoseUnits("mg")
          .route("Intravenous")
          .build());
      put("159301145", new TestNewPrescription().builder()
          .identifier("159301145")
          .dateTimeOrdered("/Date(1436792480000-0700)/")
          .frequency("Once")
          .orderStatus("COMPLETED")
          .orderedDose("2")
          .orderedDoseUnits("mg")
          .build());
      put("159301146", new TestNewPrescription().builder()
          .identifier("159301146")
          .dateTimeOrdered("/Date(1436792480000-0700)/")
          .orderStatus("COMPLETED")
          .orderedDose("5")
          .orderedDoseUnits("drop")
          .build());
      put("159301147", new TestNewPrescription().builder()
          .identifier("159301147")
          .dateTimeOrdered("/Date(1436792480000-0700)/")
          .orderStatus("ACTIVE")
          .orderedDose("20000")
          .orderedDoseUnits("Units")
          .build());
      put("159301150", new TestNewPrescription().builder()
          .identifier("159301150")
          .dateTimeOrdered("/Date(1436879925000-0700)/")
          .orderStatus("ACTIVE")
          .orderedDose("50")
          .orderedDoseUnits("mL/hr")
          .build());
      put("159301151", new TestNewPrescription().builder()
          .identifier("159301151")
          .dateTimeOrdered("/Date(1436879926000-0700)/")
          .orderStatus("ACTIVE")
          .orderedDose("1")
          .orderedDoseUnits("g")
          .build());
      put("159301152", new TestNewPrescription().builder()
          .identifier("159301152")
          .dateTimeOrdered("/Date(1436879926000-0700)/")
          .orderStatus("ACTIVE")
          .orderedDose("2")
          .orderedDoseUnits("mcg/kg/min")
          .build());
      put("159301153", new TestNewPrescription().builder()
          .identifier("159301153")
          .dateTimeOrdered("/Date(1436879927000-0700)/")
          .orderStatus("ACTIVE")
          .orderedDose("500")
          .orderedDoseUnits("mL")
          .build());
      put("159301154", new TestNewPrescription().builder()
          .identifier("159301154")
          .dateTimeOrdered("/Date(1436880679000-0700)/")
          .orderStatus("ACTIVE")
          .orderedDose("1")
          .orderedDoseUnits("mcg/kg/min")
          .build());
    }
  };

  private Encounter createAnEncounterWithApi() {
    Encounter encounter1 = new Encounter();
    encounter1.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("5041113");
    encounter1.setStatus(EncounterStateEnum.IN_PROGRESS);
    MethodOutcome outcome = client.create().resource(encounter1)
        .encodedJson().execute();
    IdDt id = outcome.getId();
    encounter1.setId(id);
    return encounter1;
  }

  /**
   * Container for diff data test blobs.
   */
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  @ToString
  @Builder
  public static class TestDiff {
    private MedAdminDiffListener.ElementType type;
    private String field;
    private String id;
    private String oldData;
    private String newData;
  }

  /**
   * Container for new data test blobs.
   */
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  @ToString
  @Builder
  public static class TestNewAdmin {
    private String identifier = "";
    private String encounterId = "";
    private String timeTaken = "";
    private String doseQuantity = "";
    private String doseUnit = "";
    private String scd = "";
    private String orderId = "";
    private String drugName = "";
  }

  /**
   * Container for new data test blobs.
   */
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  @ToString
  @Builder
  public static class TestNewPrescription {
    private String identifier = "";
    private String dateTimeOrdered = "";
    private String frequency = "";
    private String orderStatus = "";
    private String orderedDose = "";
    private String orderedDoseUnits = "";
    private String route = "";
  }
}
