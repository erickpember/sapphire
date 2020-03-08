// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
