// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.jcommander.converters;

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
