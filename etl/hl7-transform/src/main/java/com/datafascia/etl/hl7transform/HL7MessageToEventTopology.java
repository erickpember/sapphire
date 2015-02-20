// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import backtype.storm.Config;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.datafascia.common.storm.trident.BaseTridentTopology;
import com.datafascia.common.storm.trident.StreamFactory;
import com.datafascia.message.RawMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.HierarchicalConfiguration;
import storm.kafka.BrokerHosts;
import storm.kafka.ZkHosts;
import storm.kafka.trident.OpaqueTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import storm.trident.Stream;
import storm.trident.TridentTopology;

/**
 * Transforms HL7 message to event.
 */
@Slf4j
public class HL7MessageToEventTopology extends BaseTridentTopology {

  private static final String SPOUT_ID_PREFIX =
      HL7MessageToEventTopology.class.getSimpleName() + '-';
  private static final String HL7_MESSAGE_SPOUT_ID = "hl7MessageSpout";
  private static final String HL7_MESSAGE_QUEUE_CONFIGURATION_NODE = "hl7MessageQueue";

  private StreamFactory hl7MessageStreamFactory;

  public void setHL7MessageStreamFactory(StreamFactory hl7MessageStreamFactory) {
    this.hl7MessageStreamFactory = hl7MessageStreamFactory;
  }

  /**
   * Runs application
   *
   * @param args
   *     command line arguments
   */
  public static void main(String[] args) {
    HL7MessageToEventTopology application = new HL7MessageToEventTopology();
    application.submitTopology(args);
  }

  @Override
  protected Config configureTopology() {
    stormConfig.registerSerialization(RawMessage.class);
    return stormConfig;
  }

  @Override
  protected StormTopology buildTopology() {
    TridentTopology topology = new TridentTopology();

    Stream messageStream =
        // Read serialized HL7 message from queue.
        createHL7MessageStream(topology)
        .parallelismHint(getParallelism(HL7_MESSAGE_SPOUT_ID))

        // Deserialize HL7 message.
        .shuffle()
        .name(DeserializeMessage.ID)
        .each(
            new Fields(F.BYTES),
            wrap(new DeserializeMessage()),
            DeserializeMessage.OUTPUT_FIELDS)
        .project(DeserializeMessage.OUTPUT_FIELDS)
        .parallelismHint(getParallelism(DeserializeMessage.ID))

        // Save HL7 message to archive.
        .name(SaveMessage.ID)
        .each(
            new Fields(F.RAW_MESSAGE),
            wrap(new SaveMessage()))
        .parallelismHint(getParallelism(SaveMessage.ID))

        // Parse HL7 message
        .name(ParseMessage.ID)
        .each(
            new Fields(F.RAW_MESSAGE),
            wrap(new ParseMessage()),
            ParseMessage.OUTPUT_FIELDS)
        .project(ParseMessage.OUTPUT_FIELDS)
        .parallelismHint(getParallelism(ParseMessage.ID));

    messageStream
        .name(HandleAdmitMessage.ID)
        .each(
            new Fields(F.MESSAGE),
            wrap(new HandleAdmitMessage()))
        .parallelismHint(getParallelism(HandleAdmitMessage.ID));

    messageStream
        .name(HandleObservationMessage.ID)
        .each(
            new Fields(F.MESSAGE),
            wrap(new HandleObservationMessage()))
        .parallelismHint(getParallelism(HandleObservationMessage.ID));

    return topology.build();
  }

  private Stream createHL7MessageStream(TridentTopology topology) {
    if (hl7MessageStreamFactory != null) {
      return hl7MessageStreamFactory.newStream(topology);
    }

    return topology.newStream(
        SPOUT_ID_PREFIX + HL7_MESSAGE_SPOUT_ID, createSpout(HL7_MESSAGE_QUEUE_CONFIGURATION_NODE));
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
}
