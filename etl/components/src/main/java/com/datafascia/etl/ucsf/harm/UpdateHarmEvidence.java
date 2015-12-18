// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import com.datafascia.common.configuration.ConfigurationNode;
import com.datafascia.common.configuration.Configure;
import com.datafascia.common.nifi.DependencyInjectingProcessor;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.LocationRepository;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceUpdater;
import com.datafascia.emerge.ucsf.persist.HarmEvidenceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
@ConfigurationNode("UpdateHarmEvidence")
@MetaInfServices(Processor.class)
@Tags({"datafascia", "emerge", "ucsf"})
public class UpdateHarmEvidence extends DependencyInjectingProcessor {

  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("success")
      .description("Encounter identifier of updated harm evidence record")
      .build();

  public static final Relationship FAILURE = new Relationship.Builder()
      .name("failure")
      .description("Harm evidence record which failed to update")
      .build();

  private Set<Relationship> relationships = ImmutableSet.of(SUCCESS, FAILURE);

  private static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults();

  @Configure
  @Inject
  private volatile String pointOfCare;

  private volatile Set<String> desiredPointsOfCare;

  @Inject
  private volatile EncounterRepository encounterRepository;

  @Inject
  private volatile LocationRepository locationRepository;

  @Inject
  private volatile HarmEvidenceRepository harmEvidenceRepository;

  @Inject
  private volatile HarmEvidenceUpdater harmEvidenceUpdater;

  @Inject
  private volatile ObjectMapper objectMapper;

  @Override
  public Set<Relationship> getRelationships() {
    return relationships;
  }

  @Override
  protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return Collections.emptyList();
  }

  @Override
  protected void onInjected(ProcessContext processContext) {
    log.info("Include points of care [{}]", new Object[] { pointOfCare });
    desiredPointsOfCare = new HashSet<>();
    COMMA_SPLITTER.split(pointOfCare)
        .forEach(p -> desiredPointsOfCare.add(p));
  }

  private Optional<String> getPointOfCare(Encounter encounter) {
    Id<Location> locationId =
        Id.of(encounter.getLocationFirstRep().getLocation().getReference().getIdPart());
    Optional<Location> location = locationRepository.read(locationId);
    if (!location.isPresent()) {
      return Optional.empty();
    }

    String[] locationParts = location.get().getIdentifierFirstRep().getValue().split("\\^");
    if (locationParts.length > 0) {
      String pointOfCare = locationParts[0];
      return Optional.of(pointOfCare);
    } else {
      return Optional.empty();
    }
  }

  private boolean isLocatedAt(Encounter encounter, Set<String> desiredPointsOfCare) {
    return getPointOfCare(encounter)
        .map(poc -> desiredPointsOfCare.contains(poc))
        .orElse(false);
  }

  private void writeSuccess(ProcessSession session, String content) throws ProcessException {
    FlowFile flowFile = session.create();
    flowFile = session.write(flowFile, output -> {
      output.write(content.getBytes(StandardCharsets.UTF_8));
    });

    session.getProvenanceReporter().create(flowFile);
    session.transfer(flowFile, SUCCESS);
  }

  private void writeFailure(ProcessSession session, HarmEvidence record, RuntimeException exception)
      throws ProcessException {

    FlowFile flowFile = session.create();
    flowFile = session.write(flowFile, output -> {
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(output, record);
    });

    flowFile = session.putAttribute(
        flowFile, "stackTrace", Throwables.getStackTraceAsString(exception));

    session.getProvenanceReporter().create(flowFile);
    session.transfer(flowFile, FAILURE);
  }

  private void update(HarmEvidence record, Encounter encounter) {
    log.info(
        "Updating harm evidence for encounter ID {}, patient ID {}",
        new Object[] {
          record.getEncounterID(), record.getDemographicData().getMedicalRecordNumber() });

    harmEvidenceUpdater.processTimer(encounter);
  }

  private void process(ProcessSession session, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();

    Optional<HarmEvidence> optionalRecord = harmEvidenceRepository.read(Id.of(encounterId));
    if (!optionalRecord.isPresent()) {
      log.warn(
          "HarmEvidence record not found for encounter ID {}", new Object[] { encounterId });
      return;
    }

    HarmEvidence record = optionalRecord.get();
    try {
      update(record, encounter);
      writeSuccess(session, encounterId);
    } catch (RuntimeException e) {
      log.error("Cannot update for encounter ID {}", new Object[] { encounterId }, e);
      writeFailure(session, record, e);
    }
  }

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    List<Encounter> encounters =
        encounterRepository.list(Optional.of(EncounterStateEnum.IN_PROGRESS))
        .stream()
        .filter(encounter -> isLocatedAt(encounter, desiredPointsOfCare))
        .collect(Collectors.toList());

    log.info("Processing {} encounters", new Object[] { encounters.size() });
    if (encounters.isEmpty()) {
      log.warn("No encounters found for points of care [{}]", new Object[] { pointOfCare });
    }

    for (Encounter encounter : encounters) {
      process(session, encounter);
    }
  }
}
