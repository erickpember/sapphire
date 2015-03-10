// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.storm.trident;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.utils.Utils;
import com.datafascia.common.configuration.ConfigurationProvider;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.HierarchicalConfiguration;
import storm.trident.operation.Filter;
import storm.trident.operation.Function;
import storm.trident.spout.RichSpoutBatchExecutor;
import storm.trident.state.QueryFunction;
import storm.trident.state.State;

/**
 * Implements common methods for Trident topology builder
 */
@Slf4j
public abstract class BaseTridentTopology {

  /** topology name argument to indicate topology should be submitted to local Storm cluster */
  public static final String LOCAL_TOPOLOGY = "local";

  private static final String PARALLELISM_KEY = "parallelism";

  // configuration properties to submit with topology to Storm
  protected Config stormConfig;
  // root configuration node
  protected HierarchicalConfiguration rootConfiguration;
  // configuration for this topology
  protected HierarchicalConfiguration topologyConfiguration;
  // parallelism configuration for this topology
  protected HierarchicalConfiguration parallelismConfiguration;
  protected boolean local;

  protected BaseTridentTopology() {
    rootConfiguration = new ConfigurationProvider().get();
    topologyConfiguration = rootConfiguration.configurationAt(getClass().getSimpleName());
    parallelismConfiguration = topologyConfiguration.configurationAt(PARALLELISM_KEY);

    stormConfig = new Config();
    stormConfig.setNumWorkers(topologyConfiguration.getInt("workers", 1));
    stormConfig.setDebug(topologyConfiguration.getBoolean("debug", false));
    stormConfig.setMessageTimeoutSecs(topologyConfiguration.getInt("messageTimeoutSecs", 30));
    stormConfig.setMaxSpoutPending(topologyConfiguration.getInt("maxSpoutPending", 1));
    stormConfig.put(
        Config.TOPOLOGY_SLEEP_SPOUT_WAIT_STRATEGY_TIME_MS,
        topologyConfiguration.getInt("sleepSpoutWaitStrategyTimeMs", 1));
    stormConfig.put(
        RichSpoutBatchExecutor.MAX_BATCH_SIZE_CONF,
        topologyConfiguration.getInt("spoutMaxBatchSize", 1));
    stormConfig.put(
        Config.TOPOLOGY_TRIDENT_BATCH_EMIT_INTERVAL_MILLIS,
        topologyConfiguration.getInt("tridentBatchEmitIntervalMillis", 1));
  }

  /**
   * Gets ZooKeeper servers and port from {@code storm.yaml} and {@code default.yaml} files.
   *
   * @return comma separated list of host:port
   */
  protected String getZooKeepers() {
    Map<String, Object> config = Utils.readStormConfig();
    List<String> zooKeeperServers = (List<String>) config.get(Config.STORM_ZOOKEEPER_SERVERS);
    int zooKeeperPort = ((Number) config.get(Config.STORM_ZOOKEEPER_PORT)).intValue();

    StringBuilder zooKeepers = new StringBuilder();
    for (String zooKeeperServer : zooKeeperServers) {
      if (zooKeepers.length() > 0) {
        zooKeepers.append(',');
      }

      zooKeepers.append(zooKeeperServer).append(':').append(zooKeeperPort);
    }

    return zooKeepers.toString();
  }

  /**
   * Gets parallelism hint for the component. Reads property having the same name as the component
   * identifier from the {@code parallelism} subnode under the topology configuration. If property
   * is not found, then returns default value 1.
   *
   * @param componentId
   *     component identifier
   * @return number of threads
   */
  protected int getParallelism(String componentId) {
    Integer value = parallelismConfiguration.getInteger(componentId, null);
    if (value == null) {
      value = 1;
      log.warn("No parallelism configured for {}, using default {}", componentId, value);
    }

    return value;
  }

  protected boolean isLocal() {
    return local;
  }

  /**
   * Creates proxy which injects dependencies into an operation.
   *
   * @param delegate
   *     operation to inject dependencies into
   * @return operation
   */
  protected static Filter wrap(Filter delegate) {
    return LoggingProxy.create(DependencyInjectingProxy.create(delegate));
  }

  /**
   * Creates proxy which injects dependencies into an operation.
   *
   * @param delegate
   *     operation to inject dependencies into
   * @return operation
   */
  protected static Function wrap(Function delegate) {
    return LoggingProxy.create(DependencyInjectingProxy.create(delegate));
  }

  /**
   * Creates proxy which injects dependencies into an operation.
   *
   * @param delegate
   *     operation to inject dependencies into
   * @return operation
   */
  protected static <S extends State, T> QueryFunction<S, T> wrap(QueryFunction<S, T> delegate) {
    return LoggingProxy.create(DependencyInjectingProxy.create(delegate));
  }

  protected abstract Config configureTopology();
  protected abstract StormTopology buildTopology();

  /**
   * Submits topology to run
   *
   * @param args
   *     command line arguments
   */
  public void submitTopology(String[] args) {
    if (args.length < 1) {
      log.error("usage: storm jar {} topology_name", getClass().getName());
      return;
    }
    String topologyName = args[0];

    if (LOCAL_TOPOLOGY.equals(topologyName)) {
      local = true;
      LocalCluster cluster = new LocalCluster();
      cluster.submitTopology(topologyName, configureTopology(), buildTopology());
    } else {
      try {
        StormSubmitter.submitTopology(topologyName, configureTopology(), buildTopology());
      } catch (AlreadyAliveException e) {
        log.error("Topology is already running", e);
      } catch (InvalidTopologyException e) {
        log.error("Topology is invalid", e);
      }
    }
  }
}
