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

import com.fasterxml.jackson.databind.module.SimpleModule;
import java.time.Instant;
import javax.measure.Unit;

/**
 * Module containing serializer/deserializers for units of measurement.
 */
public class UnitsMapperModule extends SimpleModule {
  /**
   * Create a new UnitsMapperModule
   */
  public UnitsMapperModule() {
    super();
    addSerializer(Instant.class, new InstantSerializer());
    addDeserializer(Instant.class, new InstantDeserializer());
    addSerializer(Unit.class, new UnitSerializer());
    addDeserializer(Unit.class, new UnitDeserializer());
  }
}
