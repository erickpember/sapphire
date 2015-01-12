// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

/**
 * Creates a DatafasciaApi object that can be used to interact with an API endpoint.
 */
@Slf4j
public class DatafasciaApiBuilder {
  /**
   * @param endpoint An endpoint URL to interact with.
   *
   * @return usable DatafasciaApi object.
   */
  public static DatafasciaApi GetAPI(URI endpoint){
    RestAdapter restAdapter = new RestAdapter.Builder()
      .setConverter(new JacksonConverter())
      .setEndpoint(endpoint.toString())
      .build();

    return restAdapter.create(DatafasciaApi.class);
  }
}
