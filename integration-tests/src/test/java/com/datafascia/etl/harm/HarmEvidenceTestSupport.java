// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.api.services.ApiTestSupport;
import com.datafascia.common.inject.Injectors;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.emerge.ucsf.persist.HarmEvidenceRepository;
import com.datafascia.etl.hl7.HL7MessageProcessor;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import javax.inject.Inject;
import org.testng.annotations.BeforeClass;

/**
 * Common implementation for tests of exported data
 */
public abstract class HarmEvidenceTestSupport extends ApiTestSupport {

  protected static final String PATIENT_IDENTIFIER = "97546762";
  protected static final String ENCOUNTER_IDENTIFIER = "5014212";

  @Inject
  private HL7MessageProcessor hl7MessageProcessor;

  @Inject
  protected HarmEvidenceRepository harmEvidenceRepository;

  @Inject
  protected HarmEvidenceUpdater harmEvidenceUpdater;

  @Inject
  protected EncounterRepository encounterRepository;

  @Inject
  protected Clock clock;

  @BeforeClass
  public void beforeHarmEvidenceTestSupport() throws Exception {
    Injectors.getInjector().injectMembers(this);
  }

  protected static UnitedStatesPatient getPatient() {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT).setValue(PATIENT_IDENTIFIER);
    patient.setId(new IdDt(PatientRepository.generateId(patient).toString()));
    return patient;
  }

  protected static Encounter getEncounter() {
    Encounter encounter = new Encounter();
    encounter.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue(ENCOUNTER_IDENTIFIER);
    encounter.setId(new IdDt(EncounterRepository.generateId(encounter).toString()));

    encounter.setPatient(new ResourceReferenceDt(getPatient()));
    return encounter;
  }

  protected void processMessage(String hl7File) throws IOException {
    URL url = getClass().getResource(hl7File);
    String hl7 = Resources.toString(url, StandardCharsets.UTF_8).replace('\n', '\r');
    hl7MessageProcessor.accept(hl7);
  }

  protected void processTimer() {
    harmEvidenceUpdater.processTimer(getEncounter());
  }
}
