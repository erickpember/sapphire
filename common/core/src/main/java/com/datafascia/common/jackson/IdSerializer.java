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
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * Jackson serializer for {@link Id}.
 * <p>
 * NOTE: The SuppressWarnings is necessary because we do not have the type parameter for the Id.
 */
@Slf4j @SuppressWarnings("rawtypes")
public class IdSerializer extends StdSerializer<Id> {
  /**
   * Null constructor needed by Jackson. Should never be called otherwise
   */
  public IdSerializer() {
    super(Id.class);
  }

  @Override
  public void serialize(Id value, JsonGenerator jsonGenerator, SerializerProvider provider)
      throws IOException {

    jsonGenerator.writeString(value.toString());
  }
}
