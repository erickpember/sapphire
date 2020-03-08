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
package com.datafascia.common.configuration.guice;

import com.datafascia.common.configuration.ConfigurationNode;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import lombok.AllArgsConstructor;
import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * Receives notification of type injected by Guice.
 */
@AllArgsConstructor
public class ConfigurationNodeTypeListener implements TypeListener {

  private final HierarchicalConfiguration rootConfiguration;

  @Override
  public <T> void hear(TypeLiteral<T> typeLiteral, TypeEncounter<T> typeEncounter) {
    if (typeLiteral.getRawType().isAnnotationPresent(ConfigurationNode.class)) {
      typeEncounter.register(new ConfigureMembersInjector<T>(rootConfiguration));
    }
  }
}
