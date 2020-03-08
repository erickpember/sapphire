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
package com.datafascia.common.persist;

/**
 * Provides a code representing an enum constant in a data store or message
 * protocol. Storing a code instead of the enum constant name allows us to
 * rename the enum constant without having to worry about migrating values
 * previously stored with the old name.
 *
 * @param <C>
 *     code representation type
 */
public interface Code<C> {

  /**
   * Gets code representing an enum constant.
   *
   * @return code
   */
  C getCode();
}
