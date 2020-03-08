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
