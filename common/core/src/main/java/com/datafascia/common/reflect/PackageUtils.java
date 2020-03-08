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
package com.datafascia.common.reflect;

import java.lang.annotation.Annotation;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

/**
 * Utility functions for package associated functions
 */
@Slf4j
public class PackageUtils {
  /**
   * @param packageName the package name to get all classes of
   *
   * @return the set of classes in the package
   */
  public static Set<Class<?>> classes(String packageName) {
    Reflections reflections = new Reflections(new ConfigurationBuilder()
        .setScanners(new SubTypesScanner(false))
        .setUrls(ClasspathHelper.forPackage(packageName))
        .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName))));

    return reflections.getSubTypesOf(Object.class);
  }

  /**
   * @param packageName the package name to get all classes of
   * @param annotation the annotation class
   *
   * @return list of classes in package having the particular type annotation
   */
  public static Set<Class<?>> withTypeAnnotations(String packageName,
      Class<? extends Annotation> annotation) {
    Reflections reflections = new Reflections(new ConfigurationBuilder()
        .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner())
        .setUrls(ClasspathHelper.forPackage(packageName))
        .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName))));

    return reflections.getTypesAnnotatedWith(annotation);
  }

  private PackageUtils() {
  }
}
