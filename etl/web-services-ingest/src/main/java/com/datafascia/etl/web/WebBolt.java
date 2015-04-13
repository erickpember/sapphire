// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.web;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * A bolt that receives web tuples.
 */
@Slf4j
public class WebBolt extends BaseRichBolt {
  @Override
  public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
  }

  @Override
  public void execute(Tuple input) {
    for (Object obj : input.getValues()) {
      log.info("Received tuple: " + obj.toString());
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
  }
}
