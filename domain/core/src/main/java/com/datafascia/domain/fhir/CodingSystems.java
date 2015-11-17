// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.fhir;

/**
 * Coding system constants
 */
public class CodingSystems {

  /** dataFascia invented this coding system for observation */
  public static final String OBSERVATION =
      "http://datafascia.com/coding/Observation";

  /** dataFascia invented this coding system for procedure type */
  public static final String PROCEDURE =
      "http://datafascia.com/coding/Procedure";

  /** dataFascia invented this coding system for procedure request */
  public static final String PROCEDURE_REQUEST =
      "http://datafascia.com/coding/ProcedureRequest";

  /** dataFascia invented this coding system for body site */
  public static final String BODY_SITE =
      "http://datafascia.com/coding/BodySite";

  /** dataFascia invented this coding system for body orientation */
  public static final String BODY_ORIENTATION =
      "http://datafascia.com/coding/BodyOrientation";

  /** dataFascia invented this coding system for UCSF medication group name */
  public static final String UCSF_MEDICATION_GROUP_NAME =
      "http://datafascia.com/coding/UcsfMedicationGroupName";

  /** dataFascia invented this coding system for Semantic Clinical Drug */
  public static final String SEMANTIC_CLINICAL_DRUG =
      "http://datafascia.com/coding/SemanticClinicalDrug";

  /** dataFascia invented this coding system for drugs with SCDs */
  public static final String DRUG_UNKNOWN =
      "http://datafascia.com/coding/DrugUnknown";

  /** dataFascia invented this coding system for Semantic Clinical Drug */
  public static final String MEDICATION_INGREDIENT =
      "http://datafascia.com/coding/MedicationIngredient";

  /** dataFascia invented this coding system for UCSF administration not given reasons */
  public static final String UCSF_REASON_NOT_GIVEN = "http://datafascia.com/coding/ReasonNotGiven";

  /** dataFascia invented this coding system for UCSF administration given reasons */
  public static final String UCSF_REASON_GIVEN = "http://datafascia.com/coding/ReasonGiven";

  // Private constructor disallows creating instances of this class.
  private CodingSystems() {
  }
}
