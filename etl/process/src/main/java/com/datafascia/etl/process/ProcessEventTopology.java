// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.process;

import backtype.storm.Config;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.datafascia.common.storm.trident.BaseTridentTopology;
import com.datafascia.common.storm.trident.StreamFactory;
import com.datafascia.domain.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.HierarchicalConfiguration;
import storm.kafka.BrokerHosts;
import storm.kafka.ZkHosts;
import storm.kafka.trident.OpaqueTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import storm.trident.Stream;
import storm.trident.TridentTopology;

/**
 * Processes event to update state of system.
 */
@Slf4j
public class ProcessEventTopology extends BaseTridentTopology {

  private static final String SPOUT_ID_PREFIX =
      ProcessEventTopology.class.getSimpleName() + '-';
  private static final String EVENT_SPOUT_ID = "eventSpout";
  private static final String EVENT_QUEUE_CONFIGURATION_NODE = "eventQueue";

  private StreamFactory eventStreamFactory;

  public void setEventStreamFactory(StreamFactory eventStreamFactory) {
    this.eventStreamFactory = eventStreamFactory;
  }

  private OpaqueTridentKafkaSpout createSpout(String configurationNode) {
    String zooKeepers = getZooKeepers();

    HierarchicalConfiguration queueConfiguration =
        rootConfiguration.configurationAt(configurationNode);
    String topic = queueConfiguration.getString("topic");

    log.info("Creating Kafka spout, zooKeepers [{}], topic [{}]", zooKeepers, topic);

    BrokerHosts brokerHosts = new ZkHosts(zooKeepers);
    TridentKafkaConfig spoutConfig = new TridentKafkaConfig(brokerHosts, topic);
    return new OpaqueTridentKafkaSpout(spoutConfig);
  }

  private Stream createInputEventStream(TridentTopology topology) {
    if (eventStreamFactory != null) {
      return eventStreamFactory.newStream(topology);
    }

    return topology.newStream(
        SPOUT_ID_PREFIX + EVENT_SPOUT_ID, createSpout(EVENT_QUEUE_CONFIGURATION_NODE));
  }

  private Stream createEventStream(TridentTopology topology) {
    return createInputEventStream(topology)
        .parallelismHint(getParallelism(EVENT_SPOUT_ID))

        // Deserialize event.
        .shuffle()
        .name(DeserializeEvent.ID)
        .each(
            new Fields(F.BYTES),
            wrap(new DeserializeEvent()),
            DeserializeEvent.OUTPUT_FIELDS)
        .project(DeserializeEvent.OUTPUT_FIELDS)
        .parallelismHint(getParallelism(DeserializeEvent.ID));
  }

  private void processEvent(Stream messageStream) {
    messageStream
        // Process event.
        .name(ProcessEvent.ID)
        .each(
            new Fields(F.EVENT),
            wrap(new ProcessEvent()))
        .parallelismHint(getParallelism(ProcessEvent.ID));
  }

  @Override
  protected Config configureTopology() {
    stormConfig.registerSerialization(Event.class);
    return stormConfig;
  }

  @Override
  protected StormTopology buildTopology() {
    TridentTopology topology = new TridentTopology();

    Stream eventStream = createEventStream(topology);
    processEvent(eventStream);

    return topology.build();
  }

  /**
   * Runs application
   *
   * @param args
   *     command line arguments
   */
  public static void main(String[] args) {
    ProcessEventTopology application = new ProcessEventTopology();
    application.submitTopology(args);
  }
}
