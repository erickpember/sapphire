// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.rules.rxnorm;

import com.datafascia.rules.model.RxNorm;
import com.datafascia.rules.util.RuleTrackingEventListener;
import com.datafascia.rules.util.TestUtil;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests rules to identify records by RxNorm codes.
 */
@Slf4j
public class RxNormRuleTest {

  private static final String DRL_PATH = "com/datafascia/rules//rxnorm.drl";

  /**
   * Tests against a stateless instance of Drools.
   */
  @Test
  public void testStatelessSession() {
    log.info("Starting @Test testStatelessSession()");

    RuleTrackingEventListener ruleTracker = new RuleTrackingEventListener();

    StatelessKieSession session = TestUtil.createStatelessKieSession(DRL_PATH);
    // Add SLF4j Logger as a Global Variable
    session.setGlobal("log", log);

    session.addEventListener(ruleTracker);

    // Create our 'input' objects, that will be inserted into the Session
    RxNorm chloradiazepoxide = new RxNorm() {
      {
        setRxcuiSCD("905516");
      }
    };

    RxNorm intermittentHydromorphoneEnteral = new RxNorm() {
      {
        setRxcuiIn("3423");
        setRoute("15");
      }
    };

    RxNorm epidural = new RxNorm() {
      {
        setRxcuiIn("35780");
        setRoute("50");
        setFrequency("200905");
      }
    };

    RxNorm oxycodone = new RxNorm() {
      {
        setRxcuiIn("7804");
        setBrand("Oxycodone");
      }
    };

    RxNorm oxycontin = new RxNorm() {
      {
        setRxcuiIn("161");
        setBrand("Oxycontin");
      }
    };

    RxNorm acetominophen = new RxNorm() {
      {
        setRxcuiSCD("247974");
      }
    };

    log.info("Executing Stateless Session...");

    session.execute(Arrays.asList(chloradiazepoxide, intermittentHydromorphoneEnteral, epidural,
                                  oxycodone, oxycontin, acetominophen));

    assertEquals(chloradiazepoxide.getGroupName(), "Intermittent Chloradiazepoxide Enteral");
    assertEquals(intermittentHydromorphoneEnteral.getMedsGroup(), "UCSF_A18");
    assertEquals(epidural.getMedsGroup(), "UCSF_A8");
    assertTrue(ruleTracker.getFiredRules().contains("Epidural"));
    assertEquals(oxycodone.getMedsGroup(), "UCSF_A24");
    assertEquals(oxycontin.getMedsGroup(), "UCSF_A27");
    assertEquals(acetominophen.getMedsGroup(), "UCSF_A35");
    assertEquals(6, ruleTracker.getCount());

    log.info("===> End of test <===\n");
  }

  /**
   * Tests against a stateful instance of Drools.
   */
  @Test
  public void testStatefulSession() {
    log.info("Starting @Test testStatefulSession()");
    KieSession session = TestUtil.createKieSession(DRL_PATH);

    RuleTrackingEventListener ruleTracker = new RuleTrackingEventListener();
    session.addEventListener(ruleTracker);

    // Add SLF4j Logger as a Global Variable
    session.setGlobal("log", log);

    ArrayList<RxNorm> meds = new ArrayList<>();
    RxNorm ucsfa2 = new RxNorm() {
      {
        setRxcuiIn("319864");
        setFrequency("200553");
      }
    };
    meds.add(ucsfa2);
    RxNorm ucsfa3 = new RxNorm() {
      {
        setRxcuiIn("71535");
        setFrequency("200553");
      }
    };
    meds.add(ucsfa3);
    RxNorm ucsfa4 = new RxNorm() {
      {
        setRxcuiIn("68139");
        setFrequency("200553");
      }
    };
    meds.add(ucsfa4);
    RxNorm ucsfa5 = new RxNorm() {
      {
        setRxcuiIn("7883");
        setFrequency("200553");
      }
    };
    meds.add(ucsfa5);
    RxNorm ucsfa6 = new RxNorm() {
      {
        setRxcuiIn("319864");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa6);
    RxNorm ucsfa7 = new RxNorm() {
      {
        setRxcuiIn("71535");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa7);
    RxNorm ucsfa8 = new RxNorm() {
      {
        setRxcuiIn("1815");
        setFrequency("200905");
        setRoute("50");
      }
    };
    meds.add(ucsfa8);
    RxNorm ucsfa9 = new RxNorm() {
      {
        setRxcuiIn("1815");
        setFrequency("200905");
        setRoute("157");
      }
    };
    meds.add(ucsfa9);
    RxNorm ucsfa10 = new RxNorm() {
      {
        setRxcuiIn("4337");
        setFrequency("200905");
        setRoute("11");
        setPca("yes");
      }
    };
    meds.add(ucsfa10);
    RxNorm ucsfa11 = new RxNorm() {
      {
        setRxcuiIn("3423");
        setFrequency("200905");
        setRoute("11");
        setPca("yes");
      }
    };
    meds.add(ucsfa11);
    RxNorm ucsfa12 = new RxNorm() {
      {
        setRxcuiIn("7052");
        setFrequency("200905");
        setRoute("11");
        setPca("yes");
      }
    };
    meds.add(ucsfa12);
    RxNorm ucsfa13 = new RxNorm() {
      {
        setRxcuiIn("4337");
        setFrequency("200553");
        setRoute("11");
      }
    };
    meds.add(ucsfa13);
    RxNorm ucsfa14 = new RxNorm() {
      {
        setRxcuiIn("4337");
        setFrequency("200905");
        setRoute("11");
        setPca("no");
      }
    };
    meds.add(ucsfa14);
    RxNorm ucsfa15 = new RxNorm() {
      {
        setRxcuiIn("4337");
        setRoute("20");
      }
    };
    meds.add(ucsfa15);
    RxNorm ucsfa16 = new RxNorm() {
      {
        setRxcuiIn("3423");
        setFrequency("200553");
        setRoute("11");
      }
    };
    meds.add(ucsfa16);
    RxNorm ucsfa17 = new RxNorm() {
      {
        setRxcuiIn("3423");
        setFrequency("200905");
        setRoute("11");
        setPca("no");
      }
    };
    meds.add(ucsfa17);
    RxNorm ucsfa18 = new RxNorm() {
      {
        setRxcuiIn("3423");
        setRoute("15");
      }
    };
    meds.add(ucsfa18);
    RxNorm ucsfa19 = new RxNorm() {
      {
        setRxcuiIn("73032");
        setFrequency("200905");
        setRoute("11");
        setPca("no");
      }
    };
    meds.add(ucsfa19);
    RxNorm ucsfa20 = new RxNorm() {
      {
        setRxcuiIn("7052");
        setFrequency("200553");
        setRoute("11");
      }
    };
    meds.add(ucsfa20);
    RxNorm ucsfa21 = new RxNorm() {
      {
        setRxcuiIn("7052");
        setFrequency("200905");
        setRoute("11");
        setPca("no");
      }
    };
    meds.add(ucsfa21);
    RxNorm ucsfa22 = new RxNorm() {
      {
        setRxcuiIn("7052");
        setBrand("Made-up");
        setRoute("15");
      }
    };
    meds.add(ucsfa22);

    // A22 specifies "verify in testing" on brand name, which when assigned Kadian trips rule 23.
    RxNorm ucsfa23 = new RxNorm() {
      {
        setRxcuiIn("7052");
        setRoute("15");
        setBrand("Kadian");
      }
    };
    meds.add(ucsfa23);
    RxNorm ucsfa24 = new RxNorm() {
      {
        setRxcuiIn("7804");
        setBrand("Oxycodone");
      }
    };
    meds.add(ucsfa24);

    // Rule 24 specifies "verify in testing" to exclude brand OxyContin, which trips rule 25.
    RxNorm ucsfa25 = new RxNorm() {
      {
        setRxcuiIn("7804");
        setBrand("OxyContin");
      }
    };
    meds.add(ucsfa25);
    RxNorm ucsfa26 = new RxNorm() {
      {
        setRxcuiIn("7804");
        // We need to be provided a brand here! Guessing Percocet!
        setBrand("Percocet");
      }
    };
    meds.add(ucsfa26);
    RxNorm ucsfa27 = new RxNorm() {
      {
        setRxcuiIn("5489");
      }
    };
    meds.add(ucsfa27);
    RxNorm ucsfa28 = new RxNorm() {
      {
        setRxcuiIn("6813");
        setRoute("11");
      }
    };
    meds.add(ucsfa28);
    RxNorm ucsfa29 = new RxNorm() {
      {
        setRxcuiIn("6813");
        setRoute("15");
      }
    };
    meds.add(ucsfa29);
    RxNorm ucsfa30 = new RxNorm() {
      {
        setRxcuiSCD("836408");
      }
    };
    meds.add(ucsfa30);
    RxNorm ucsfa31 = new RxNorm() {
      {
        setRxcuiSCD("310963");
      }
    };
    meds.add(ucsfa31);
    RxNorm ucsfa32 = new RxNorm() {
      {
        setRxcuiIn("140587");
      }
    };
    meds.add(ucsfa32);
    RxNorm ucsfa33 = new RxNorm() {
      {
        setRxcuiIn("35827");
        setRoute("11");
      }
    };
    meds.add(ucsfa33);
    RxNorm ucsfa34 = new RxNorm() {
      {
        setRxcuiSCD("993755");
      }
    };
    meds.add(ucsfa34);
    RxNorm ucsfa35 = new RxNorm() {
      {
        setRxcuiSCD("1148399");
      }
    };
    meds.add(ucsfa35);
    RxNorm ucsfa36 = new RxNorm() {
      {
        setRxcuiIn("161");
        setRoute("11");
      }
    };
    meds.add(ucsfa36);
    RxNorm ucsfa37 = new RxNorm() {
      {
        setRxcuiIn("161");
        setRoute("17");
      }
    };
    meds.add(ucsfa37);
    RxNorm ucsfa38 = new RxNorm() {
      {
        setRxcuiIn("25480");
        setRoute("15");
      }
    };
    meds.add(ucsfa38);
    RxNorm ucsfa39 = new RxNorm() {
      {
        setRxcuiIn("187832");
      }
    };
    meds.add(ucsfa39);
    RxNorm ucsfa40 = new RxNorm() {
      {
        setRxcuiIn("6130");
        setRoute("15");
      }
    };
    meds.add(ucsfa40);
    RxNorm ucsfa41 = new RxNorm() {
      {
        setRxcuiIn("6130");
        setFrequency("200905");
        setRoute("11");
        setPca("no");
      }
    };
    meds.add(ucsfa41);
    RxNorm ucsfa42 = new RxNorm() {
      {
        setRxcuiIn("6130");
        setFrequency("200553");
        setRoute("11");
      }
    };
    meds.add(ucsfa42);
    RxNorm ucsfa43 = new RxNorm() {
      {
        setRxcuiSCD("1009464");
      }
    };
    meds.add(ucsfa43);
    RxNorm ucsfa44 = new RxNorm() {
      {
        setFrequency("200553");
        setRxcuiSCD("892477");
      }
    };
    meds.add(ucsfa44);
    RxNorm ucsfa45 = new RxNorm() {
      {
        setFrequency("200905");
        setRoute("9");
      }
    };
    meds.add(ucsfa45);
    RxNorm ucsfa46 = new RxNorm() {
      {
        setRxcuiSCD("756245");
      }
    };
    meds.add(ucsfa46);
    RxNorm ucsfa47 = new RxNorm() {
      {
        setRxcuiIn("8782");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa47);
    RxNorm ucsfa48 = new RxNorm() {
      {
        setRxcuiIn("48937");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa48);
    RxNorm ucsfa49 = new RxNorm() {
      {
        setRxcuiIn("6470");
        setFrequency("200553");
        setRoute("11");
      }
    };
    meds.add(ucsfa49);
    RxNorm ucsfa50 = new RxNorm() {
      {
        setRxcuiIn("6470");
        setFrequency("n/a");
        setRoute("15");
      }
    };
    meds.add(ucsfa50);
    RxNorm ucsfa51 = new RxNorm() {
      {
        setRxcuiIn("6470");
        setFrequency("200905");
        setRoute("11");
      }
    };
    meds.add(ucsfa51);
    RxNorm ucsfa52 = new RxNorm() {
      {
        setRxcuiIn("6960");
        setFrequency("200553");
        setRoute("11");
      }
    };
    meds.add(ucsfa52);
    RxNorm ucsfa53 = new RxNorm() {
      {
        setRxcuiIn("6960");
        setFrequency("200905");
        setRoute("11");
      }
    };
    meds.add(ucsfa53);
    RxNorm ucsfa54 = new RxNorm() {
      {
        setRxcuiIn("2598");
      }
    };
    meds.add(ucsfa54);
    RxNorm ucsfa55 = new RxNorm() {
      {
        setRxcuiIn("3322");
        setRoute("11");
      }
    };
    meds.add(ucsfa55);
    RxNorm ucsfa56 = new RxNorm() {
      {
        setRxcuiIn("3322");
        setRoute("17");
      }
    };
    meds.add(ucsfa56);
    RxNorm ucsfa57 = new RxNorm() {
      {
        setRxcuiSCD("905369");
      }
    };
    meds.add(ucsfa57);
    RxNorm ucsfa58 = new RxNorm() {
      {
        setRxcuiIn("596");
      }
    };
    meds.add(ucsfa58);
    RxNorm ucsfa59 = new RxNorm() {
      {
        setRxcuiIn("11289");
      }
    };
    meds.add(ucsfa59);
    RxNorm ucsfa60 = new RxNorm() {
      {
        setRxcuiIn("15202");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa60);
    RxNorm ucsfa61 = new RxNorm() {
      {
        setRxcuiIn("60819");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa61);
    RxNorm ucsfa62 = new RxNorm() {
      {
        setRxcuiIn("1546356");
      }
    };
    meds.add(ucsfa62);
    RxNorm ucsfa63 = new RxNorm() {
      {
        setRxcuiIn("1364430");
      }
    };
    meds.add(ucsfa63);
    RxNorm ucsfa64 = new RxNorm() {
      {
        setRxcuiIn("1114195");
      }
    };
    meds.add(ucsfa64);
    RxNorm ucsfa65 = new RxNorm() {
      {
        setRxcuiIn("1599538");
      }
    };
    meds.add(ucsfa65);
    RxNorm ucsfa66 = new RxNorm() {
      {
        setRxcuiIn("321208");
      }
    };
    meds.add(ucsfa66);
    RxNorm ucsfa67 = new RxNorm() {
      {
        setRxcuiIn("67108");
        setFrequency("q12h");
      }
    };
    meds.add(ucsfa67);
    RxNorm ucsfa68 = new RxNorm() {
      {
        setDrugId("206328");
      }
    };
    meds.add(ucsfa68);
    RxNorm ucsfa69 = new RxNorm() {
      {
        setRxcuiIn("235473");
        setRoute("subcutaneous");
      }
    };
    meds.add(ucsfa69);
    RxNorm ucsfa70 = new RxNorm() {
      {
        setAhfs("56:28.12");
      }
    };
    meds.add(ucsfa70);
    RxNorm ucsfa71 = new RxNorm() {
      {
        setRxcuiIn("3628");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa71);
    RxNorm ucsfa72 = new RxNorm() {
      {
        setRxcuiIn("3992");
        setFrequency("200905");
        setRoute("11");
      }
    };
    meds.add(ucsfa72);
    RxNorm ucsfa73 = new RxNorm() {
      {
        setRxcuiIn("7512");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa73);
    RxNorm ucsfa74 = new RxNorm() {
      {
        setRxcuiIn("8163");
        setFrequency("200905");
        setRoute("11");
      }
    };
    meds.add(ucsfa74);
    RxNorm ucsfa75 = new RxNorm() {
      {
        setRxcuiIn("11149");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa75);
    RxNorm ucsfa76 = new RxNorm() {
      {
        setRxcuiIn("3616");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa76);
    RxNorm ucsfa77 = new RxNorm() {
      {
        setRxcuiIn("52769");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa77);
    RxNorm ucsfa78 = new RxNorm() {
      {
        setRxcuiIn("6054");
        setFrequency("200905");
        setRoute("11");
      }
    };
    meds.add(ucsfa78);
    RxNorm ucsfa79 = new RxNorm() {
      {
        setRoute("11");
        setAhfs("10:00.00");
      }
    };
    meds.add(ucsfa79);
    RxNorm ucsfa80 = new RxNorm() {
      {
        setDrugId("40801561");
      }
    };
    meds.add(ucsfa80);
    RxNorm ucsfa81 = new RxNorm() {
      {
        setRxcuiIn("8814");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa81);
    RxNorm ucsfa82 = new RxNorm() {
      {
        setRxcuiIn("343048");
        setFrequency("200905");
        setRoute("11");
      }
    };
    meds.add(ucsfa82);
    RxNorm ucsfa83 = new RxNorm() {
      {
        setRoute("15");
        setRxcuiSCD("1247386");
      }
    };
    meds.add(ucsfa83);
    RxNorm ucsfa84 = new RxNorm() {
      {
        setRxcuiIn("32968");
      }
    };
    meds.add(ucsfa84);
    RxNorm ucsfa85 = new RxNorm() {
      {
        setRxcuiIn("613391");
      }
    };
    meds.add(ucsfa85);
    RxNorm ucsfa86 = new RxNorm() {
      {
        setRxcuiIn("1116632");
      }
    };
    meds.add(ucsfa86);
    RxNorm ucsfa87 = new RxNorm() {
      {
        setRxcuiIn("1191");
        setRoute("17");
      }
    };
    meds.add(ucsfa87);
    RxNorm ucsfa88 = new RxNorm() {
      {
        setRoute("11");
        setRxcuiSCD("238755");
      }
    };
    meds.add(ucsfa88);
    RxNorm ucsfa89 = new RxNorm() {
      {
        setRxcuiIn("8640");
      }
    };
    meds.add(ucsfa89);
    RxNorm ucsfa90 = new RxNorm() {
      {
        setRxcuiIn("6902");
        setRoute("11");
      }
    };
    meds.add(ucsfa90);
    RxNorm ucsfa91 = new RxNorm() {
      {
        setRxcuiIn("3264");
        setRoute("11");
      }
    };
    meds.add(ucsfa91);
    RxNorm ucsfa92 = new RxNorm() {
      {
        setRxcuiIn("2878");
      }
    };
    meds.add(ucsfa92);
    RxNorm ucsfa93 = new RxNorm() {
      {
        setRxcuiIn("8638");
        setRoute("15");
      }
    };
    meds.add(ucsfa93);

    // Insert objects into the working memory
    for (RxNorm med : meds) {
      session.insert(med);
    }

    // Since rules were not fired, the rxnorm has not been calculated yet
    assertEquals(null, ucsfa2.getGroupName());

    log.info("Fire all rules!!");
    session.fireAllRules();

    assertEquals(ucsfa2.getMedsGroup(), "UCSF_A2");
    assertEquals(ucsfa3.getMedsGroup(), "UCSF_A3");
    assertEquals(ucsfa4.getMedsGroup(), "UCSF_A4");
    assertEquals(ucsfa5.getMedsGroup(), "UCSF_A5");
    assertEquals(ucsfa6.getMedsGroup(), "UCSF_A6");
    assertEquals(ucsfa7.getMedsGroup(), "UCSF_A7");
    assertEquals(ucsfa8.getMedsGroup(), "UCSF_A8");
    assertEquals(ucsfa9.getMedsGroup(), "UCSF_A9");
    assertEquals(ucsfa10.getMedsGroup(), "UCSF_A10");
    assertEquals(ucsfa11.getMedsGroup(), "UCSF_A11");
    assertEquals(ucsfa12.getMedsGroup(), "UCSF_A12");
    assertEquals(ucsfa13.getMedsGroup(), "UCSF_A13");
    assertEquals(ucsfa14.getMedsGroup(), "UCSF_A14");
    assertEquals(ucsfa15.getMedsGroup(), "UCSF_A15");
    assertEquals(ucsfa16.getMedsGroup(), "UCSF_A16");
    assertEquals(ucsfa17.getMedsGroup(), "UCSF_A17");
    assertEquals(ucsfa18.getMedsGroup(), "UCSF_A18");
    assertEquals(ucsfa19.getMedsGroup(), "UCSF_A19");
    assertEquals(ucsfa20.getMedsGroup(), "UCSF_A20");
    assertEquals(ucsfa21.getMedsGroup(), "UCSF_A21");
    assertEquals(ucsfa22.getMedsGroup(), "UCSF_A22");
    assertEquals(ucsfa23.getMedsGroup(), "UCSF_A23");
    assertEquals(ucsfa24.getMedsGroup(), "UCSF_A24");
    assertEquals(ucsfa25.getMedsGroup(), "UCSF_A25");
    assertEquals(ucsfa26.getMedsGroup(), "UCSF_A26");
    assertEquals(ucsfa27.getMedsGroup(), "UCSF_A27");
    assertEquals(ucsfa28.getMedsGroup(), "UCSF_A28");
    assertEquals(ucsfa29.getMedsGroup(), "UCSF_A29");
    assertEquals(ucsfa30.getMedsGroup(), "UCSF_A30");
    assertEquals(ucsfa31.getMedsGroup(), "UCSF_A31");
    assertEquals(ucsfa32.getMedsGroup(), "UCSF_A32");
    assertEquals(ucsfa33.getMedsGroup(), "UCSF_A33");
    assertEquals(ucsfa34.getMedsGroup(), "UCSF_A34");
    assertEquals(ucsfa35.getMedsGroup(), "UCSF_A35");
    assertEquals(ucsfa36.getMedsGroup(), "UCSF_A36");
    assertEquals(ucsfa37.getMedsGroup(), "UCSF_A37");
    assertEquals(ucsfa38.getMedsGroup(), "UCSF_A38");
    assertEquals(ucsfa39.getMedsGroup(), "UCSF_A39");
    assertEquals(ucsfa40.getMedsGroup(), "UCSF_A40");
    assertEquals(ucsfa41.getMedsGroup(), "UCSF_A41");
    assertEquals(ucsfa42.getMedsGroup(), "UCSF_A42");
    assertEquals(ucsfa43.getMedsGroup(), "UCSF_A43");
    assertEquals(ucsfa44.getMedsGroup(), "UCSF_A44");
    assertEquals(ucsfa45.getMedsGroup(), "UCSF_A45");
    assertEquals(ucsfa46.getMedsGroup(), "UCSF_A46");
    assertEquals(ucsfa47.getMedsGroup(), "UCSF_A47");
    assertEquals(ucsfa48.getMedsGroup(), "UCSF_A48");
    assertEquals(ucsfa49.getMedsGroup(), "UCSF_A49");
    assertEquals(ucsfa50.getMedsGroup(), "UCSF_A50");
    assertEquals(ucsfa51.getMedsGroup(), "UCSF_A51");
    assertEquals(ucsfa52.getMedsGroup(), "UCSF_A52");
    assertEquals(ucsfa53.getMedsGroup(), "UCSF_A53");
    assertEquals(ucsfa54.getMedsGroup(), "UCSF_A54");
    assertEquals(ucsfa55.getMedsGroup(), "UCSF_A55");
    assertEquals(ucsfa56.getMedsGroup(), "UCSF_A56");
    assertEquals(ucsfa57.getMedsGroup(), "UCSF_A57");
    assertEquals(ucsfa58.getMedsGroup(), "UCSF_A58");
    assertEquals(ucsfa59.getMedsGroup(), "UCSF_A59");
    assertEquals(ucsfa60.getMedsGroup(), "UCSF_A60");
    assertEquals(ucsfa61.getMedsGroup(), "UCSF_A61");
    assertEquals(ucsfa62.getMedsGroup(), "UCSF_A62");
    assertEquals(ucsfa63.getMedsGroup(), "UCSF_A63");
    assertEquals(ucsfa64.getMedsGroup(), "UCSF_A64");
    assertEquals(ucsfa65.getMedsGroup(), "UCSF_A65");
    assertEquals(ucsfa66.getMedsGroup(), "UCSF_A66");
    assertEquals(ucsfa67.getMedsGroup(), "UCSF_A67");
    assertEquals(ucsfa68.getMedsGroup(), "UCSF_A68");
    assertEquals(ucsfa69.getMedsGroup(), "UCSF_A69");
    assertEquals(ucsfa70.getMedsGroup(), "UCSF_A70");
    assertEquals(ucsfa71.getMedsGroup(), "UCSF_A71");
    assertEquals(ucsfa72.getMedsGroup(), "UCSF_A72");
    assertEquals(ucsfa73.getMedsGroup(), "UCSF_A73");
    assertEquals(ucsfa74.getMedsGroup(), "UCSF_A74");
    assertEquals(ucsfa75.getMedsGroup(), "UCSF_A75");
    assertEquals(ucsfa76.getMedsGroup(), "UCSF_A76");
    assertEquals(ucsfa77.getMedsGroup(), "UCSF_A77");
    assertEquals(ucsfa78.getMedsGroup(), "UCSF_A78");
    assertEquals(ucsfa79.getMedsGroup(), "UCSF_A79");
    assertEquals(ucsfa80.getMedsGroup(), "UCSF_A80");
    assertEquals(ucsfa81.getMedsGroup(), "UCSF_A81");
    assertEquals(ucsfa82.getMedsGroup(), "UCSF_A82");
    assertEquals(ucsfa83.getMedsGroup(), "UCSF_A83");
    assertEquals(ucsfa84.getMedsGroup(), "UCSF_A84");
    assertEquals(ucsfa85.getMedsGroup(), "UCSF_A85");
    assertEquals(ucsfa86.getMedsGroup(), "UCSF_A86");
    assertEquals(ucsfa87.getMedsGroup(), "UCSF_A87");
    assertEquals(ucsfa88.getMedsGroup(), "UCSF_A88");
    assertEquals(ucsfa89.getMedsGroup(), "UCSF_A89");
    assertEquals(ucsfa90.getMedsGroup(), "UCSF_A90");
    assertEquals(ucsfa91.getMedsGroup(), "UCSF_A91");
    assertEquals(ucsfa92.getMedsGroup(), "UCSF_A92");
    assertEquals(ucsfa93.getMedsGroup(), "UCSF_A93");

    log.warn("RULES A26 A24 AND A50 HAVE KNOWN PROBLEMS. Duplicate firing detection has been"
        + " DISABLED.");

    session.dispose();
    log.info("===> End of test <===\n");
  }
}
