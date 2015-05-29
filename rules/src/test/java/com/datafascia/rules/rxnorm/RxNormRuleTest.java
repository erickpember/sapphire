// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.rules.rxnorm;

import com.datafascia.rules.model.RxNorm;
import com.datafascia.rules.util.TestUtil;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests rules to identify records by RxNorm codes.
 */
@Slf4j
public class RxNormRuleTest {

  private static final String DRL_PATH = "com/datafascia/rules/examples/simple/rxnorm.drl";

  /**
   * Tests against a stateless instance of Drools.
   */
  @Test
  public void testStatelessSession() {
    log.info("Starting @Test testStatelessSession()");

    StatelessKieSession session = TestUtil.createStatelessKieSession(DRL_PATH);
    // Add SLF4j Logger as a Global Variable
    session.setGlobal("log", log);

    // Create our 'input' objects, that will be inserted into the Session
    RxNorm chloradiazepoxide = new RxNorm() {
      {
        setRxcuiSCD("905516");
      }
    };

    log.info("Executing Stateless Session...");

    session.execute(Arrays.asList(chloradiazepoxide));

    Assert.assertEquals("Intermittent Chloradiazepoxide Enteral", chloradiazepoxide.getGroupName());

    log.info("===> End of test <===\n");
  }

  /**
   * Tests against a stateful instance of Drools.
   */
  @Test
  public void testStatefulSession() {
    log.info("Starting @Test testStatefulSession()");
    KieSession session = TestUtil.createKieSession(DRL_PATH);

    // Add SLF4j Logger as a Global Variable
    session.setGlobal("log", log);

    RxNorm chloradiazepoxide = new RxNorm() {
      {
        setRxcuiSCD("905516");
      }
    };

    log.info("Inserting objects into Session...");
    // Insert objects into the working memory
    session.insert(chloradiazepoxide);

    // Since rules were not fired, the rxnorm has not been calculated yet
    Assert.assertEquals(null, chloradiazepoxide.getGroupName());

    log.info("Fire all rules!!");
    session.fireAllRules();

    Assert.assertEquals("Intermittent Chloradiazepoxide Enteral", chloradiazepoxide.getGroupName());

    session.dispose();
    log.info("===> End of test <===\n");
  }
}
