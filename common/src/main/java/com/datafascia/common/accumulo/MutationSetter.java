// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.accumulo;

/**
 * Puts write operations in a mutation.
 */
@FunctionalInterface
public interface MutationSetter {

  /**
   * Puts write operations in a mutation.
   *
   * @param mutationBuilder
   *     mutation builder
   */
  void putWriteOperations(MutationBuilder mutationBuilder);
}
