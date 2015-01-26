// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Configuration parameters for Accumulo.
 */
@Slf4j @Getter @Setter @JsonAutoDetect @NoArgsConstructor
public class AccumuloConfig {
  /** Property name for ZooKeeper instance */
  public static final String ZOOKEEPERS = "zooKeepers";
  /** Property name for Accumulo instance */
  public static final String INSTANCE = "instance";
  /** Property name for Accumulo user */
  public static final String USER = "user";
  /** Property name for Accumulo password */
  public static final String PASSWORD = "password";
  /** Property name for Accumulo directory */
  public static final String DIRECTORY = "directory";
  /** Property name for Accumulo type */
  public static final String TYPE = "type";

  /** Default user name */
  public static final String ROOT = "root";
  /** Default user password */
  public static final String USER_PASSWORD = "secret";

  /** Accumulo type mock */
  @JsonIgnore
  public static String MOCK = "mock";
  /** Accumulo type real */
  @JsonIgnore
  public static String REAL = "real";
  /** Accumulo type mini */
  @JsonIgnore
  public static String MINI = "mini";

  /** Accumulo instance name */
  private String instance;
  /** Accumulo user name */
  private String user;
  /** Accumulo user password */
  private String password;
  /** Accumulo ZooKeeper list */
  private String zooKeepers;
  /** Accumulo type instance */
  private String type = REAL;

  /**
   * Create configuration from properties file
   *
   * @param config the properties file to load information from
   */
  public AccumuloConfig(String config) {
    Properties props = new Properties();
    try {
      props.load(new FileInputStream(config));
    } catch (IOException e) {
      throw new IllegalArgumentException("Accumulo config file error.", e);
    }

    instance = props.getProperty(INSTANCE);
    user = props.getProperty(USER);
    password = props.getProperty(PASSWORD);
    zooKeepers = props.getProperty(ZOOKEEPERS);
    type = props.getProperty(TYPE);
 }

  /**
   * @return true if Accumulo type is real
   */
  public boolean isReal() {
    return type.equalsIgnoreCase(REAL);
  }

  /**
   * @return true if Accumulo type is mock
   */
  public boolean isMock() {
    return type.equalsIgnoreCase(MOCK);
  }

  /**
   * @return true if Accumulo type is mini
   */
  public boolean isMini() {
    return type.equalsIgnoreCase(MINI);
  }
}
