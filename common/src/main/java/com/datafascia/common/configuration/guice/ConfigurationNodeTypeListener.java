// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.configuration.guice;

import com.datafascia.common.configuration.ConfigurationNode;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import javax.inject.Inject;
import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * Receives notification of type injected by Guice.
 */
public class ConfigurationNodeTypeListener implements TypeListener {

  @Inject
  private HierarchicalConfiguration rootConfiguration;

  @Override
  public <T> void hear(TypeLiteral<T> typeLiteral, TypeEncounter<T> typeEncounter) {
    if (typeLiteral.getRawType().isAnnotationPresent(ConfigurationNode.class)) {
      typeEncounter.register(new ConfigureMembersInjector<T>(rootConfiguration));
    }
  }
}
