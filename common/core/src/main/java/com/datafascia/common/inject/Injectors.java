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
package com.datafascia.common.inject;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import java.util.ArrayList;
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

  /**
   * Sets Guice injector.
   *
   * @param aInjector
   *     to set
   */
  public static synchronized void setInjector(Injector aInjector) {
    injector = aInjector;
  }

  /**
   * Creates providers using {@link ServiceLoader}, then injects into the providers.
   *
   * @param <S>
   *     service type
   * @param service
   *     interface or abstract class representing the service
   * @param injector
   *     Guice injector
   * @return providers
   */
  public static <S> List<S> loadService(Class<S> service, Injector injector) {
    List<S> providers = new ArrayList<>();
    ServiceLoader<S> serviceLoader = ServiceLoader.load(service);
    for (S provider : serviceLoader) {
      injector.injectMembers(provider);
      providers.add(provider);
    }

    return providers;
  }

  /**
   * Creates providers using {@link ServiceLoader}, then injects into the providers.
   *
   * @param <S>
   *     service type
   * @param service
   *     interface or abstract class representing the service
   * @return providers
   */
  public static <S> List<S> loadService(Class<S> service) {
    return loadService(service, Injectors.getInjector());
  }
}
