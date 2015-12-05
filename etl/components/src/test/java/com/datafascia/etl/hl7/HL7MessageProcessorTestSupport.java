// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.Terser;
import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.configuration.guice.ConfigureModule;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.IngestMessageRepository;
import com.datafascia.domain.persist.LocationRepository;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.domain.persist.PractitionerRepository;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceUpdater;
import com.datafascia.etl.inject.ComponentsModule;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.util.Modules;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.TimeZone;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.testng.annotations.BeforeMethod;

import static org.mockito.Mockito.mock;

/**
 * Common implementation for tests using {@link HL7MessageProcessor}
 */
public class HL7MessageProcessorTestSupport {

  private static final ZoneId ZONE_ID = ZoneId.of("America/Los_Angeles");

  private static class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(Clock.class)
          .toInstance(Clock.fixed(Instant.now(), ZONE_ID));
      bind(HarmEvidenceUpdater.class)
          .toInstance(mock(HarmEvidenceUpdater.class));
    }

    @Provides
    @Singleton
    public ConnectorFactory connectorFactory() {
      return new ConnectorFactory(
          AccumuloConfiguration.builder()
          .instance(ConnectorFactory.MOCK_INSTANCE)
          .zooKeepers("")
          .user("root")
          .password("secret")
          .build());
    }
  }

  @Inject
  private Parser parser;

  @Inject
  private IngestMessageRepository ingestMessageRepository;

  @Inject
  private HL7MessageProcessor hl7MessageProcessor;

  @Inject
  protected EncounterRepository encounterRepository;

  @Inject
  protected LocationRepository locationRepository;

  @Inject
  protected PatientRepository patientRepository;

  @Inject
  protected PractitionerRepository practitionerRepository;

  @BeforeMethod
  public void injectMembers() throws Exception {
    TimeZone.setDefault(TimeZone.getTimeZone(ZONE_ID));

    Injector injector = Guice.createInjector(
        Modules.override(new ConfigureModule(), new ComponentsModule())
            .with(new TestModule()));
    injector.injectMembers(this);
  }

  protected void processMessage(String hl7File) throws HL7Exception, IOException {
    URL url = Resources.getResource(getClass(), hl7File);
    String hl7 = Resources.toString(url, StandardCharsets.UTF_8).replace('\n', '\r');

    Message message = parser.parse(hl7);
    Terser terser = new Terser(message);
    String encounterIdentifier = terser.get("/.PV1-19");

    ingestMessageRepository.save(Id.of(encounterIdentifier), hl7);

    hl7MessageProcessor.accept(hl7);
  }
}
