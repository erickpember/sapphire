// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.fhir;

/**
 * Identifier system constants
 */
public class IdentifierSystems {

  public static final String INSTITUTION_PATIENT_IDENTIFIER =
      "http://datafascia.com/identifier/InstitutionPatientIdentifier";

  public static final String ACCOUNT_NUMBER =
      "http://datafascia.com/identifier/AccountNumber";

  // Private constructor disallows creating instances of this class.
  private IdentifierSystems() {
  }
}
