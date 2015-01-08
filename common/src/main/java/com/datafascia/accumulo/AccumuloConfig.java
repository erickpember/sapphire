// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.Setter;

/**
 * Configuration parameters for Accumulo.
 */
@Slf4j @Getter @Setter @JsonAutoDetect
public class AccumuloConfig {
  /** Accumulo type mock */
  @JsonIgnore
  public static String MOCK = "mock";
  /** Accumulo type real*/
  @JsonIgnore
  public static String REAL = "real";

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
}
