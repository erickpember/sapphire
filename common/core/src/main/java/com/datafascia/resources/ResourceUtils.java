// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.resources;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;

/**
 * A set of helper funtions for resource files.
 */
@Slf4j
public class ResourceUtils {
  /**
   * Singleton class
   */
  private ResourceUtils() {
  }

  /**
   * Reads the given resource file from {@code resourcePath} and returns its contents as a
   * UTF-8 string.
   *
   * @param fileName the file name of the resource file
   *
   * @return the contents of {@code resourcePath/{fileName}}
   *
   * @throws IOException if {@code fileName} doesn't exist or can't be opened
   */
  public static String resource(String fileName) throws IOException {
    return resource(fileName, Charsets.UTF_8);
  }

  /**
   * Reads the given resource file from {@code resourcePath} and returns its contents as a
   * string.
   *
   * @param fileName the file name of the resource file
   * @param charset the character set of {@code fileName}
   *
   * @return the contents of {@code resourcePath/{fileName}}
   *
   * @throws IOException if {@code fileName} doesn't exist or can't be opened
   */
  public static String resource(String fileName, Charset charset) throws IOException {
    log.debug("Fetching resource: " + fileName + " with charset: " + charset);
    return Resources.toString(Resources.getResource(fileName), charset).trim();
  }
}
