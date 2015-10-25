// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import com.codahale.metrics.annotation.Timed;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.LocationRepository;
import com.datafascia.emerge.ucsf.EmergeDataFeed;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.HarmEvidenceBundle;
import com.datafascia.emerge.ucsf.persist.HarmEvidenceRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

/**
 * Resource providing the Emerge data.
 */
@Path("/emerge/harmEvidence")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class HarmEvidenceResource {

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private LocationRepository locationRepository;

  @Inject
  private HarmEvidenceRepository harmEvidenceRepository;

  // If desired point of care is not null, then include only encounters located
  // at that point of care.
  private boolean select(Encounter encounter, String desiredPointOfCare) {
    if (desiredPointOfCare == null) {
      return true;
    }

    String locationId = encounter.getLocationFirstRep().getLocation().getReference().getIdPart();
    Optional<Location> location = locationRepository.read(Id.of(locationId));
    if (!location.isPresent()) {
      return false;
    }

    String[] locationParts = location.get().getIdentifierFirstRep().getValue().split("\\^");
    String pointOfCare = (locationParts.length > 0) ? locationParts[0] : null;
    return desiredPointOfCare.equals(pointOfCare);
  }

  private List<Encounter> getEncounters(String pointOfCare) {
    return encounterRepository.list(Optional.of(EncounterStateEnum.IN_PROGRESS))
        .stream()
        .filter(encounter -> select(encounter, pointOfCare))
        .collect(Collectors.toList());
  }

  private List<HarmEvidence> getRecords(List<Encounter> encounters) {
    return encounters.stream()
        .flatMap(encounter -> {
          String patientId = encounter.getPatient().getReference().getIdPart();
          Optional<HarmEvidence> record = harmEvidenceRepository.read(Id.of(patientId));
          if (!record.isPresent()) {
            log.warn("Harm evidence record not found for patientId [{}]", patientId);
            return Stream.empty();
          }
          return Stream.of(record.get());
        })
        .collect(Collectors.toList());
  }

  /**
   * Lists harm evidence records.
   *
   * @param pointOfCare
   *     filter results to point of care
   * @return harm evidence bundle
   */
  @GET
  @Timed
  public HarmEvidenceBundle list(@QueryParam("pointOfCare") String pointOfCare) {
    List<Encounter> encounters = getEncounters(pointOfCare);
    List<HarmEvidence> records = getRecords(encounters);

    EmergeDataFeed emergeDataFeed = new EmergeDataFeed()
        .withTimeOfDataFeed(new Date())
        .withEmergePatients(records);

    HarmEvidenceBundle bundle = new HarmEvidenceBundle()
        .withEmergeDataFeed(emergeDataFeed);
    return bundle;
  }
}
