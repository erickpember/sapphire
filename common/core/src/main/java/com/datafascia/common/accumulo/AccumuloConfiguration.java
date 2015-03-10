// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.accumulo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration parameters for Accumulo.
 */
@AllArgsConstructor @Builder @Data @NoArgsConstructor
public class AccumuloConfiguration {
  /** Accumulo instance name */
  private String instance;

  /** Accumulo ZooKeeper list */
  private String zooKeepers;

  /** Accumulo user name */
  private String user;

  /** Accumulo user password */
  private String password;
}
