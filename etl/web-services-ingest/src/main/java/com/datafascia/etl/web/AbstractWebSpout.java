// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.web;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * An abstract spout for querying web services.
 *
 * When extended, it provides a mechanism for doing timed polls of a web service.
 */
@Slf4j
public abstract class AbstractWebSpout extends BaseRichSpout {
  private SpoutOutputCollector collector;
  private Instant nextPoll;

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields(getFields()));
  }

  @Override
  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    this.collector = collector;
  }

  /**
   * @return The next instant the poll should run.
   */
  abstract protected Instant getNextPoll(boolean isFirstRun);

  /**
   * @return A list of web call responses to pass to the topology.
   */
  abstract protected List<String> getResponses();

  /**
   * @return A list of fields to spout.
   */
  abstract List<String> getFields();

  @Override
  public void nextTuple() {
    if (nextPoll == null) {
      nextPoll = getNextPoll(true);
    }

    if (nextPoll.isBefore(Instant.now())) {
      nextPoll = getNextPoll(false);

      List<String> responses = getResponses();
      if (responses != null && responses.size() > 0) {
        collector.emit(new Values(responses));
      }
    } else {
      /*
       * Waiting a millisecond when called with no tuples to emit, as per recommendation in ISpout
       * documentation.
       */
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        log.error("Error waiting in nextTuple.", e);
      }
    }
  }
}
