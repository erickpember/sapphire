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
package com.datafascia.common.io;

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
