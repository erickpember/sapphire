// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.fhir;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.model.dstu2.composite.BoundCodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.util.ElementUtil;
import lombok.ToString;

/**
 * Extends {@link Patient} with race from
 * <a href="http://hl7.org/fhir/us-core.html">United States realm FHIR Profile</a>
 */
@ResourceDef(name = "Patient") @ToString(callSuper = true)
public class UnitedStatesPatient extends Patient {

  @Child(name = "race")
  @Description(formalDefinition = "category of humans sharing history, origin or nationality")
  @Extension(
      url = "http://hl7.org/fhir/StructureDefinition/us-core-race",
      definedLocally = false,
      isModifier = false)
  private BoundCodeableConceptDt<RaceEnum> race;

  /**
   * @return race
   */
  public BoundCodeableConceptDt<RaceEnum> getRace() {
    if (race == null) {
      race = new BoundCodeableConceptDt<>(RaceEnum.VALUESET_BINDER);
    }
    return race;
  }

  /**
   * Sets race.
   *
   * @param race
   *     value to set
   * @return this object
   */
  public UnitedStatesPatient setRace(BoundCodeableConceptDt<RaceEnum> race) {
    this.race = race;
    return this;
  }

  /**
   * Sets race.
   *
   * @param race
   *     value to set
   * @return this object
   */
  public UnitedStatesPatient setRace(RaceEnum race) {
    getRace().setValueAsEnum(race);
    return this;
  }

  @Override
  public boolean isEmpty() {
    return super.isEmpty() && ElementUtil.isEmpty(race);
  }
}
