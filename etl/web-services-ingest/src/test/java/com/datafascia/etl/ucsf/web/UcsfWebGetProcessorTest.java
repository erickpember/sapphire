// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web;

import com.datafascia.etl.ucsf.web.config.UcsfWebGetConfig;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * A test class for the {@link UcsfWebGetProcessor}
 */
@Slf4j
public class UcsfWebGetProcessorTest {
  public static String lastUrl;

  @Test
  public void testEpicDateToInstant() throws IOException, InterruptedException {
    Instant newFormat = UcsfWebGetProcessor.epicDateToInstant("2016-06-28T11:13:31Z");
    assertEquals(newFormat, Instant.parse("2016-06-28T11:13:31Z"));
    Instant oldFormat = UcsfWebGetProcessor.epicDateToInstant("/Date(1450192679000-0800)/");
    assertEquals(oldFormat, Instant.parse("2015-12-15T23:17:59Z"));
    Instant newFormatNull = UcsfWebGetProcessor.epicDateToInstant("0001-01-01T00:00:00");
    assertEquals(newFormatNull, Instant.EPOCH);
  }

  @Test
  public void testOnTrigger() throws IOException, InterruptedException {
    int webPort;
    // Find a random open port.
    try (
        ServerSocket serverSocket = new ServerSocket(0)) {
      webPort = serverSocket.getLocalPort();
    }

    HttpServer server = HttpServer.create(new InetSocketAddress(webPort), 0);
    server.createContext("/test", new TestHandler());
    server.setExecutor(null);
    server.start();

    UcsfWebGetConfig config = UcsfWebGetConfig.load("src/test/resources/webgetconfig.yml");
    for (String url : config.urls) {
      String patched = url.replace("{{PORT}}", Integer.toString(webPort));
      config.urls.remove(url);
      config.urls.add(patched);
    }

    config.filename = "medAdmin";

    TestRunner runner = TestRunners.newTestRunner(UcsfWebGetProcessor.class);
    ((UcsfWebGetProcessor) runner.getProcessor()).setConfig(config);
    runner.setProperty(UcsfWebGetProcessor.YAMLPATH, "null");
    runner.run();

    runner.assertAllFlowFilesTransferred(UcsfWebGetProcessor.SUCCESS, 1);
    MockFlowFile mockFile = runner.getFlowFilesForRelationship(UcsfWebGetProcessor.SUCCESS).get(0);

    mockFile.assertContentEquals(getTestFile(lastUrl));

    runner.run();
    mockFile = runner.getFlowFilesForRelationship(UcsfWebGetProcessor.SUCCESS).get(1);
    mockFile.assertContentEquals(getTestFile(lastUrl));
    runner.shutdown();
    server.stop(0);
  }

  @Slf4j
  static class TestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
      try {
        lastUrl = httpExchange.getRequestURI().toString();
        String response = getTestFile(httpExchange.getRequestURI().toString());
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream out = httpExchange.getResponseBody();
        out.write(response.getBytes());
        out.close();
      } catch (Exception e) {
        log.error("FAILURE: " + e.getMessage(), e);
        httpExchange.sendResponseHeaders(500, 0);
      }
    }
  }

  static String getTestFile(String uri) throws IOException {
    String[] uriParts = uri.split("/");
    String filename = uriParts[uriParts.length - 1]
        .replace("?", "{questionMark}")
        .replace(":", "{colon}") + ".json";
    URL url = Resources.getResource(filename);
    return Resources.toString(url, Charsets.UTF_8);
  }
}
