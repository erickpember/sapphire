// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.api.ResourceMetadataKeyEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.Dates;
import com.datafascia.emerge.ucsf.codes.ProcedureCategoryEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
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
    Clock clock = Clock.fixed(Instant.now(), ZoneId.of("America/Los_Angeles"));

    ProcedureBuilder procedureBuilder = new ProcedureBuilder(new Encounter(), clock);
    addObservation(procedureBuilder, "304890002", "Placement Date", "20150208");
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

    assertEquals(procedure.getCode().getCodingFirstRep().getCode(), "Tunneled CVC Single Lumen");
    CodeableConceptDt bodySite = procedure.getBodySiteFirstRep();
    assertEquals(bodySite.getCodingFirstRep().getCode(), "Femoral");
    CodeableConceptDt orientation = procedure.getBodySite().get(1);
    assertEquals(orientation.getCodingFirstRep().getCode(), "Left");
    DateTimeDt expectedPerformed = Dates.toDateTime(
        LocalDate.parse("2015-02-08"), LocalTime.parse("18:19:00"));
    assertEquals(procedure.getPerformed(), expectedPerformed);
    InstantDt updated = ResourceMetadataKeyEnum.UPDATED.get(procedure);
    assertEquals(updated.getValue(), Date.from(Instant.now(clock)));

    assertEquals(procedure.getStatusElement().getValueAsEnum(), ProcedureStatusEnum.IN_PROGRESS);
    CodeableConceptDt category = procedure.getCategory();
    assertEquals(
        category.getCodingFirstRep().getCode(), ProcedureCategoryEnum.CENTRAL_LINE.getCode());
  }
}
