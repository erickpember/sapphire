// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.dstu2.resource.Questionnaire;
import ca.uhn.fhir.model.dstu2.valueset.AnswerFormatEnum;
import ca.uhn.fhir.model.dstu2.valueset.QuestionnaireStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.ResourceTypeEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Integration tests for questionnaire resources.
 */
@Slf4j
public class QuestionnaireIT extends ApiTestSupport {
  @Inject
  private ClientBuilder clientBuilder;

  @Test
  public void should_write_and_read() {
    Questionnaire questionnaire1 = createQuestionnaire();
    clientBuilder.getQuestionnaireClient().saveQuestionnaire(questionnaire1);

    Questionnaire questionnaire2
        = clientBuilder.getQuestionnaireClient().getQuestionnaire(
            questionnaire1.getIdentifierFirstRep().getValue());

    assertEquals(questionnaire1.getIdentifierFirstRep().getValue(),
        questionnaire2.getIdentifierFirstRep().getValue());
    assertEquals(questionnaire1.getIdentifierFirstRep().getSystem(),
        questionnaire2.getIdentifierFirstRep().getSystem());
    assertEquals(questionnaire1.getSubjectType(), questionnaire2.getSubjectType());
    assertEquals(questionnaire1.getVersion(), questionnaire2.getVersion());
    assertEquals(questionnaire1.getStatus(), questionnaire2.getStatus());
    assertEquals(questionnaire1.getPublisher(), questionnaire2.getPublisher());
    assertEquals(questionnaire1.getDate().toInstant().getEpochSecond(),
        questionnaire2.getDate().toInstant().getEpochSecond());
    assertEquals(questionnaire1.getGroup().getConceptFirstRep().getSystem(),
        questionnaire2.getGroup().getConceptFirstRep().getSystem());
    assertEquals(questionnaire1.getGroup().getConceptFirstRep().getCode(),
        questionnaire2.getGroup().getConceptFirstRep().getCode());
    assertEquals(questionnaire1.getGroup().getText(), questionnaire2.getGroup().getText());
    assertEquals(questionnaire1.getGroup().getTitle(), questionnaire2.getGroup().getTitle());
    assertEquals(questionnaire1.getGroup().getQuestionFirstRep().getText(),
        questionnaire2.getGroup().getQuestionFirstRep().getText());
  }

  public static Questionnaire createQuestionnaire() {
    Questionnaire.Group group = new Questionnaire.Group();
    group.addConcept().setSystem(CodingSystems.QUESTIONNAIRE_CONCEPT).setCode("000001");
    group.setText("A test designed to distinguish replicants from humans based on their emotional"
        + "response to questions.");
    group.setTitle("Voight-Kampff test");
    populateGroupQuestion(group.addQuestion(),
        "It’s your birthday. Someone gives you a calfskin wallet. How do you react?",
        AnswerFormatEnum.STRING, false, true);
    populateGroupQuestion(group.addQuestion(),
        "You’ve got a little boy. He shows you his butterfly collection plus the killing jar. "
        + "What do you do?",
        AnswerFormatEnum.TEXT, false, false);
    populateGroupQuestion(group.addQuestion(),
        "You’re in a desert walking along in the sand when all of the sudden you look down, and"
        + " you see a tortoise, it’s crawling toward you. You reach down, you flip the tortoise"
        + " over on its back. The tortoise lays on its back, its belly baking in the hot sun,"
        + " beating its legs trying to turn itself over, but it can’t, not without your help."
        + " But you’re not helping. Why is that?",
        AnswerFormatEnum.INTEGER, false, true);
    Questionnaire.GroupQuestion choiceBased = populateGroupQuestion(group.addQuestion(),
        "You’re watching television. Suddenly you realize there’s a wasp crawling on your arm.",
        AnswerFormatEnum.CHOICE, false, true);
    choiceBased.addOption().setSystem(CodingSystems.QUESTIONNAIRE_OPTION).setCode("Kill it!");
    choiceBased.addOption().setSystem(CodingSystems.QUESTIONNAIRE_OPTION).setCode("Watch it.");
    choiceBased.addOption().setSystem(CodingSystems.QUESTIONNAIRE_OPTION)
        .setCode("Call a wasp handler.");

    Questionnaire questionnaire = new Questionnaire();
    questionnaire.addIdentifier()
        .setSystem(IdentifierSystems.QUESTIONNAIRE)
        .setValue("testquestionnaire");
    questionnaire.setDate(DateTimeDt.withCurrentTime());
    questionnaire.setPublisher("Tyrell Corporation");
    questionnaire.setStatus(QuestionnaireStatusEnum.DRAFT);
    questionnaire.setSubjectType(ResourceTypeEnum.QUESTIONNAIRE);
    questionnaire.setVersion("v1.0");
    questionnaire.setGroup(group);
    return questionnaire;
  }

  public static Questionnaire.GroupQuestion populateGroupQuestion(
      Questionnaire.GroupQuestion question, String text, AnswerFormatEnum format,
      boolean repeats, boolean required) {
    question.addConcept().setSystem(CodingSystems.QUESTIONNAIRE_CONCEPT).setCode("000001");
    question.setType(format);
    question.setText(text);
    question.setRepeats(repeats);
    question.setRequired(required);
    return question;
  }
}
