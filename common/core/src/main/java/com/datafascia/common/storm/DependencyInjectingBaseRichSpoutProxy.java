// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.storm;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.base.BaseRichSpout;
import com.datafascia.common.inject.Injectors;
import java.util.Map;

/**
 * Injects dependencies into a spout. The spout class must define fields annotated
 * with {@code @Inject} which will be set with the dependencies.
 */
public class DependencyInjectingBaseRichSpoutProxy extends BaseRichSpoutProxy {
  private static final long serialVersionUID = 1L;

  /**
   * Wraps spout to inject dependencies into it.
   *
   * @param delegate
   *     spout to wrap
   */
  public DependencyInjectingBaseRichSpoutProxy(BaseRichSpout delegate) {
    super(delegate);
  }

  @Override
  public void open(Map config, TopologyContext context, SpoutOutputCollector collector) {
    Injectors.getInjector().injectMembers(delegate);
    super.open(config, context, collector);
  }
}
