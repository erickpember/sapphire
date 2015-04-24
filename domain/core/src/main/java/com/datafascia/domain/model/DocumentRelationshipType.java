// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

/**
 * Represents how a DocumentReference relates to another DocumentReference.
 */
public enum DocumentRelationshipType {
  REPLACES,
  TRANSFORMS,
  SIGNS,
  APPENDS
}
