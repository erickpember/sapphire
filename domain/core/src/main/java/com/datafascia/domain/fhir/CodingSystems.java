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

  /** dataFascia invented this coding system for Questionnaire Concept */
  public static final String QUESTIONNAIRE_CONCEPT =
      "http://datafascia.com/coding/QuestionnaireConcept";

  /** dataFascia invented this coding system for Questionnaire Option */
  public static final String QUESTIONNAIRE_OPTION =
      "http://datafascia.com/coding/QuestionnaireOption";

  /** dataFascia invented this coding system for UCSF administration not given reasons */
  public static final String UCSF_REASON_NOT_GIVEN = "http://datafascia.com/coding/ReasonNotGiven";

  /** dataFascia invented this coding system for UCSF administration given reasons */
  public static final String UCSF_REASON_GIVEN = "http://datafascia.com/coding/ReasonGiven";

  // Private constructor disallows creating instances of this class.
  private CodingSystems() {
  }
}
