// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides a map-like interface to get and put fetched times while saving to persistent storage.
 */
@Slf4j
public class UrlToFetchedTimeMap {

  private static final String TIMESTORE_FILENAME = "/tmp/df-etl-webget-timestore";
  private static final Path FILE_PATH = Paths.get(TIMESTORE_FILENAME);

  private ConcurrentHashMap<String, String> lastTimestamps = new ConcurrentHashMap<>();

  /**
   * Clears stored dates for the query interval, forcing a full refresh.
   */
  public void clear() {
    lastTimestamps.clear();

    try {
      Files.deleteIfExists(FILE_PATH);
    } catch (IOException e) {
      throw new IllegalStateException("Could not delete file " + FILE_PATH);
    }
  }

  private void updateTimeStore() {
    // Write a new file, then move the new file to the target file name.
    String newFileName = TIMESTORE_FILENAME + '-' + Thread.currentThread().getId();
    Path newFilePath = Paths.get(newFileName);
    try {
      OutputStream fos = Files.newOutputStream(newFilePath);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(lastTimestamps);
      oos.close();
      fos.close();

      Files.move(newFilePath, FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ex) {
      throw new IllegalStateException("Could not write file " + FILE_PATH, ex);
    }
  }

  /**
   * Maps the specified key to the specified value.
   *
   * @param key
   *     key with which the specified value is to be associated
   * @param value
   *     value to be associated with the specified key
   */
  public void put(String key, String value) {
    lastTimestamps.put(key, value);
    updateTimeStore();
  }

  private void loadTimeStore() {
    try {
      InputStream fis = Files.newInputStream(FILE_PATH);
      ObjectInputStream ois = new ObjectInputStream(fis);
      lastTimestamps = (ConcurrentHashMap) ois.readObject();
      ois.close();
      fis.close();
    } catch (IOException ex) {
      log.info("Could not read file {}. Performing full refresh.", FILE_PATH, ex);
      clear();
    } catch (ClassNotFoundException ex) {
      throw new IllegalStateException("Error loading timestore.", ex);
    }
  }

  /**
   * Gets the value to which the specified key is mapped.
   *
   * @param key
   *     key to find
   * @return value, or {@code null} if this map contains no mapping for the key
   */
  public String get(String key) {
    loadTimeStore();
    return lastTimestamps.get(key);
  }
}
