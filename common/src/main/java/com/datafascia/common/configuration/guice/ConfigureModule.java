// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.configuration.guice;

import com.datafascia.common.configuration.ConfigurationProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import javax.inject.Singleton;
import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * Guice module which enables injection of configuration values with
 * {@link com.datafascia.common.configuration.Configure} annotation.
 */
public abstract class ConfigureModule extends AbstractModule {

  @Override
  protected final void configure() {
    ConfigurationNodeTypeListener listener = new ConfigurationNodeTypeListener();
    requestInjection(listener);
    bindListener(Matchers.any(), listener);

    onConfigure();
  }

  /**
   * Subclasses may override this method to customize this module.  The default
   * implementation does nothing.
   */
  protected void onConfigure() {
  }

  @Provides @Singleton
  public HierarchicalConfiguration getConfiguration() {
    return new ConfigurationProvider().get();
  }
}
