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
 * Identifier system constants
 */
public class IdentifierSystems {

  /** dataFascia invented this identifier system for EpisodeOfCare */
  public static final String EPISODE_OF_CARE = "http://datafascia.com/identifier/EpisodeOfCare";

  /** institution assigned patient identifier */
  public static final String INSTITUTION_PATIENT =
      "http://datafascia.com/identifier/InstitutionPatient";

  /** institution assigned billing account number */
  public static final String INSTITUTION_BILLING_ACCOUNT =
      "http://datafascia.com/identifier/InstitutionBillingAccount";

  /** institution assigned encounter identifier */
  public static final String INSTITUTION_ENCOUNTER =
      "http://datafascia.com/identifier/InstitutionEncounter";

  /** institution assigned location identifier */
  public static final String INSTITUTION_LOCATION =
      "http://datafascia.com/identifier/InstitutionLocation";

  /** institution assigned medication administration identifier */
  public static final String INSTITUTION_MEDICATION_ADMINISTRATION =
      "http://datafascia.com/identifier/InstitutionMedicationAdministration";

  /** institution assigned medication order identifier */
  public static final String INSTITUTION_MEDICATION_ORDER =
      "http://datafascia.com/identifier/InstitutionMedicationOrder";

  /** institution assigned observation sub-identifier */
  public static final String INSTITUTION_OBSERVATION_SUB_IDENTIFIER =
      "http://datafascia.com/identifier/InstitutionObservationSubIdentifier";

  /** institution assigned practitioner identifier */
  public static final String INSTITUTION_PRACTITIONER =
      "http://datafascia.com/identifier/InstitutionPractitioner";

  /** institution assigned procedure request identifier */
  public static final String INSTITUTION_PROCEDURE_REQUEST =
      "http://datafascia.com/identifier/InstitutionProcedureRequest";

  /** dataFascia invented this identifier system for Questionnaire */
  public static final String QUESTIONNAIRE = "http://datafascia.com/identifier/Questionnaire";

  /** dataFascia invented this identifier system for Questionnaire Response */
  public static final String QUESTIONNAIRE_RESPONSE =
      "http://datafascia.com/identifier/QuestionnaireResponse";

  // Private constructor disallows creating instances of this class.
  private IdentifierSystems() {
  }
}
