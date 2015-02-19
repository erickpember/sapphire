// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import com.datafascia.string.StringUtils;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

/**
 * Creates a DatafasciaApi object that can be used to interact with an API endpoint.
 */
@Slf4j
public class DatafasciaApiBuilder {
  /**
   * @param endpoint An endpoint URL to interact with.
   * @param user the user name to use
   * @param password the user password to use
   *
   * @return usable DatafasciaApi object.
   */
  public static DatafasciaApi endpoint(URI endpoint, String user, String password) {
    RestAdapter.Builder builder = new RestAdapter.Builder()
        .setConverter(new JacksonConverter())
        .setEndpoint(endpoint.toString());

    if ((user != null) && (password != null)) {
      setAuthentication(builder, user, password);
    }

    return builder.build().create(DatafasciaApi.class);
  }

  /**
   * Set basic authentication using user name and password
   *
   * @param builder The retrofit builder
   * @param user the user
   * @param password the password
   */
  private static void setAuthentication(RestAdapter.Builder builder, String user, String password) {
    builder.setRequestInterceptor(new RequestInterceptor() {
      @Override
      public void intercept(RequestFacade request) {
        String creds = "Basic " + StringUtils.base64Encode(user + ":" + password);
        request.addHeader("Authorization", creds);
      }
    });
  }

  private DatafasciaApiBuilder() {
  }
}
