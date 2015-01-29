// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.reflections;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

/**
 * Utility functions for package associated functions
 */
@Slf4j
public class PackageUtils {
  /**
   * Return list of classes in package
   *
   * @param packageName the package name to get all classes of
   */
  public static Set<Class<?>> classes(String packageName) {
    Reflections reflections = new Reflections(new ConfigurationBuilder()
        .setScanners(new SubTypesScanner(false), new ResourcesScanner())
        .setUrls(ClasspathHelper.forPackage(packageName))
        .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName))));

    return reflections.getSubTypesOf(Object.class);
  }
}
