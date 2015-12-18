// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.QuestionnaireResponse;
import ca.uhn.fhir.model.dstu2.valueset.QuestionnaireResponseStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.IdentifierSystems;
import java.sql.Date;
import javax.inject.Inject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Integration tests for questionnaire response resources.
 */
public class QuestionnaireResponseIT extends ApiTestSupport {
  @Inject
  private ClientBuilder clientBuilder;

  @Test
  public void should_write_and_read() {
    QuestionnaireResponse qr1 = createQuestionnaireResponse();

    qr1 = clientBuilder.getQuestionnaireResponseClient().create(qr1);
    QuestionnaireResponse qr2 = clientBuilder.getQuestionnaireResponseClient().read(
        qr1.getIdentifier().getValue(), "encounter1");
    assertEquals(qr1.getAuthored().getTime(),
        qr2.getAuthored().getTime());
    assertEquals(qr1.getIdentifier().getSystem(),
        qr2.getIdentifier().getSystem());
    assertEquals(qr1.getIdentifier().getValue(),
        qr2.getIdentifier().getValue());
    assertEquals(qr1.getEncounter().getReference().getIdPart(),
        qr2.getEncounter().getReference().getIdPart());

    assertTrue(clientBuilder.getQuestionnaireResponseClient().search("encounter1").size() > 0);
  }

  public QuestionnaireResponse createQuestionnaireResponse() {
    QuestionnaireResponse qr = new QuestionnaireResponse();
    qr.setAuthored(new DateTimeDt(Date.valueOf("1985-11-05")));
    qr.setIdentifier(new IdentifierDt()
        .setSystem(IdentifierSystems.QUESTIONNAIRE_RESPONSE)
        .setValue("testquestionnaireresponse"));
    qr.setEncounter(new ResourceReferenceDt(clientBuilder.getEncounterClient()
        .getEncounter("encounter1")));
    qr.setStatus(QuestionnaireResponseStatusEnum.AMENDED);
    return qr;
  }
}
