// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.domain.event.AddObservationsData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.ObservationData;
import com.datafascia.domain.fhir.Dates;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.ObservationRepository;
import java.util.function.Consumer;
import javax.inject.Inject;

/**
 * Processes add observations event.
 */
public class AddObservations implements Consumer<Event> {

  @Inject
  private transient ObservationRepository observationRepository;

  private static Encounter getEncounter(String encounterIdentifier) {
    Encounter encounter = new Encounter();
    encounter.addIdentifier()
        .setSystem(IdentifierSystems.ENCOUNTER_IDENTIFIER).setValue(encounterIdentifier);
    encounter.setId(new IdDt(EncounterRepository.generateId(encounter).toString()));
    return encounter;
  }

  private static Observation toObservation(ObservationData fromObservation) {
    StringDt observationValue = new StringDt();
    observationValue.setValue(fromObservation.getValue().get(0));

    String code = fromObservation.getId();
    Observation observation = new Observation();
    observation.setCode(new CodeableConceptDt("system", code));
    observation.setValue(observationValue);
    observation.setIssued(Dates.toInstant(fromObservation.getObservationDateAndTime()));
    return observation;
  }

  @Override
  public void accept(Event event) {
    AddObservationsData addObservationsData = (AddObservationsData) event.getData();
    Encounter encounter = getEncounter(addObservationsData.getEncounterIdentifier());

    for (ObservationData fromObservation : addObservationsData.getObservations()) {
      Observation observation = toObservation(fromObservation);
      observationRepository.save(encounter, observation);
    }
  }
}
