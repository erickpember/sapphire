// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

/**
 * Codes specifying how two observations are related
 */
public enum ObservationRelationshipType {
  HAS_COMPONENT,
  HAS_MEMBER,
  DERIVED_FROM,
  SEQUEL_TO,
  REPLACES,
  QUALIFIED_BY,
  INTERFERED_BY
}
