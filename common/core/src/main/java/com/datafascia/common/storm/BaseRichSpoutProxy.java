// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.storm;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import java.util.Map;

/**
 * Wraps a spout to implement additional behavior around spout methods.
 */
public abstract class BaseRichSpoutProxy extends BaseRichSpout {

  protected BaseRichSpout delegate;

  protected BaseRichSpoutProxy(BaseRichSpout delegate) {
    this.delegate = delegate;
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    delegate.declareOutputFields(declarer);
  }

  @Override
  public void open(Map config, TopologyContext context, SpoutOutputCollector collector) {
    delegate.open(config, context, collector);
  }

  @Override
  public void nextTuple() {
    delegate.nextTuple();
  }

  @Override
  public void close() {
    delegate.close();
  }

  @Override
  public void activate() {
    delegate.activate();
  }

  @Override
  public void deactivate() {
    delegate.deactivate();
  }

  @Override
  public void ack(Object messageId) {
    delegate.ack(messageId);
  }

  @Override
  public void fail(Object messageId) {
    delegate.fail(messageId);
  }
}
