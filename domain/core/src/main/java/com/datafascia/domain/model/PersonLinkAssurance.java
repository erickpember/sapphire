// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

/**
 * Part of a Person model, holds the level of confidence that this link represents
 * the same actual person, based on NIST Authentication Levels.
 */
public enum PersonLinkAssurance {
  LEVEL1,
  LEVEL2,
  LEVEL3,
  LEVEL4
}
