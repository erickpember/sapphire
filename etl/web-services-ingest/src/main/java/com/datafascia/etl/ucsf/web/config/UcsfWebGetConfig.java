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
package com.datafascia.etl.ucsf.web.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.yaml.snakeyaml.Yaml;

/**
 * Configuration POJO for the UCSF web get processor.
 */
public class UcsfWebGetConfig {
  public ArrayList<String> urls;
  public boolean followRedirects = false;
  public int connectionTimeoutMilliseconds = 30000;
  public String acceptContentType = "application/json";
  public int dataTimeoutMilliseconds = 30000;
  public String filename;
  public String username;
  public String password;
  public String userAgent;
  public String trustStore;
  public String trustStorePassword;

  /**
   * Loads a given YAML file config.
   * @param filename The file to use for configuration.
   * @return The configuration object.
   * @throws FileNotFoundException If the file isn't found.
   * @throws UnsupportedEncodingException If the system doesn't support UTF-8.
   */
  public static UcsfWebGetConfig load(String filename)
      throws FileNotFoundException, UnsupportedEncodingException {
    Yaml yaml = new Yaml();
    return yaml.loadAs(new InputStreamReader(new FileInputStream(filename),
        "UTF-8"), UcsfWebGetConfig.class);
  }
}
