// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
