// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
