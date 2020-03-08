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

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import com.neovisionaries.i18n.LanguageCode;

/**
 * Language utility methods
 */
public class Languages {

  // Private constructor disallows creating instances of this class.
  private Languages() {
  }

  /**
   * Creates language codeable concept.
   *
   * @param languageCode
   *     language code
   * @return codeable concept
   */
  public static CodeableConceptDt createLanguage(LanguageCode languageCode) {
    CodeableConceptDt codeableConcept = new CodeableConceptDt();
    codeableConcept.addCoding().setCode(languageCode.name()).setDisplay(languageCode.getName());
    return codeableConcept;
  }
}
