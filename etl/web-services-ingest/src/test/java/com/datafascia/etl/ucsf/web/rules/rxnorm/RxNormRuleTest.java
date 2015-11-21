// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web.rules.rxnorm;

import com.datafascia.etl.ucsf.web.rules.model.MedsSet;
import com.datafascia.etl.ucsf.web.rules.model.RxNorm;
import com.datafascia.etl.ucsf.web.rules.util.RuleTrackingEventListener;
import com.datafascia.etl.ucsf.web.rules.util.TestUtil;
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
  private static final String DRL_PATH = "com/datafascia/etl/ucsf/web/rules/rxnorm.drl";

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
        getRxcuiIn().add("3423");
        setRoute("15");
      }
    };

    RxNorm epidural = new RxNorm() {
      {
        getRxcuiIn().add("35780");
        setRoute("50");
        setFrequency("200905");
      }
    };

    RxNorm oxycodone = new RxNorm() {
      {
        getRxcuiIn().add("7804");
        setBrand("Oxycodone");
      }
    };

    RxNorm oxycontin = new RxNorm() {
      {
        getRxcuiIn().add("161");
        getRxcuiIn().add("5489");
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

    assertEquals(chloradiazepoxide.getMedsSets().get(0).getName(),
        "Intermittent Chloradiazepoxide Enteral");
    assertEquals(intermittentHydromorphoneEnteral.getMedsSets().get(0).getCode(), "UCSF_A18");
    assertEquals(epidural.getMedsSets().get(0).getCode(), "UCSF_A8");
    assertTrue(ruleTracker.getFiredRules().contains("Epidural"));
    assertEquals(oxycodone.getMedsSets().get(0).getCode(), "UCSF_A24");
    assertEquals(oxycontin.getMedsSets().get(0).getCode(), "UCSF_A27");
    assertEquals(acetominophen.getMedsSets().get(0).getCode(), "UCSF_A35");
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
        getRxcuiIn().add("319864");
        setFrequency("200553");
      }
    };
    meds.add(ucsfa2);
    RxNorm ucsfa3 = new RxNorm() {
      {
        getRxcuiIn().add("71535");
        setFrequency("200553");
      }
    };
    meds.add(ucsfa3);
    RxNorm ucsfa4 = new RxNorm() {
      {
        getRxcuiIn().add("68139");
        setFrequency("200553");
      }
    };
    meds.add(ucsfa4);
    RxNorm ucsfa5 = new RxNorm() {
      {
        getRxcuiIn().add("7883");
        setFrequency("200553");
      }
    };
    meds.add(ucsfa5);
    RxNorm ucsfa6 = new RxNorm() {
      {
        getRxcuiIn().add("319864");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa6);
    RxNorm ucsfa7 = new RxNorm() {
      {
        getRxcuiIn().add("71535");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa7);
    RxNorm ucsfa8 = new RxNorm() {
      {
        getRxcuiIn().add("1815");
        setFrequency("200905");
        setRoute("50");
      }
    };
    meds.add(ucsfa8);
    RxNorm ucsfa9 = new RxNorm() {
      {
        getRxcuiIn().add("1815");
        setFrequency("200905");
        setRoute("157");
      }
    };
    meds.add(ucsfa9);
    RxNorm ucsfa10 = new RxNorm() {
      {
        getRxcuiIn().add("4337");
        setFrequency("200905");
        setRoute("11");
        setPca("yes");
      }
    };
    meds.add(ucsfa10);
    RxNorm ucsfa11 = new RxNorm() {
      {
        getRxcuiIn().add("3423");
        setFrequency("200905");
        setRoute("11");
        setPca("yes");
      }
    };
    meds.add(ucsfa11);
    RxNorm ucsfa12 = new RxNorm() {
      {
        getRxcuiIn().add("7052");
        setFrequency("200905");
        setRoute("11");
        setPca("yes");
      }
    };
    meds.add(ucsfa12);
    RxNorm ucsfa13 = new RxNorm() {
      {
        getRxcuiIn().add("4337");
        setFrequency("200553");
        setRoute("11");
      }
    };
    meds.add(ucsfa13);
    RxNorm ucsfa14 = new RxNorm() {
      {
        getRxcuiIn().add("4337");
        setFrequency("200905");
        setRoute("11");
        setPca("no");
      }
    };
    meds.add(ucsfa14);
    RxNorm ucsfa15 = new RxNorm() {
      {
        getRxcuiIn().add("4337");
        setRoute("20");
      }
    };
    meds.add(ucsfa15);
    RxNorm ucsfa16 = new RxNorm() {
      {
        getRxcuiIn().add("3423");
        setFrequency("200553");
        setRoute("11");
      }
    };
    meds.add(ucsfa16);
    RxNorm ucsfa17 = new RxNorm() {
      {
        getRxcuiIn().add("3423");
        setFrequency("200905");
        setRoute("11");
        setPca("no");
      }
    };
    meds.add(ucsfa17);
    RxNorm ucsfa18 = new RxNorm() {
      {
        getRxcuiIn().add("3423");
        setRoute("15");
      }
    };
    meds.add(ucsfa18);
    RxNorm ucsfa19 = new RxNorm() {
      {
        getRxcuiIn().add("73032");
        setFrequency("200905");
        setRoute("11");
        setPca("no");
      }
    };
    meds.add(ucsfa19);
    RxNorm ucsfa20 = new RxNorm() {
      {
        getRxcuiIn().add("7052");
        setFrequency("200553");
        setRoute("11");
      }
    };
    meds.add(ucsfa20);
    RxNorm ucsfa21 = new RxNorm() {
      {
        getRxcuiIn().add("7052");
        setFrequency("200905");
        setRoute("11");
        setPca("no");
      }
    };
    meds.add(ucsfa21);
    RxNorm ucsfa22 = new RxNorm() {
      {
        getRxcuiIn().add("7052");
        setBrand("Made-up");
        setRoute("15");
      }
    };
    meds.add(ucsfa22);

    // A22 specifies "verify in testing" on brand name, which when assigned Kadian trips rule 23.
    RxNorm ucsfa23 = new RxNorm() {
      {
        getRxcuiIn().add("7052");
        setRoute("15");
        setBrand("Kadian");
      }
    };
    meds.add(ucsfa23);
    RxNorm ucsfa24 = new RxNorm() {
      {
        getRxcuiIn().add("7804");
        setBrand("Oxycodone");
      }
    };
    meds.add(ucsfa24);

    // Rule 24 specifies "verify in testing" to exclude brand OxyContin, which trips rule 25.
    RxNorm ucsfa25 = new RxNorm() {
      {
        getRxcuiIn().add("7804");
        setBrand("OxyContin");
      }
    };
    meds.add(ucsfa25);
    RxNorm ucsfa26 = new RxNorm() {
      {
        getRxcuiIn().add("7804");
        getRxcuiIn().add("161");
      }
    };
    meds.add(ucsfa26);
    RxNorm ucsfa27 = new RxNorm() {
      {
        getRxcuiIn().add("5489");
        getRxcuiIn().add("161");
      }
    };
    meds.add(ucsfa27);
    RxNorm ucsfa28 = new RxNorm() {
      {
        getRxcuiIn().add("6813");
        setRoute("11");
      }
    };
    meds.add(ucsfa28);
    RxNorm ucsfa29 = new RxNorm() {
      {
        getRxcuiIn().add("6813");
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
        getRxcuiIn().add("140587");
      }
    };
    meds.add(ucsfa32);
    RxNorm ucsfa33 = new RxNorm() {
      {
        getRxcuiIn().add("35827");
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
        getRxcuiIn().add("161");
        setRoute("11");
      }
    };
    meds.add(ucsfa36);
    RxNorm ucsfa37 = new RxNorm() {
      {
        getRxcuiIn().add("161");
        setRoute("17");
      }
    };
    meds.add(ucsfa37);
    RxNorm ucsfa38 = new RxNorm() {
      {
        getRxcuiIn().add("25480");
        setRoute("15");
      }
    };
    meds.add(ucsfa38);
    RxNorm ucsfa39 = new RxNorm() {
      {
        getRxcuiIn().add("187832");
      }
    };
    meds.add(ucsfa39);
    RxNorm ucsfa40 = new RxNorm() {
      {
        getRxcuiIn().add("6130");
        setRoute("15");
      }
    };
    meds.add(ucsfa40);
    RxNorm ucsfa41 = new RxNorm() {
      {
        getRxcuiIn().add("6130");
        setFrequency("200905");
        setRoute("11");
        setPca("no");
      }
    };
    meds.add(ucsfa41);
    RxNorm ucsfa42 = new RxNorm() {
      {
        getRxcuiIn().add("6130");
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
        getRxcuiIn().add("8782");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa47);
    RxNorm ucsfa48 = new RxNorm() {
      {
        getRxcuiIn().add("48937");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa48);
    RxNorm ucsfa49 = new RxNorm() {
      {
        getRxcuiIn().add("6470");
        setFrequency("200553");
        setRoute("11");
      }
    };
    meds.add(ucsfa49);
    RxNorm ucsfa50 = new RxNorm() {
      {
        getRxcuiIn().add("6470");
        setFrequency("200553");
        setRoute("15");
      }
    };
    meds.add(ucsfa50);
    RxNorm ucsfa51 = new RxNorm() {
      {
        getRxcuiIn().add("6470");
        setFrequency("200905");
        setRoute("11");
      }
    };
    meds.add(ucsfa51);
    RxNorm ucsfa52 = new RxNorm() {
      {
        getRxcuiIn().add("6960");
        setFrequency("200553");
        setRoute("11");
      }
    };
    meds.add(ucsfa52);
    RxNorm ucsfa53 = new RxNorm() {
      {
        getRxcuiIn().add("6960");
        setFrequency("200905");
        setRoute("11");
      }
    };
    meds.add(ucsfa53);
    RxNorm ucsfa54 = new RxNorm() {
      {
        getRxcuiIn().add("2598");
      }
    };
    meds.add(ucsfa54);
    RxNorm ucsfa55 = new RxNorm() {
      {
        getRxcuiIn().add("3322");
        setRoute("11");
      }
    };
    meds.add(ucsfa55);
    RxNorm ucsfa56 = new RxNorm() {
      {
        getRxcuiIn().add("3322");
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
        getRxcuiIn().add("596");
      }
    };
    meds.add(ucsfa58);
    RxNorm ucsfa59 = new RxNorm() {
      {
        getRxcuiIn().add("11289");
      }
    };
    meds.add(ucsfa59);
    RxNorm ucsfa60 = new RxNorm() {
      {
        getRxcuiIn().add("15202");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa60);
    RxNorm ucsfa61 = new RxNorm() {
      {
        getRxcuiIn().add("60819");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa61);
    RxNorm ucsfa62 = new RxNorm() {
      {
        getRxcuiIn().add("1546356");
      }
    };
    meds.add(ucsfa62);
    RxNorm ucsfa63 = new RxNorm() {
      {
        getRxcuiIn().add("1364430");
      }
    };
    meds.add(ucsfa63);
    RxNorm ucsfa64 = new RxNorm() {
      {
        getRxcuiIn().add("1114195");
      }
    };
    meds.add(ucsfa64);
    RxNorm ucsfa65 = new RxNorm() {
      {
        getRxcuiIn().add("1599538");
      }
    };
    meds.add(ucsfa65);
    RxNorm ucsfa66 = new RxNorm() {
      {
        getRxcuiIn().add("321208");
      }
    };
    meds.add(ucsfa66);
    RxNorm ucsfa67 = new RxNorm() {
      {
        getRxcuiIn().add("67108");
        setFrequency("200523");
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
        getRxcuiIn().add("235473");
        setRoute("18");
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
        getRxcuiIn().add("3628");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa71);
    RxNorm ucsfa72 = new RxNorm() {
      {
        getRxcuiIn().add("3992");
        setFrequency("200905");
        setRoute("11");
      }
    };
    meds.add(ucsfa72);
    RxNorm ucsfa73 = new RxNorm() {
      {
        getRxcuiIn().add("7512");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa73);
    RxNorm ucsfa74 = new RxNorm() {
      {
        getRxcuiIn().add("8163");
        setFrequency("200905");
        setRoute("11");
      }
    };
    meds.add(ucsfa74);
    RxNorm ucsfa75 = new RxNorm() {
      {
        getRxcuiIn().add("11149");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa75);
    RxNorm ucsfa76 = new RxNorm() {
      {
        getRxcuiIn().add("3616");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa76);
    RxNorm ucsfa77 = new RxNorm() {
      {
        getRxcuiIn().add("52769");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa77);
    RxNorm ucsfa78 = new RxNorm() {
      {
        getRxcuiIn().add("6054");
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
        getRxcuiIn().add("8814");
        setFrequency("200905");
      }
    };
    meds.add(ucsfa81);
    RxNorm ucsfa82 = new RxNorm() {
      {
        getRxcuiIn().add("343048");
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
        getRxcuiIn().add("32968");
      }
    };
    meds.add(ucsfa84);
    RxNorm ucsfa85 = new RxNorm() {
      {
        getRxcuiIn().add("613391");
      }
    };
    meds.add(ucsfa85);
    RxNorm ucsfa86 = new RxNorm() {
      {
        getRxcuiIn().add("1116632");
      }
    };
    meds.add(ucsfa86);
    RxNorm ucsfa87 = new RxNorm() {
      {
        getRxcuiIn().add("1191");
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
        getRxcuiIn().add("8640");
      }
    };
    meds.add(ucsfa89);
    RxNorm ucsfa90 = new RxNorm() {
      {
        getRxcuiIn().add("6902");
        setRoute("11");
      }
    };
    meds.add(ucsfa90);
    RxNorm ucsfa91 = new RxNorm() {
      {
        getRxcuiIn().add("3264");
        setRoute("11");
      }
    };
    meds.add(ucsfa91);
    RxNorm ucsfa92 = new RxNorm() {
      {
        getRxcuiIn().add("2878");
      }
    };
    meds.add(ucsfa92);
    RxNorm ucsfa93 = new RxNorm() {
      {
        getRxcuiIn().add("8638");
        setRoute("15");
      }
    };
    meds.add(ucsfa93);
    RxNorm ucsfa94 = new RxNorm() {
      {
        MedsSet result = new MedsSet();
        result.setName("Intermittent Cisatracurium IV");
        getMedsSets().add(result);
      }
    };
    meds.add(ucsfa94);
    RxNorm ucsfa95 = new RxNorm() {
      {
        MedsSet result = new MedsSet();
        result.setName("Continuous Infusion Vecuronium IV");
        getMedsSets().add(result);
      }
    };
    meds.add(ucsfa95);
    RxNorm ucsfa96 = new RxNorm() {
      {
        MedsSet result = new MedsSet();
        result.setName("Continuous Infusion Lorazepam IV");
        getMedsSets().add(result);
      }
    };
    meds.add(ucsfa96);

    // Insert objects into the working memory
    for (RxNorm med : meds) {
      session.insert(med);
    }

    // Since rules were not fired, the rxnorm has not been calculated yet
    assertTrue(ucsfa2.getMedsSets().isEmpty());

    log.info("Fire all rules!!");
    session.fireAllRules();

    assertEquals(ucsfa2.getMedsSets().get(0).getCode(), "UCSF_A2");
    assertEquals(ucsfa3.getMedsSets().get(0).getCode(), "UCSF_A3");
    assertEquals(ucsfa4.getMedsSets().get(0).getCode(), "UCSF_A4");
    assertEquals(ucsfa5.getMedsSets().get(0).getCode(), "UCSF_A5");
    assertEquals(ucsfa6.getMedsSets().get(0).getCode(), "UCSF_A6");
    assertEquals(ucsfa7.getMedsSets().get(0).getCode(), "UCSF_A7");
    assertEquals(ucsfa8.getMedsSets().get(0).getCode(), "UCSF_A8");
    assertEquals(ucsfa9.getMedsSets().get(0).getCode(), "UCSF_A9");
    assertEquals(ucsfa10.getMedsSets().get(0).getCode(), "UCSF_A10");
    assertEquals(ucsfa11.getMedsSets().get(0).getCode(), "UCSF_A11");
    assertEquals(ucsfa12.getMedsSets().get(0).getCode(), "UCSF_A12");
    assertEquals(ucsfa13.getMedsSets().get(0).getCode(), "UCSF_A13");
    assertEquals(ucsfa14.getMedsSets().get(0).getCode(), "UCSF_A14");
    assertEquals(ucsfa15.getMedsSets().get(0).getCode(), "UCSF_A15");
    assertEquals(ucsfa16.getMedsSets().get(0).getCode(), "UCSF_A16");
    assertEquals(ucsfa17.getMedsSets().get(0).getCode(), "UCSF_A17");
    assertEquals(ucsfa18.getMedsSets().get(0).getCode(), "UCSF_A18");
    assertEquals(ucsfa19.getMedsSets().get(0).getCode(), "UCSF_A19");
    assertEquals(ucsfa20.getMedsSets().get(0).getCode(), "UCSF_A20");
    assertEquals(ucsfa21.getMedsSets().get(0).getCode(), "UCSF_A21");
    assertEquals(ucsfa22.getMedsSets().get(0).getCode(), "UCSF_A22");
    assertEquals(ucsfa23.getMedsSets().get(0).getCode(), "UCSF_A23");
    assertEquals(ucsfa24.getMedsSets().get(0).getCode(), "UCSF_A24");
    assertEquals(ucsfa25.getMedsSets().get(0).getCode(), "UCSF_A25");
    assertEquals(ucsfa26.getMedsSets().get(0).getCode(), "UCSF_A26");
    assertEquals(ucsfa27.getMedsSets().get(0).getCode(), "UCSF_A27");
    assertEquals(ucsfa28.getMedsSets().get(0).getCode(), "UCSF_A28");
    assertEquals(ucsfa29.getMedsSets().get(0).getCode(), "UCSF_A29");
    assertEquals(ucsfa30.getMedsSets().get(0).getCode(), "UCSF_A30");
    assertEquals(ucsfa31.getMedsSets().get(0).getCode(), "UCSF_A31");
    assertEquals(ucsfa32.getMedsSets().get(0).getCode(), "UCSF_A32");
    assertEquals(ucsfa33.getMedsSets().get(0).getCode(), "UCSF_A33");
    assertEquals(ucsfa34.getMedsSets().get(0).getCode(), "UCSF_A34");
    assertEquals(ucsfa35.getMedsSets().get(0).getCode(), "UCSF_A35");
    assertEquals(ucsfa36.getMedsSets().get(0).getCode(), "UCSF_A36");
    assertEquals(ucsfa37.getMedsSets().get(0).getCode(), "UCSF_A37");
    assertEquals(ucsfa38.getMedsSets().get(0).getCode(), "UCSF_A38");
    assertEquals(ucsfa39.getMedsSets().get(0).getCode(), "UCSF_A39");
    assertEquals(ucsfa40.getMedsSets().get(0).getCode(), "UCSF_A40");
    assertEquals(ucsfa41.getMedsSets().get(0).getCode(), "UCSF_A41");
    assertEquals(ucsfa42.getMedsSets().get(0).getCode(), "UCSF_A42");
    assertEquals(ucsfa43.getMedsSets().get(0).getCode(), "UCSF_A43");
    assertEquals(ucsfa44.getMedsSets().get(0).getCode(), "UCSF_A44");
    assertEquals(ucsfa45.getMedsSets().get(0).getCode(), "UCSF_A45");
    assertEquals(ucsfa46.getMedsSets().get(0).getCode(), "UCSF_A46");
    assertEquals(ucsfa47.getMedsSets().get(0).getCode(), "UCSF_A47");
    assertEquals(ucsfa48.getMedsSets().get(0).getCode(), "UCSF_A48");
    assertEquals(ucsfa49.getMedsSets().get(0).getCode(), "UCSF_A49");
    assertEquals(ucsfa50.getMedsSets().get(0).getCode(), "UCSF_A50");
    assertEquals(ucsfa51.getMedsSets().get(0).getCode(), "UCSF_A51");
    assertEquals(ucsfa52.getMedsSets().get(0).getCode(), "UCSF_A52");
    assertEquals(ucsfa53.getMedsSets().get(0).getCode(), "UCSF_A53");
    assertEquals(ucsfa54.getMedsSets().get(0).getCode(), "UCSF_A54");
    assertEquals(ucsfa55.getMedsSets().get(0).getCode(), "UCSF_A55");
    assertEquals(ucsfa56.getMedsSets().get(0).getCode(), "UCSF_A56");
    assertEquals(ucsfa57.getMedsSets().get(0).getCode(), "UCSF_A57");
    assertEquals(ucsfa58.getMedsSets().get(0).getCode(), "UCSF_A58");
    assertEquals(ucsfa59.getMedsSets().get(0).getCode(), "UCSF_A59");
    assertEquals(ucsfa60.getMedsSets().get(0).getCode(), "UCSF_A60");
    assertEquals(ucsfa61.getMedsSets().get(0).getCode(), "UCSF_A61");
    assertEquals(ucsfa62.getMedsSets().get(0).getCode(), "UCSF_A62");
    assertEquals(ucsfa63.getMedsSets().get(0).getCode(), "UCSF_A63");
    assertEquals(ucsfa64.getMedsSets().get(0).getCode(), "UCSF_A64");
    assertEquals(ucsfa65.getMedsSets().get(0).getCode(), "UCSF_A65");
    assertEquals(ucsfa66.getMedsSets().get(0).getCode(), "UCSF_A66");
    assertEquals(ucsfa67.getMedsSets().get(0).getCode(), "UCSF_A67");
    assertEquals(ucsfa68.getMedsSets().get(0).getCode(), "UCSF_A68");
    assertEquals(ucsfa69.getMedsSets().get(0).getCode(), "UCSF_A69");
    assertEquals(ucsfa70.getMedsSets().get(0).getCode(), "UCSF_A70");
    assertEquals(ucsfa71.getMedsSets().get(0).getCode(), "UCSF_A71");
    assertEquals(ucsfa72.getMedsSets().get(0).getCode(), "UCSF_A72");
    assertEquals(ucsfa73.getMedsSets().get(0).getCode(), "UCSF_A73");
    assertEquals(ucsfa74.getMedsSets().get(0).getCode(), "UCSF_A74");
    assertEquals(ucsfa75.getMedsSets().get(0).getCode(), "UCSF_A75");
    assertEquals(ucsfa76.getMedsSets().get(0).getCode(), "UCSF_A76");
    assertEquals(ucsfa77.getMedsSets().get(0).getCode(), "UCSF_A77");
    assertEquals(ucsfa78.getMedsSets().get(0).getCode(), "UCSF_A78");
    assertEquals(ucsfa79.getMedsSets().get(0).getCode(), "UCSF_A79");
    assertEquals(ucsfa80.getMedsSets().get(0).getCode(), "UCSF_A80");
    assertEquals(ucsfa81.getMedsSets().get(0).getCode(), "UCSF_A81");
    assertEquals(ucsfa82.getMedsSets().get(0).getCode(), "UCSF_A82");
    assertEquals(ucsfa83.getMedsSets().get(0).getCode(), "UCSF_A83");
    assertEquals(ucsfa84.getMedsSets().get(0).getCode(), "UCSF_A84");
    assertEquals(ucsfa85.getMedsSets().get(0).getCode(), "UCSF_A85");
    assertEquals(ucsfa86.getMedsSets().get(0).getCode(), "UCSF_A86");
    assertEquals(ucsfa87.getMedsSets().get(0).getCode(), "UCSF_A87");
    assertEquals(ucsfa88.getMedsSets().get(0).getCode(), "UCSF_A88");
    assertEquals(ucsfa89.getMedsSets().get(0).getCode(), "UCSF_A89");
    assertEquals(ucsfa90.getMedsSets().get(0).getCode(), "UCSF_A90");
    assertEquals(ucsfa91.getMedsSets().get(0).getCode(), "UCSF_A91");
    assertEquals(ucsfa92.getMedsSets().get(0).getCode(), "UCSF_A92");
    assertEquals(ucsfa93.getMedsSets().get(0).getCode(), "UCSF_A93");
    assertEquals(ucsfa94.getMedsSets().get(1).getCode(), "UCSF_A94");

    // A2 should also have tripped A94
    assertEquals(ucsfa2.getMedsSets().get(1).getCode(), "UCSF_A94",
        ucsfa2.getMedsSets().toString());
    assertEquals(ucsfa95.getMedsSets().get(1).getCode(), "UCSF_A95");
    assertEquals(ucsfa96.getMedsSets().get(1).getCode(), "UCSF_A96");

    session.dispose();
    log.info("===> End of test <===\n");
  }
}
