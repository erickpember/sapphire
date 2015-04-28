// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

/**
 * Represents a comparator, as used in NumericQuantity to be used in cases
 * where a quantity includes a comparator, serving as an upper or lower limit.
 */
public enum Comparator {
  GREATER,
  LESS,
  GREATER_OR_EQUAL,
  LESS_OR_EQUAL
}
