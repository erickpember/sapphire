// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.urn;

import com.datafascia.common.reflect.PackageUtils;
import com.datafascia.common.urn.annotations.IdNamespace;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Maps between Java classes and URN namespaces
 */
@Slf4j
public class URNMap {
  /** Map from Java class name to Id URN namespace */
  private static final Map<String, String> ID_CLASS_NS = new HashMap<>();

  /** Map from Id URN namespace to Java class */
  private static final Map<String, Class<?>> ID_NS_CLASS = new HashMap<>();

  /**
   * Load Id namespace mapping based on annotations on the classes
   *
   * @param packageName the package to load from
   */
  public static void idNSMapping(String packageName) {
    for (Class<?> clazz : PackageUtils.withTypeAnnotations(packageName, IdNamespace.class)) {
      IdNamespace idNS = clazz.getAnnotation(IdNamespace.class);
      // Annonymous inner class initialization seems to cause this
      if (idNS == null) {
        continue;
      }
      log.info("Found IdNamespace annotation in " + clazz.getName());
      if ((getClassFromIdNamespace(idNS.value()) != null) ||
          (getIdNamespace(clazz) != null)) {
        log.error("Duplicate ID namespace mapping found for " + idNS.value());
      } else {
        ID_CLASS_NS.put(clazz.getName(), idNS.value());
        ID_NS_CLASS.put(idNS.value(), clazz);
      }
    }
  }

  /**
   * @param clazz the clazz
   *
   * @return the ID namespace associated with class.
   */
  public static String getIdNamespace(Class<?> clazz) {
    if (clazz == null) {
      throw new IllegalArgumentException("Class name cannot be null");
    }

    return getIdNamespace(clazz.getName());
  }

  /**
   * @param name the name of the class
   *
   * @return the ID namespace associated with class.
   */
  public static String getIdNamespace(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Class name cannot be null");
    }

    return ID_CLASS_NS.get(name);
  }

  /**
   * @param ns the namespace of the URN
   *
   * @return class associated with ID namespace
   */
  public static Class<?> getClassFromIdNamespace(String ns) {
    if (ns == null) {
      throw new IllegalArgumentException("URN namespace cannot be null");
    }

    return ID_NS_CLASS.get(ns);
  }

  private URNMap() {
  }
}
