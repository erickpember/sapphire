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
package com.datafascia.common.nifi;

import com.datafascia.common.inject.Injectors;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.logging.ProcessorLog;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;

/**
 * NiFi processor that will set its fields by dependency injection. To use,
 * extend this class and define fields annotated with {@code @Inject} which will
 * be set with the dependencies.
 */
public abstract class DependencyInjectingProcessor extends AbstractProcessor {

  protected ProcessorLog log;

  /**
   * Invoked after dependencies injected. The default implementation does
   * nothing.
   *
   * @param processContext
   *     process context
   */
  protected void onInjected(ProcessContext processContext) {
  }

  /**
   * When this processor is scheduled to run, injects dependencies.
   *
   * @param processContext
   *     process context
   */
  @OnScheduled
  public void injectMembers(ProcessContext processContext) {
    log = getLogger();

    Injectors.getInjector().injectMembers(this);
    onInjected(processContext);
  }
}
