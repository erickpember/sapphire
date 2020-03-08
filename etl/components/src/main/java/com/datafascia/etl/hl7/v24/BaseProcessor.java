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
package com.datafascia.etl.hl7.v24;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.model.v24.segment.PV1;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.etl.hl7.MessageProcessor;
import com.datafascia.etl.hl7.RaceMap;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.neovisionaries.i18n.LanguageCode;

/**
 * Implements common methods for message processors.
 */
public abstract class BaseProcessor implements MessageProcessor {

  protected static String getPatientIdentifier(PID pid) {
    String patientIdentifier = pid.getPatientIdentifierList(0).getID().getValue();
    if (Strings.isNullOrEmpty(patientIdentifier)) {
      throw new IllegalStateException("Field PID-3 does not contain patient identifier");
    }
    return patientIdentifier;
  }

  protected static String getEncounterIdentifier(PV1 pv1) {
    String encounterIdentifier = pv1.getVisitNumber().getID().getValue();
    if (Strings.isNullOrEmpty(encounterIdentifier)) {
      throw new IllegalStateException("Field PV1-19 does not contain visit number");
    }
    return encounterIdentifier;
  }

  protected MaritalStatusCodesEnum toMaritalStatus(String code) {
    MaritalStatusCodesEnum maritalStatus = MaritalStatusCodesEnum.UNK.forCode(code);
    return MoreObjects.firstNonNull(maritalStatus, MaritalStatusCodesEnum.UNK);
  }

  protected RaceEnum toRace(String code) {
    if (code == null) {
      return RaceEnum.UNKNOWN;
    }

    RaceEnum race = RaceMap.raceMap.get(code.toLowerCase());
    return MoreObjects.firstNonNull(race, RaceEnum.UNKNOWN);
  }

  protected CodeableConceptDt toLanguage(String code) {
    LanguageCode languageCode = LanguageCode.getByCodeIgnoreCase(code);
    languageCode = MoreObjects.firstNonNull(languageCode, LanguageCode.undefined);

    CodeableConceptDt codeableConcept = new CodeableConceptDt();
    codeableConcept.addCoding()
        .setCode(languageCode.name())
        .setDisplay(languageCode.getName());
    return codeableConcept;
  }
}
