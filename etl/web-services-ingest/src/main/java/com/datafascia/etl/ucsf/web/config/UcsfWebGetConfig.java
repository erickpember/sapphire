// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
