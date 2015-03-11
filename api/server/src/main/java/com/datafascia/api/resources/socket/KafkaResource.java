// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.socket;

import com.datafascia.common.kafka.KafkaConfig;
import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import lombok.extern.slf4j.Slf4j;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.PathParam;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;

import static com.datafascia.common.kafka.KafkaConfig.TIMEOUT;
import static javax.servlet.http.HttpServletResponse.SC_REQUEST_TIMEOUT;
import static org.apache.commons.httpclient.HttpStatus.SC_CONTINUE;

/**
 * A linkage of Kafka resource to WebSocket.
 *
 * WebSocket is handled through Atmosphere and configured by AtmosphereBundle.
 * This resource is accessible by URL and spins up a new instance for each client connection.
 *
 * Example URL ws://127.0.0.1:9090/websocket/kafka/TOPICNAME?timeout=-1
 *
 */
@Slf4j @ManagedService(path = "/websocket/kafka/{topic}",
        interceptors = AtmosphereResourceLifecycleInterceptor.class)
public final class KafkaResource {
  // Optional parameter pulled from URL via the Atmosphere resource. Ex: "?timeout=-1"
  private static final String TIMEOUTPARAM = "timeout";

  private final KafkaConfig config;

  // This is a mandatory URL parameter populated from the path set in the ManagedService annotation.
  @PathParam("topic")
  private String topic;

  // Kafka consumer connection.
  private ConsumerConnector connector;

  /**
   * Construct the resource Injection done by way of Guice and Atmosphere combined.
   *
   * @param config Resource populated from yml via Dropwizard, injected by Guice and Atmosphere.
   * This contains configuration presets needed by any Kafka consumer.
   */
  @Inject
  public KafkaResource(KafkaConfig config) {
    this.config = config;
  }

  /**
   * Invoked when the connection as been fully established and suspended, e.g ready for receiving
   * messages.
   *
   * @param resource the atmosphere resource
   */
  @Ready
  public void onOpen(AtmosphereResource resource) {
    log.info("Resource {} connected. Topic:{}", resource.uuid(), topic);

    // Pull properties from Dropwizard config, add client-specified properties to that.
    Properties props = appendRestParametersToProperties(config.buildConsumerConfig(), resource);

    // Invoke a Kafka consumer and connect to the topic.
    ConsumerConfig kafkaConfig = new ConsumerConfig(props);
    connector = Consumer.createJavaConsumerConnector(kafkaConfig);
    Map<String, Integer> streamCounts = Collections.singletonMap(topic, 1);
    Map<String, List<KafkaStream<byte[], byte[]>>> streams
            = connector.createMessageStreams(streamCounts);
    KafkaStream<byte[], byte[]> stream = streams.get(topic).get(0);

    // Listen for messages, relay to WebSocket.
    relayMessages(resource, stream);
  }

  /**
   * Handle either deliberate and accidental disconnection, depending on which happened.
   *
   * @param event the Atmosphere event
   */
  @Disconnect
  public void onDisconnect(final AtmosphereResourceEvent event) {
    closeConsumerConnection();

    if (event.isCancelled()) {
      log.info("Resource {} disconnected from client unexpectedly.", event.getResource().uuid());
    } else if (event.isClosedByClient()) {
      log.info("Client closed connection to Resource {}", event.getResource().uuid());
    } else {
      log.info("Resource {} disconnected under unknown circumstances.", event.getResource().uuid());
    }
  }

  /**
   * Shut down the Kafka consumer when disconnecting.
   */
  private void closeConsumerConnection() {
    log.info("Shutting down Kafka consumer.");
    if (connector != null) {
      connector.shutdown();
    }
  }

  /**
   * Pull optional properties from url parameters, overwrite dropwizard config properties when found
   *
   * @param defaultProperties pre-populated properties with default values from dropwizard config
   * @param resource Atmosphere's handle to the session that enables us to pull parameters from the
   * get request URL
   * @return Properties with any additional client-specified parameters added or overwritten
   */
  private Properties appendRestParametersToProperties(Properties defaultProperties,
          AtmosphereResource resource) {
    if (!Strings.isNullOrEmpty(resource.getRequest().getParameter(TIMEOUTPARAM))) {
      defaultProperties.put(TIMEOUT, resource.getRequest().getParameter(TIMEOUTPARAM));
    }

    return defaultProperties;
  }

  /**
   * Loop on the Kafka stream connection, waiting for messages. All messages are sent back to client
   * over WebSocket.
   *
   * If TIMEOUT property &gt; 0, a ConsumerTimeoutException will be thrown after waiting that many
   * milliseconds without a new message.
   *
   * @param resource Atmosphere's handle to the session, used here to talk to the client.
   * @param connector Kafka's consumer connection, used here to close after session ends.
   * @param stream Connection to stream of Kafka messages, read here.
   */
  private void relayMessages(AtmosphereResource resource, KafkaStream<byte[], byte[]> stream) {
    try {
      for (MessageAndMetadata<byte[], byte[]> messageAndMetadata : stream) {
        if (resource.isCancelled()) {
          log.info("Client connection cancelled, stopping Kafka subscription");
          break;
        }
        byte[] message = messageAndMetadata.message();
        log.debug("message:" + Arrays.toString(message));
        resource.getResponse().write(message).setStatus(SC_CONTINUE);
      }
    } catch (ConsumerTimeoutException e) {
      resource.getResponse().setStatus(SC_REQUEST_TIMEOUT);
      log.info("Kafka consumer timed out. Set timeout to -1 to prevent this.", e);
    } finally {
      closeConsumerConnection();
    }
  }
}
