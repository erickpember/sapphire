// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import javax.inject.Inject;

/**
 * Plays pending HL7 messages for an encounter.
 */
public class EncounterExecutor implements Consumer<String> {

  private static class NamedThreadFactory implements ThreadFactory {
    private ThreadGroup group;
    private String namePrefix;
    private AtomicInteger threadNum = new AtomicInteger(1);

    private NamedThreadFactory(String namePrefix) {
      SecurityManager s = System.getSecurityManager();
      this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
      this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(group, runnable, namePrefix + threadNum.getAndIncrement(), 0);
      thread.setDaemon(true);
      if (thread.getPriority() != Thread.NORM_PRIORITY) {
        thread.setPriority(Thread.NORM_PRIORITY);
      }
      return thread;
    }
  }

  private static final String NAME_PREFIX =
      EncounterExecutor.class.getSimpleName() + " encounter %s thread ";

  @Inject
  private PlayMessages playMessages;

  private Map<String, ThreadPoolExecutor> identifierToExecutorMap = new HashMap<>();

  private ExecutorService getExecutor(String encounterIdentifier) {
    return identifierToExecutorMap.computeIfAbsent(
        encounterIdentifier,
        id ->
            (ThreadPoolExecutor) Executors.newFixedThreadPool(
                1, new NamedThreadFactory(String.format(NAME_PREFIX, id))));
  }

  private void removeEmptyExecutors() {
    Iterator<Map.Entry<String, ThreadPoolExecutor>> iterator =
        identifierToExecutorMap.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, ThreadPoolExecutor> entry = iterator.next();
      ThreadPoolExecutor executor = entry.getValue();
      if (executor.getQueue().isEmpty()) {
        executor.shutdown();
        iterator.remove();
      }
    }
  }

  /**
   * Plays pending HL7 messages for an encounter.
   *
   * @param encounterIdentifier
   *     encounter identifier
   */
  public synchronized void accept(String encounterIdentifier) {
    ExecutorService executor = getExecutor(encounterIdentifier);
    executor.submit(() -> playMessages.accept(encounterIdentifier));

    removeEmptyExecutors();
  }
}
