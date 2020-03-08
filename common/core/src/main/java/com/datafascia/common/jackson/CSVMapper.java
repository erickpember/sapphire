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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Jackson mapper class used to read and write CSV models
 *
 * @param <T> the class to extrace CSV information from
 */
@Slf4j
public class CSVMapper<T> {
  @Getter
  private final List<String> headers;
  private final ObjectReader reader;
  private final ObjectWriter writer;

  /**
   * Construct mapper with default header
   *
   * @param clazz the class associated with model
   */
  public CSVMapper(Class<T> clazz) {
    this(clazz, defaultHeaders(clazz));
  }

  /**
   * Construct mapper for associated header.
   *
   * @param clazz the class associated with model
   * @param headers the headers for the csv
   */
  public CSVMapper(Class<T> clazz, List<String> headers) {
    CsvMapper mapper = new CsvMapper();
    mapper.configure(CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING, true);
    CsvSchema.Builder builder = CsvSchema.builder();
    for (String header : headers) {
      builder.addColumn(header);
    }
    CsvSchema schema = builder.build();

    reader = mapper.reader(clazz).with(schema);
    writer = mapper.writer(schema);
    this.headers = headers;
  }

  /**
   * Create object from string using ',' as separators.
   *
   * @param line the line to parse values from
   *
   * @return model instance created from string
   *
   * @throws java.io.IOException from underlying Jackson errors
   */
  public T fromCSV(String line) throws IOException {
    return reader.readValue(line);
  }

  /**
   * @param model the model to transform
   *
   * @return model as string
   *
   * @throws com.fasterxml.jackson.core.JsonProcessingException from underlying Jackson errors
   */
  public String asCSV(T model) throws JsonProcessingException {
    return writer.writeValueAsString(model).trim();
  }

  /**
   * @return the headers for the object
   */
  private static List<String> defaultHeaders(Class<?> clazz) {
    List<String> headers = new ArrayList<>();
    for (Field f : clazz.getDeclaredFields()) {
      if (f.isAnnotationPresent(JsonProperty.class)) {
        JsonProperty property = f.getAnnotation(JsonProperty.class);
        headers.add(property.index(), property.value());
      }
    }

    return headers;
  }
}
