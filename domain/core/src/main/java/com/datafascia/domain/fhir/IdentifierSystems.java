// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.fhir;

/**
 * Identifier system constants
 */
public class IdentifierSystems {

  /** institution assigned patient identifier */
  public static final String INSTITUTION_PATIENT =
      "http://datafascia.com/identifier/InstitutionPatient";

  /** institution assigned billing account number */
  public static final String INSTITUTION_BILLING_ACCOUNT =
      "http://datafascia.com/identifier/InstitutionBillingAccount";

  /** institution assigned encounter identifier */
  public static final String INSTITUTION_ENCOUNTER =
      "http://datafascia.com/identifier/InstitutionEncounter";

  // Private constructor disallows creating instances of this class.
  private IdentifierSystems() {
  }
}
