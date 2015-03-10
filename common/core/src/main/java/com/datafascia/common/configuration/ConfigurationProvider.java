// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.configuration;

import javax.inject.Provider;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * Gets configuration instance.
 */
public class ConfigurationProvider implements Provider<HierarchicalConfiguration> {

  private static final String CONFIGURATION_DEFINITION_FILE = "configuration.xml";
  private static final String YAML_TAG = "yaml";

  @Override
  public HierarchicalConfiguration get() {
    try {
      DefaultConfigurationBuilder builder =
          new DefaultConfigurationBuilder(CONFIGURATION_DEFINITION_FILE);
      builder.addConfigurationProvider(YAML_TAG, new YamlConfigurationProvider());
      return builder.getConfiguration(true);
    } catch (ConfigurationException e) {
      throw new IllegalStateException(
          "Cannot build configuration from " + CONFIGURATION_DEFINITION_FILE, e);
    }
  }
}
