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

import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.configuration.AbstractHierarchicalFileConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.yaml.snakeyaml.Yaml;

/**
 * Hierarchical configuration from YAML file.
 */
public class YamlConfiguration extends AbstractHierarchicalFileConfiguration {
  private static final long serialVersionUID = 1L;

  private final Yaml yaml = new Yaml();

  /**
   * Constructor
   */
  public YamlConfiguration() {
    super();
  }

  @Override
  public void load(Reader reader) throws ConfigurationException {
    setNodeValue(getRootNode(), yaml.load(reader));
  }

  @SuppressWarnings("unchecked")
  protected void setNodeValue(ConfigurationNode targetNode, Object value) {
    if (value instanceof Map<?, ?>) {
      for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
        Node childNode = new Node(entry.getKey());
        setNodeValue(childNode, entry.getValue());
        targetNode.addChild(childNode);
      }
    } else if (value instanceof Collection) {
      for (Object child : (Iterable<?>) value) {
        Node childNode = new Node("item");
        setNodeValue(childNode, child);
        targetNode.addChild(childNode);
      }
    }

    targetNode.setValue(value);
  }

  @Override
  public void save(Writer writer) throws ConfigurationException {
    throw new UnsupportedOperationException("save not implemented");
  }
}
