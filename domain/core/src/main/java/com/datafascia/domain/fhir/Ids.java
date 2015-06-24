// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.fhir;

import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;

/**
 * Logical id utility methods
 */
public class Ids {

  // Private constructor disallows creating instances of this class.
  private Ids() {
  }

  /**
   * Converts logical id to data store primary key.
   *
   * @param logicalId
   *     logical id
   * @param <E>
   *     entity type
   * @return primary key
   */
  public static <E> Id<E> toPrimaryKey(IdDt logicalId) {
    return Id.of(logicalId.getIdPart());
  }
}
