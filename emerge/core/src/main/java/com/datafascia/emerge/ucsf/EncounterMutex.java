// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import lombok.extern.slf4j.Slf4j;

/**
 * Ensures exclusive access to resource.
 */
@Slf4j
public class EncounterMutex {

  private ConcurrentHashMap<String, Semaphore> identifierToLockMap = new ConcurrentHashMap<>();

  /**
   * Acquires exclusive access to resource. If the resource is already in use, blocks the calling
   * thread until the thread can acquire the resource.
   *
   * @param identifier
   *     resource identifier
   */
  public void acquire(String identifier) {
    Semaphore lock = identifierToLockMap.computeIfAbsent(identifier, id -> new Semaphore(1, true));
    log.debug("Acquiring mutex {} for encounter {}", lock, identifier);
    lock.acquireUninterruptibly();
    identifierToLockMap.put(identifier, lock);
  }

  /**
   * Releases exclusive access to resource.
   *
   * @param identifier
   *     resource identifier
   */
  public void release(String identifier) {
    Semaphore lock = identifierToLockMap.remove(identifier);
    log.debug("Releasing mutex {} for encounter {}", lock, identifier);
    if (lock != null) {
      lock.release();
    }
  }
}
