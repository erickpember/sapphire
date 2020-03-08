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

import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.Ids;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.FlagRepository;
import com.datafascia.domain.persist.PatientRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Flag resource endpoint
 */
@Slf4j
public class FlagResourceProvider implements IResourceProvider {

  @Inject
  private PatientRepository patientRepository;

  @Inject
  private FlagRepository flagRepository;

  @Override
  public Class<Flag> getResourceType() {
    return Flag.class;
  }

  /**
   * Creates a flag.
   *
   * @param flag
   *     to create
   * @return
   *     Outcome of create method. If successful, includes ID of created resource.
   */
  @Create
  public MethodOutcome create(@ResourceParam Flag flag) {
    flagRepository.save(flag);
    return new MethodOutcome(flag.getId());
  }

  /**
   * Searches flags by patient ID. Absent an ID, return flags for all patients.
   *
   * @param patientId
   *     patient ID
   * @return
   *     flags
   */
  @Search
  public List<Flag> search(@OptionalParam(name = Flag.SP_PATIENT) StringParam patientId) {
    List<Flag> flags = new ArrayList<>();
    if (patientId != null) {
      flags = flagRepository.list(Id.of(patientId.getValue()));
    } else {
      List<UnitedStatesPatient> allPatients = patientRepository.list(
          Optional.empty(), Optional.empty(), PatientResourceProvider.MAX_DEFAULT_PATIENT_RESULTS);
      for (UnitedStatesPatient patient : allPatients) {
        flags.addAll(flagRepository.list(Ids.toPrimaryKey(patient.getId())));
      }
    }
    return flags;
  }
}
