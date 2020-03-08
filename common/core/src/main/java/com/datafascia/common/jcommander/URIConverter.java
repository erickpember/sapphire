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
package com.datafascia.common.jcommander;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.BaseConverter;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Convert a string into a URI via JCommander environment
 */
public class URIConverter extends BaseConverter<URI> {
  /**
   * Default constructor
   *
   * @param optionName the option name
   */
  public URIConverter(String optionName) {
    super(optionName);
  }

  @Override
  public URI convert(String value) throws ParameterException {
    try {
      return new URI(value);
    } catch (URISyntaxException e) {
      throw new ParameterException(
          getErrorString(value, " not a RFC 2396 and RFC 2732 compliant URI."));
    }
  }
}
