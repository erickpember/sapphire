// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
