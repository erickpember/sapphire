// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.valueset.FlagStatusEnum;
import com.datafascia.domain.fhir.FlagCodeEnum;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates flags from observations.
 */
public class FlagBuilder {

  private static final String ADVANCE_DIRECTIVE = "230218701ADPOA";
  private static final String PHYSICIAN_ORDERS_FOR_LIFE_SUSTAINING_TREATMENT = "230218701POLST";

  private final UnitedStatesPatient patient;
  private final List<Flag> flags = new ArrayList<>();

  /**
   * Constructor
   *
   * @param patient
   *    patient to which flags apply
   */
  public FlagBuilder(UnitedStatesPatient patient) {
    this.patient = patient;
  }

  private void addFlag(FlagCodeEnum code, Observation observation) {
    PeriodDt period = new PeriodDt();
    period.setStart(observation.getIssued(), TemporalPrecisionEnum.DAY);

    Flag flag = new Flag()
        .setStatus(FlagStatusEnum.ACTIVE)
        .setPeriod(period)
        .setCode(new CodeableConceptDt(code.getSystem(), code.getCode()))
        .setPatient(new ResourceReferenceDt(patient));

    flags.add(flag);
  }

  /**
   * Adds observation.
   *
   * @param observation
   *     to add
   */
  public void add(Observation observation) {
    String code = observation.getCode().getCodingFirstRep().getCode();
    switch (code) {
      case ADVANCE_DIRECTIVE:
        addFlag(FlagCodeEnum.ADVANCE_DIRECTIVE, observation);
        break;
      case PHYSICIAN_ORDERS_FOR_LIFE_SUSTAINING_TREATMENT:
        addFlag(FlagCodeEnum.PHYSICIAN_ORDERS_FOR_LIFE_SUSTAINING_TREATMENT, observation);
        break;
    }
  }

  /**
   * Creates flags.
   *
   * @return flags.
   */
  public List<Flag> build() {
    return flags;
  }
}