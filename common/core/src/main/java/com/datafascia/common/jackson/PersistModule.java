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
package com.datafascia.common.jackson;

import com.datafascia.common.persist.Id;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.time.LocalDate;

/**
 * Module adds serializer and deserializer for persist classes.
 */
public class PersistModule extends SimpleModule {

  /**
   * Constructor
   */
  public PersistModule() {
    addSerializer(Id.class, new IdSerializer());
    addDeserializer(Id.class, new IdDeserializer());
    addSerializer(LocalDate.class, new LocalDateSerializer());
    addDeserializer(LocalDate.class, new LocalDateDeserializer());
  }
}
