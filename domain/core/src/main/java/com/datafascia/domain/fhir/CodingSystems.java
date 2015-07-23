// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.fhir;

/**
 * Coding system constants
 */
public class CodingSystems {

  /** We invented this coding system for procedure type */
  public static final String PROCEDURE_TYPE =
      "http://datafascia.com/coding/ProcedureType";

  /** We invented this coding system for body site */
  public static final String BODY_SITE =
      "http://datafascia.com/coding/BodySite";

  // Private constructor disallows creating instances of this class.
  private CodingSystems() {
  }
}
