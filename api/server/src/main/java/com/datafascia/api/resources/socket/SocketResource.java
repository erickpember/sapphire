// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.api.resources.socket;

import com.datafascia.common.kafka.KafkaConfig;
import java.io.IOException;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Post;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.util.IOUtils;

/**
 * Example resource to show integration of Atmosphere within Dropwizard.
 */
@Slf4j
@ManagedService(path = "/websocket/test",
    interceptors = AtmosphereResourceLifecycleInterceptor.class)
public final class SocketResource {
  private final KafkaConfig config;

  /**
   * Construct the resource
   *
   * @param config the Kafka configuration
   */
  @Inject
  public SocketResource(KafkaConfig config) {
    this.config = config;
  }

  /**
   * Invoked when the connection as been fully established and suspended, e.g ready for receiving
   * messages.
   *
   * @param resource the atmosphere resource
   *
   * @return the connection identifier
   */
  @Ready
  public String onReady(final AtmosphereResource resource) {
    return "Connect " + resource.uuid();
  }

  /**
   * Invoked when the client disconnect or when an unexpected closing of the underlying connection
   * happens.
   *
   * @param event the Atmosphere event
   */
  @Disconnect
  public void onDisconnect(final AtmosphereResourceEvent event) {
    log.info("Resource {} disconnected ", event.getResource().uuid());
  }

  /**
   *
   * Invoked when the client sends a message to websocket
   *
   * @param resource the resource information
   */
  @Post
  public void onMessage(final AtmosphereResource resource) {
    try {
      String message = IOUtils.readEntirely(resource).toString();
      log.info("Test message: " + message + ", " + config.getGroupId());
    } catch (IOException e) {
      log.error("Error reading websocket message", e);
    }
  }
}
