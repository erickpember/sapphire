// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.persist.entity;

import com.datafascia.common.persist.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Element in a materialized path in an entity ID.
 */
@AllArgsConstructor @Data
public class TypeAndId {

  private Class<?> type;
  private Id<?> id;
}
