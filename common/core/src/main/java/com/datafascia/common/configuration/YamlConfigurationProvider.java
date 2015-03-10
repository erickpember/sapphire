// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.configuration;

import org.apache.commons.configuration.DefaultConfigurationBuilder;

/**
 * Loads configuration from YAML file.
 */
public class YamlConfigurationProvider
    extends DefaultConfigurationBuilder.FileConfigurationProvider {

  /**
   * Constructor
   */
  public YamlConfigurationProvider() {
    super(YamlConfiguration.class);
  }
}
