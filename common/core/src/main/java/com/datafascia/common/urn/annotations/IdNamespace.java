// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.urn.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to define ID URN name space associated with a class. This is a class
 * level annotation and needs to be put on the most specific type of the class and not necessarily
 * on the class containing the '@id' field. For example if the '@id' field is in BaseClass and two
 * different classes Foo and Bar that extend BaseClass, then this annotation needs to be in both Foo
 * and Bar with different values for the namespace.
 *
 * Essentially IdNamespace annotation is a bijective function between class name and the associated
 * URN namespace.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IdNamespace {
  /**
   * @return hardcoded version for compatibility tests
   */
  int version() default 1;

  /**
   * @return namespace
   */
  String value();
}
