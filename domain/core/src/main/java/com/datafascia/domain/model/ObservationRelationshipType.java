// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

/**
 * Codes specifying how two observations are related
 */
public enum ObservationRelationshipType {
  HasComponent,
  HasMember,
  DerivedFrom,
  SequelTo,
  Replaces,
  QualifiedBy,
  InterferedBy
}
