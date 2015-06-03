// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
