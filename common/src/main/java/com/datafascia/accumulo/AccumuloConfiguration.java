// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * Configuration parameters for Accumulo.
 */
@AllArgsConstructor @Builder @Data @NoArgsConstructor
public class AccumuloConfiguration {

  /** Property name for Accumulo instance */
  public static final String INSTANCE = "instance";
  /** Property name for ZooKeeper instance */
  public static final String ZOOKEEPERS = "zooKeepers";
  /** Property name for Accumulo user */
  public static final String USER = "user";
  /** Property name for Accumulo password */
  public static final String PASSWORD = "password";

  /** user to configure in tests */
  public static final String TESTING_USER = "root";
  /** password to configure in tests */
  public static final String TESTING_PASSWORD = "secret";

  /** Accumulo instance name */
  private String instance;
  /** Accumulo ZooKeeper list */
  private String zooKeepers;
  /** Accumulo user name */
  private String user;
  /** Accumulo user password */
  private String password;

  /**
   * Create configuration from properties file
   *
   * @param config the properties file to load information from
   */
  public AccumuloConfiguration(String config) {
    Properties props = new Properties();
    try {
      props.load(new FileInputStream(config));
    } catch (IOException e) {
      throw new IllegalArgumentException("Accumulo config file error.", e);
    }

    instance = props.getProperty(INSTANCE);
    zooKeepers = props.getProperty(ZOOKEEPERS);
    user = props.getProperty(USER);
    password = props.getProperty(PASSWORD);
  }
}
