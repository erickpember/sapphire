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
