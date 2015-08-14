// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import com.datafascia.domain.event.AdmitPatientData;
import com.datafascia.domain.event.Event;
import com.datafascia.etl.inject.ComponentsModule;
import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.inject.Inject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link MessageToEventService} test
 */
public class MessageToEventServiceTest {

  @Inject
  private MessageToEventService messageToEventService;

  private List<Event> transformMessageToEvents(String hl7File) throws IOException {
    URL url = MessageToEventServiceTest.class.getResource(hl7File);
    String message = Resources.toString(url, StandardCharsets.UTF_8).replace('\n', '\r');
    return messageToEventService.toEvents(message.getBytes(StandardCharsets.UTF_8));
  }

  @BeforeClass
  public void injectMembers() throws Exception {
    Injector injector = Guice.createInjector(new ComponentsModule());
    injector.injectMembers(this);
  }

  @Test
  public void should_extract_gender_female() throws Exception {
    List<Event> events = transformMessageToEvents("gender-female.hl7");
    AdmitPatientData admitPatientData = (AdmitPatientData) events.get(0).getData();
    assertEquals(admitPatientData.getPatient().getGender(), AdministrativeGenderEnum.FEMALE);
  }

  @Test
  public void should_extract_gender_male() throws Exception {
    List<Event> events = transformMessageToEvents("gender-male.hl7");
    AdmitPatientData admitPatientData = (AdmitPatientData) events.get(0).getData();
    assertEquals(admitPatientData.getPatient().getGender(), AdministrativeGenderEnum.MALE);
  }

  @Test
  public void should_extract_gender_other() throws Exception {
    List<Event> events = transformMessageToEvents("gender-other.hl7");
    AdmitPatientData admitPatientData = (AdmitPatientData) events.get(0).getData();
    assertEquals(admitPatientData.getPatient().getGender(), AdministrativeGenderEnum.OTHER);
  }

  @Test
  public void should_extract_gender_unknown() throws Exception {
    List<Event> events = transformMessageToEvents("gender-unknown.hl7");
    AdmitPatientData admitPatientData = (AdmitPatientData) events.get(0).getData();
    assertEquals(admitPatientData.getPatient().getGender(), AdministrativeGenderEnum.UNKNOWN);
  }
}
