// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * Utilities for making REST calls.
 */
public class RestUtils {
  /**
   * @param urlToRead The URL from which to GET.
   * @param sslSocketFactory Factory for SSL sockets.
   * @return A JSON string for a given GET.
   * @throws IOException When unable to open the key file or establish a connection.
   */
  public static String getJson(String urlToRead, SSLSocketFactory sslSocketFactory)
      throws IOException {
    URL url = new URL(urlToRead);
    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
    if (sslSocketFactory != null) {
      conn.setSSLSocketFactory(sslSocketFactory);
    }

    conn.setRequestMethod("GET");
    conn.setRequestProperty("Accept", "application/json");
    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

    // Build the response string.
    String line;
    StringBuilder result = new StringBuilder();
    while ((line = rd.readLine()) != null) {
      result.append(line);
    }
    rd.close();

    return result.toString();
  }
}
