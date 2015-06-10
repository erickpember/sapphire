// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web;

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
import lombok.extern.slf4j.Slf4j;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.testng.Reporter;
import org.testng.annotations.Test;

/**
 * A test class for the UcsfWebGetProcessor
 */
@Slf4j
public class UcsfWebGetProcessorTest {
  public static String lastUrl;

  @Test
  public void testOnTrigger() throws IOException, InterruptedException {
    // Find a random open port.
    ServerSocket ss = new ServerSocket(0);
    int webPort = ss.getLocalPort();
    ss.close();

    HttpServer server = HttpServer.create(new InetSocketAddress(webPort), 0);
    server.createContext("/test", new TestHandler());
    server.setExecutor(null);
    server.start();

    String url = "http://localhost:" + webPort + "/test/GetMedAdminUnit?ListID=15860";

    TestRunner runner = TestRunners.newTestRunner(UcsfWebGetProcessor.class);
    runner.setProperty(UcsfWebGetProcessor.URL, url);
    runner.setProperty(UcsfWebGetProcessor.FILENAME, "medAdmin");
    runner.run();

    runner.assertAllFlowFilesTransferred(UcsfWebGetProcessor.SUCCESS, 1);
    MockFlowFile mff = runner.getFlowFilesForRelationship(UcsfWebGetProcessor.SUCCESS).get(0);

    mff.assertContentEquals(getTestFile(lastUrl));

    runner.run();
    mff = runner.getFlowFilesForRelationship(UcsfWebGetProcessor.SUCCESS).get(1);
    mff.assertContentEquals(getTestFile(lastUrl));
    runner.shutdown();
    server.stop(0);
  }

  @Slf4j
  static class TestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
      try {
        lastUrl = t.getRequestURI().toString();
        Reporter.log("### Got url: " + lastUrl);
        String response = getTestFile(t.getRequestURI().toString());
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
      } catch (Exception e) {
        Reporter.log("FAILURE: " + e.getMessage() + "\n\n" + e.getStackTrace());
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
