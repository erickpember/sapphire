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

  private Yaml yaml = new Yaml();

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

  protected void setNodeValue(ConfigurationNode targetNode, Object source) {
    if (source instanceof Map<?, ?>) {
      for (Map.Entry<String, Object> entry : ((Map<String, Object>) source).entrySet()) {
        Node childNode = new Node(entry.getKey());
        childNode.setReference(entry);
        setNodeValue(childNode, entry.getValue());
        targetNode.addChild(childNode);
      }
    } else if (source instanceof Collection) {
      for (Object child : (Collection) source) {
        Node childNode = new Node("item");
        childNode.setReference(child);
        setNodeValue(childNode, child);
        targetNode.addChild(childNode);
      }
    }

    targetNode.setValue(source);
  }

  @Override
  public void save(Writer writer) throws ConfigurationException {
    throw new UnsupportedOperationException("save not implemented");
  }
}
