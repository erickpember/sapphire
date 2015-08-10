// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import com.datafascia.common.fhir.DependencyInjectingResourceProvider;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.FlagRepository;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Flag resource endpoint
 */
@Slf4j
public class FlagResourceProvider extends DependencyInjectingResourceProvider {

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
   * @return Outcome of create method. If successful, includes ID of created resource.
   */
  @Create
  public MethodOutcome create(@ResourceParam Flag flag) {
    flagRepository.save(flag);
    return new MethodOutcome(flag.getId());
  }

  /**
   * Searches flags by patient ID.
   *
   * @param patientId
   *     patient ID
   * @return flags
   */
  @Search
  public List<Flag> searchByPatientId(
      @RequiredParam(name = Flag.SP_PATIENT) StringParam patientId) {

    return flagRepository.list(Id.of(patientId.getValue()));
  }
}
