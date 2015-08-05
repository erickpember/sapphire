// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web.rules.util;

import java.util.ArrayList;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;

import static com.datafascia.etl.ucsf.web.rules.util.TestUtil.findDuplicates;

/**
 * This serves as a way of logging, tracking, counting rule activations in test code.
 */
@Slf4j
public class RuleTrackingEventListener extends DefaultAgendaEventListener {

  private final ArrayList<String> firedRules = new ArrayList<>();

  @Override
  public void afterMatchFired(AfterMatchFiredEvent event) {
    final Rule rule = event.getMatch().getRule();
    firedRules.add(rule.getName());
    log.debug("rule fired: " + rule.getName());
  }

  /**
   * Get the count of rules that fired.
   *
   * @return Rule count.
   */
  public int getCount() {
    return firedRules.size();
  }

  /**
   * Get the list of rules that fired.
   *
   * @return Fired rules list.
   */
  public ArrayList<String> getFiredRules() {
    return firedRules;
  }

  /**
   * Returns a set of duplicates found in rule firings.
   *
   * @return A set of duplicate rule firings, as string titles.
   */
  public Set<String> findDuplicateFirings() {
    return findDuplicates(firedRules);
  }
}
