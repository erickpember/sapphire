// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.configuration.guice;

import com.datafascia.common.configuration.ConfigurationProvider;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * Guice module which enables injection of configuration values with
 * {@link com.datafascia.common.configuration.Configure} annotation.
 */
public class ConfigureModule extends AbstractModule {

  @Override
  protected final void configure() {
    ConfigurationNodeTypeListener listener =
        new ConfigurationNodeTypeListener(new ConfigurationProvider().get());
    bindListener(Matchers.any(), listener);

    onConfigure();
  }

  /**
   * Subclasses may override this method to customize this module. The default
   * implementation does nothing.
   */
  protected void onConfigure() {
  }
}
