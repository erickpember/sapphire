// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.harm;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.Terser;
import com.datafascia.api.services.ApiTestSupport;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.AccumuloReflectEntityStore;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.IngestMessageRepository;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.persist.HarmEvidenceRepository;
import com.datafascia.etl.hl7.HL7MessageProcessor;
import com.datafascia.etl.ucsf.web.NursingOrdersTransformer;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Common implementation for tests of exported data
 */
public abstract class HarmEvidenceTestSupport extends ApiTestSupport {

  protected static final String PATIENT_IDENTIFIER = "97546762";
  private static final Id<UnitedStatesPatient> PATIENT_ID = Id.of(PATIENT_IDENTIFIER);
  private static final String ENCOUNTER_IDENTIFIER = "5014212";
  private static final Id<Encounter> ENCOUNTER_ID = Id.of(ENCOUNTER_IDENTIFIER);

  @Inject
  private Parser parser;

  @Inject
  private IngestMessageRepository ingestMessageRepository;

  @Inject
  private HL7MessageProcessor hl7MessageProcessor;

  @Inject
  private NursingOrdersTransformer nursingOrdersTransformer;

  @Inject
  private HarmEvidenceRepository harmEvidenceRepository;

  @Inject
  private HarmEvidenceUpdater harmEvidenceUpdater;

  @Inject
  private AccumuloReflectEntityStore entityStore;

  @Inject
  protected Clock clock;

  protected static UnitedStatesPatient getPatient() {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT)
        .setValue(PATIENT_IDENTIFIER);
    patient.setId(new IdDt(PatientRepository.generateId(patient).toString()));
    return patient;
  }

  protected static Encounter getEncounter() {
    Encounter encounter = new Encounter();
    encounter.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER)
        .setValue(ENCOUNTER_IDENTIFIER);
    encounter.setId(new IdDt(EncounterRepository.generateId(encounter).toString()));
    encounter.setPeriod(new PeriodDt().setStart(DateTimeDt.withCurrentTime()));
    encounter.setPatient(new ResourceReferenceDt(getPatient()));
    return encounter;
  }

  private String readResource(String hl7File) throws IOException {
    URL url = Resources.getResource(getClass(), hl7File);
    return Resources.toString(url, StandardCharsets.UTF_8).replace('\n', '\r');
  }

  private void saveMessageByEncounter(String hl7) throws HL7Exception {
    Message message = parser.parse(hl7);
    Terser terser = new Terser(message);
    String encounterIdentifier = terser.get("/.PV1-19");

    ingestMessageRepository.save(Id.of(encounterIdentifier), hl7);
  }

  protected void saveMessage(String hl7File) throws HL7Exception, IOException {
    String hl7 = readResource(hl7File);
    saveMessageByEncounter(hl7);
  }

  protected void processMessage(String hl7File) throws HL7Exception, IOException {
    String hl7 = readResource(hl7File);
    saveMessageByEncounter(hl7);
    hl7MessageProcessor.accept(hl7);
  }

  protected void processNursingOrder(String jsonFile) throws IOException {
    URL url = Resources.getResource(getClass(), jsonFile);
    String json = Resources.toString(url, StandardCharsets.UTF_8);
    nursingOrdersTransformer.accept(json);
  }

  protected void processTimer() {
    harmEvidenceUpdater.processTimer(getEncounter());
  }

  protected Optional<HarmEvidence> readOptionalHarmEvidence() {
    return harmEvidenceRepository.read(ENCOUNTER_ID);
  }

  protected HarmEvidence readHarmEvidence() {
    return readOptionalHarmEvidence().get();
  }

  protected void deleteIngestedData() {
    entityStore.delete(new EntityId(Encounter.class, ENCOUNTER_ID));
    entityStore.delete(new EntityId(UnitedStatesPatient.class, PATIENT_ID));
    ingestMessageRepository.delete(ENCOUNTER_ID);
  }
}
