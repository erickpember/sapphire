// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import com.datafascia.common.fhir.DependencyInjectingResourceProvider;
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
public class FlagResourceProvider extends DependencyInjectingResourceProvider {

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
