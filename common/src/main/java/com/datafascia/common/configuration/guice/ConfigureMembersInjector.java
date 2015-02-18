// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.configuration.guice;

import com.datafascia.common.configuration.ConfigurationNode;
import com.datafascia.common.configuration.Configure;
import com.google.common.base.CaseFormat;
import com.google.inject.MembersInjector;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * Injects values into fields and methods annotated with @{@link Configure}.
 *
 * @param <T>
 *     type to inject members of
 */
public class ConfigureMembersInjector<T> implements MembersInjector<T> {

  private static class Node {
    private final String key;
    private final HierarchicalConfiguration configuration;

    Node(String key, HierarchicalConfiguration configuration) {
      this.key = key;
      this.configuration = configuration;
    }
  }

  private static final String CONFIGURATION_NODE =
      "@" + ConfigurationNode.class.getSimpleName();
  private static final String CONFIGURE = "@" + Configure.class.getSimpleName();

  private final HierarchicalConfiguration rootConfiguration;

  /**
   * Constructor
   *
   * @param rootConfiguration
   *     root configuration
   */
  public ConfigureMembersInjector(HierarchicalConfiguration rootConfiguration) {
    this.rootConfiguration = rootConfiguration;
  }

  @Override
  public void injectMembers(T instance) {
    Class<?> clazz = instance.getClass();
    Node node = getNode(clazz);
    injectFields(instance, clazz, node);
    injectMethods(instance, clazz, node);
  }

  private Node getNode(Class<?> clazz) {
    ConfigurationNode node = clazz.getAnnotation(ConfigurationNode.class);
    String nodeKey = node.value();
    if (nodeKey.isEmpty()) {
      throw new IllegalArgumentException(
          CONFIGURATION_NODE + " on " + clazz.getName() + " must specify key");
    }

    return new Node(nodeKey, rootConfiguration.configurationAt(nodeKey));
  }

  private void injectFields(T instance, Class<?> clazz, Node node) {
    for (Field field : clazz.getDeclaredFields()) {
      Configure configure = field.getAnnotation(Configure.class);
      if (configure != null) {
        String propertyName = configure.property();
        if (propertyName.isEmpty()) {
          // Configuration property name was not explicitly specified.
          // Find the property by the field name.
          propertyName = field.getName();
        }

        Object value = getValue(node, propertyName);
        field.setAccessible(true);
        try {
          field.set(instance, ConvertUtils.convert(value, field.getType()));
        } catch (IllegalAccessException e) {
          throw new IllegalStateException("Cannot set field " + field, e);
        }
      }
    }
  }

  private void injectMethods(T instance, Class<?> clazz, Node node) {
    for (Method method : clazz.getDeclaredMethods()) {
      Configure configure = method.getAnnotation(Configure.class);
      if (configure != null) {
        String propertyName = configure.property();
        if (propertyName.isEmpty()) {
          // Configuration property name was not explicitly specified.
          // Find the property by the method name.
          propertyName = method.getName();
          if (propertyName.startsWith("set")) {
            propertyName = propertyName.substring(3);
            propertyName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, propertyName);
          }
        }

        invoke(method, instance, getValue(node, propertyName));
      }
    }
  }

  private Object getValue(Node node, String propertyName) {
    Object value = node.configuration.getProperty(propertyName);
    if (value == null) {
      throw new IllegalArgumentException(String.format(
          "Node [%s] does not have property [%s]", node.key, propertyName));
    }
    return value;
  }

  private void invoke(Method method, T instance, Object value) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length != 1) {
      throw new IllegalArgumentException(String.format(
          "Method %s annotated with %s must have exactly one parameter",
          method.getName(),
          CONFIGURE));
    }

    method.setAccessible(true);
    try {
      method.invoke(instance, ConvertUtils.convert(value, parameterTypes[0]));
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException("Cannot invoke method " + method.getName(), e);
    }
  }
}
