// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.configurations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.dropwizard.Configuration;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@JsonAutoDetect
public class APIConfiguration extends Configuration {
  @NotEmpty @Getter @Setter
  private String defaultPackage;
}
