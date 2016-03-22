// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.datafascia.etl.ucsf.hl7.ProcessHL7;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

  private static class Worker {
    public ThreadPoolExecutor executor;
    public AtomicInteger pendingMessageCount = new AtomicInteger();

    public Worker(String id) {
      executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(
          1, new NamedThreadFactory(String.format(NAME_PREFIX, id)));
    }
  }

  private static final String NAME_PREFIX =
      EncounterExecutor.class.getSimpleName() + " encounter %s thread ";

  @Inject
  private PlayMessages playMessages;

  private ConcurrentHashMap<String, Worker> encounterToWorkerMap = new ConcurrentHashMap<>();

  @Inject
  private void initialize(MetricRegistry metrics) {
    metrics.register(
        MetricRegistry.name(ProcessHL7.class, "pendingEncounterCount"),
        (Gauge<Integer>) () -> encounterToWorkerMap.size());
    metrics.register(
        MetricRegistry.name(ProcessHL7.class, "pendingMessageCount"),
        (Gauge<Integer>) () -> getPendingMessageCount());
  }

  private Worker getWorker(String encounterIdentifier) {
    return encounterToWorkerMap.computeIfAbsent(
        encounterIdentifier,
        id -> new Worker(id));
  }

  private void removeIdleWorkers() {
    Iterator<Map.Entry<String, Worker>> iterator =
        encounterToWorkerMap.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Worker> entry = iterator.next();
      Worker worker = entry.getValue();
      if (worker.executor.getActiveCount() == 0 && worker.executor.getQueue().isEmpty()) {
        worker.executor.shutdown();
        iterator.remove();
      }
    }
  }

  private int getPendingMessageCount() {
    int count = 0;
    for (Worker worker : encounterToWorkerMap.values()) {
      count += worker.pendingMessageCount.get();
    }
    return count;
  }

  /**
   * Plays pending HL7 messages for an encounter.
   *
   * @param encounterIdentifier
   *     encounter identifier
   */
  public synchronized void accept(String encounterIdentifier) {
    Worker worker = getWorker(encounterIdentifier);
    worker.executor.submit(
        () -> playMessages.accept(encounterIdentifier, worker.pendingMessageCount));

    removeIdleWorkers();
  }
}
