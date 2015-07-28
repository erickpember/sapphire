// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.domain.fhir.CodingSystems;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link ProcedureBuilder} test
 */
public class ProcedureBuilderTest {

  private static void addObservation(
      ProcedureBuilder procedureBuilder,
      String observationCode,
      String observationText,
      String observationValue) {

    CodeableConceptDt code = new CodeableConceptDt(CodingSystems.OBSERVATION, observationCode)
        .setText(observationText);

    Observation observation = new Observation()
        .setCode(code)
        .setValue(new StringDt(observationValue));

    procedureBuilder.add(observation);
  }

  @Test
  public void should_create_line_procedure() {
    ProcedureBuilder procedureBuilder = new ProcedureBuilder(new Encounter());
    addObservation(procedureBuilder, "304890077", "Placement Date", "20150208");
    addObservation(procedureBuilder, "304890078", "Placement Time", "181900");
    addObservation(procedureBuilder, "304890094", "Lumens", "2");
    addObservation(procedureBuilder, "304890080", "Line Type-CVC Single Lumen", "Tunneled");
    addObservation(procedureBuilder, "304890099", "Line Type-CVC Single Lumen", "Power");
    addObservation(procedureBuilder, "304890081", "Orientation", "Left");
    addObservation(procedureBuilder, "304890092", "Orientation", "Left");
    addObservation(procedureBuilder, "304890103", "Orientation", "Left");
    addObservation(procedureBuilder, "304890082", "Location", "Femoral");
    addObservation(procedureBuilder, "304890097", "Location", "Femoral");
    addObservation(procedureBuilder, "304890100", "Location", "Arm");
    addObservation(procedureBuilder, "304890104", "Location", "Arm");
    addObservation(procedureBuilder, "304890093", "Access Location", "Arm");
    Procedure procedure = procedureBuilder.build().get();

    assertEquals(procedure.getType().getCodingFirstRep().getCode(), "Tunneled CVC Single Lumen");
    CodeableConceptDt bodySite = (CodeableConceptDt) procedure.getBodySiteFirstRep().getSite();
    assertEquals(bodySite.getCodingFirstRep().getCode(), "Femoral (Left)");
    assertEquals(procedure.getPerformed(), new DateTimeDt("2015-02-08T18:19:00"));
  }
}
