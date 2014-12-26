// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources.socket;

import javax.ws.rs.Path;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Post;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.util.IOUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j @Path("/") @ManagedService(interceptors = AtmosphereResourceLifecycleInterceptor.class)
public final class SocketResource {
  /**
   * Invoked when the connection as been fully established and suspended, e.g ready for receiving
   * messages.
   * 
   * @param resource the atmosphere resource
   */
  @Ready
  public String onReady(final AtmosphereResource resource) {
    return "Connect " + resource.uuid();
  }

  /**
   * Invoked when the client disconnect or when an unexpected closing of the underlying connection
   * happens.
   * 
   * @param event the event
   */
  @Disconnect
  public void onDisconnect(final AtmosphereResourceEvent event) {
    log.info("Resource {} disconnected ", event.getResource().uuid());
  }

  /**
   * 
   * Invoked when the client sends a message to websocket
   * 
   * @param resource
   */
  @Post
  public void onMessage(final AtmosphereResource resource) {
    String message = IOUtils.readEntirely(resource).toString();
    log.info(message);
  }
}
