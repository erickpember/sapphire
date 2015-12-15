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
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.nifi.annotation.behavior.WritesAttribute;
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
 * Generates event to update harm evidence values which are dependent on the current time.
 * This NiFi processor should be scheduled to run frequently enough so the
 * values served to clients are not too stale.
 */
@CapabilityDescription("Updates harm evidence values which are dependent on the current time.")
@ConfigurationNode("UpdateHarmEvidence")
@MetaInfServices(Processor.class)
@Tags({"datafascia", "emerge", "ucsf"})
@WritesAttribute(
    attribute = "encounterIdentifier",
    description = "Identifier of encounter to update")
public class UpdateHarmEvidence extends DependencyInjectingProcessor {

  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("success")
      .description("Event to update harm evidence for an encounter")
      .build();

  private static final Set<Relationship> RELATIONSHIPS = ImmutableSet.of(SUCCESS);

  private static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults();

  @Configure
  @Inject
  private volatile String pointOfCare;

  private volatile Set<String> desiredPointsOfCare;

  @Inject
  private volatile EncounterRepository encounterRepository;

  @Inject
  private volatile LocationRepository locationRepository;

  @Override
  public Set<Relationship> getRelationships() {
    return RELATIONSHIPS;
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

  private void writeSuccess(ProcessSession session, String encounterIdentifier)
      throws ProcessException {

    FlowFile flowFile = session.create();
    flowFile = session.putAttribute(flowFile, "encounterIdentifier", encounterIdentifier);

    session.getProvenanceReporter().create(flowFile);
    session.transfer(flowFile, SUCCESS);
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
      writeSuccess(session, encounter.getIdentifierFirstRep().getValue());
    }
  }
}
