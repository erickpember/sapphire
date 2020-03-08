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
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Questionnaire;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.QuestionnaireRepository;
import java.util.Optional;
import javax.inject.Inject;

/**
 * QuestionnaireResourceProvider resource endpoint
 */
public class QuestionnaireResourceProvider implements IResourceProvider {

  @Inject
  private QuestionnaireRepository questionnaireRepository;

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   *
   * @return Class of resource.
   */
  @Override
  public Class<Questionnaire> getResourceType() {
    return Questionnaire.class;
  }

  /**
   * Updates resource, or creates resource if no resource already exists for the id.
   *
   * @param questionnaire
   *     new content to store
   * @return method outcome
   */
  @Update
  public MethodOutcome update(@ResourceParam Questionnaire questionnaire) {
    questionnaireRepository.save(questionnaire);
    return new MethodOutcome(questionnaire.getId());
  }

  /**
   * Store a new questionnaire.
   *
   * @param questionnaire The new questionnaire to store.
   * @return Outcome of create method. Resource ID of Procedure.
   */
  @Create
  public MethodOutcome create(@ResourceParam Questionnaire questionnaire) {
    questionnaireRepository.save(questionnaire);
    return new MethodOutcome(questionnaire.getId());
  }

  /**
   * Retrieves a questionnaire using the ID.
   *
   * @param resourceId ID of patient resource.
   * @return resource matching this identifier
   * @throws ResourceNotFoundException if not found
   */
  @Read()
  public Questionnaire getResourceById(@IdParam IdDt resourceId) {
    Optional<Questionnaire> result = questionnaireRepository.read(Id.of(resourceId.getIdPart()));
    if (result.isPresent()) {
      return result.get();
    } else {
      throw new ResourceNotFoundException("Questionnaire resource with ID: "
          + resourceId.getIdPart() + " not found.");
    }
  }
}
