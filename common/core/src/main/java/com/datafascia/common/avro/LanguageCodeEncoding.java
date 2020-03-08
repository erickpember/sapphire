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
package com.datafascia.common.avro;

import com.neovisionaries.i18n.LanguageCode;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;

/**
 * Converts between {@link LanguageCode} and String.
 */
public class LanguageCodeEncoding extends CustomEncoding<LanguageCode> {

  /**
   * Constructor
   */
  public LanguageCodeEncoding() {
    schema = Schema.create(Schema.Type.STRING);
    schema.addProp("CustomEncoding", LanguageCodeEncoding.class.getSimpleName());
  }

  @Override
  protected void write(Object datum, Encoder out) throws IOException {
    out.writeString(((LanguageCode) datum).name());
  }

  @Override
  protected LanguageCode read(Object reuse, Decoder in) throws IOException {
    return LanguageCode.getByCode(in.readString());
  }
}
