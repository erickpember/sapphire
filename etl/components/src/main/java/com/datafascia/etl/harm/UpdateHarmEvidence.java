// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.nifi.DependencyInjectingProcessor;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.persist.HarmEvidenceRepository;
import com.google.common.collect.ImmutableSet;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Processor;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.kohsuke.MetaInfServices;

/**
 * Updates harm evidence values which are dependent on the current time.
 * This NiFi processor should be scheduled to run frequently enough so the
 * values served to clients are not too stale.
 */
@CapabilityDescription("Updates harm evidence values which are dependent on the current time.")
@MetaInfServices(Processor.class)
@Tags({"datafascia", "emerge", "ucsf"})
public class UpdateHarmEvidence extends DependencyInjectingProcessor {

  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("success")
      .description("Encounter identifier of updated harm evidence record")
      .build();

  private Set<Relationship> relationships = ImmutableSet.of(SUCCESS);

  @Inject
  private volatile HarmEvidenceRepository harmEvidenceRepository;

  @Inject
  private volatile HarmEvidenceUpdater harmEvidenceUpdater;

  @Override
  public Set<Relationship> getRelationships() {
    return relationships;
  }

  @Override
  protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return Collections.emptyList();
  }

  private static UnitedStatesPatient getPatient(String patientIdentifier) {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT).setValue(patientIdentifier);
    patient.setId(new IdDt(PatientRepository.generateId(patient).toString()));
    return patient;
  }

  private static Encounter getEncounter(String encounterIdentifier, UnitedStatesPatient patient) {
    Encounter encounter = new Encounter();
    encounter.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue(encounterIdentifier);
    encounter.setId(new IdDt(EncounterRepository.generateId(encounter).toString()));

    encounter.setPatient(new ResourceReferenceDt(patient));
    return encounter;
  }

  private void writeSuccess(ProcessSession session, String content) throws ProcessException {
    FlowFile flowFile = session.create();
    flowFile = session.write(flowFile, output -> {
      output.write(content.getBytes(StandardCharsets.UTF_8));
    });

    session.getProvenanceReporter().create(flowFile);

    session.transfer(flowFile, SUCCESS);
  }

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    List<HarmEvidence> records = harmEvidenceRepository.list();
    for (HarmEvidence record : records) {
      UnitedStatesPatient patient =
          getPatient(record.getDemographicData().getMedicalRecordNumber());
      Encounter encounter = getEncounter(record.getEncounterID(), patient);
      harmEvidenceUpdater.processTimer(encounter);

      writeSuccess(session, record.getEncounterID());
    }
  }
}
