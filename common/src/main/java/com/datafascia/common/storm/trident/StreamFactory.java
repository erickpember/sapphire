// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.storm.trident;

import storm.trident.Stream;
import storm.trident.TridentTopology;

/**
 * Creates Trident stream.
 */
public interface StreamFactory {

  /**
   * Creates Trident stream.
   *
   * @param topology
   *     Trident topology
   * @return Trident stream
   */
  Stream newStream(TridentTopology topology);
}
