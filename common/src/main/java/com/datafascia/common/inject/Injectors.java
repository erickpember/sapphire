// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.inject;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import java.util.List;
import java.util.ServiceLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides a Guice injector to code which cannot otherwise be passed an injector.
 */
@Slf4j
public class Injectors {

  private static Module[] overrides = new Module[0];
  private static Injector injector;

  // Private constructor disallows creating instances of this class.
  private Injectors() {
  }

  /**
   * Sets overriding modules.
   *
   * @param overrides
   *     overriding modules
   */
  public static void overrideWith(Module... overrides) {
    Injectors.overrides = overrides;
  }

  /**
   * Gets a Guice injector.
   *
   * @return injector
   */
  public static synchronized Injector getInjector() {
    if (injector == null) {
      injector = createInjector();
    }
    return injector;
  }

  private static Injector createInjector() {
    ServiceLoader<Module> serviceLoader = ServiceLoader.load(Module.class);
    List<Module> modules = Lists.newArrayList(serviceLoader.iterator());
    if (modules.isEmpty()) {
      throw new IllegalStateException(
          "ServiceLoader cannot find class implementing " + Module.class.getName());
    }

    if (overrides.length > 0) {
      log.info("Creating injector from modules {} with overrides {}", modules, overrides);
      return Guice.createInjector(Modules.override(modules).with(overrides));
    } else {
      log.info("Creating injector from modules {}", modules);
      return Guice.createInjector(modules);
    }
  }
}
