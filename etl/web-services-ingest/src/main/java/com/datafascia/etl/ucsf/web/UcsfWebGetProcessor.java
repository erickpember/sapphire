// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
// Forked from GetHTTP which is available under the Apache 2.0 license.
package com.datafascia.etl.ucsf.web;

import com.datafascia.etl.UrlToFetchedTimeMap;
import com.datafascia.etl.ucsf.web.config.UcsfWebGetConfig;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.CoreAttributes;
import org.apache.nifi.logging.ProcessorLog;
import org.apache.nifi.processor.AbstractSessionFactoryProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessSessionFactory;
import org.apache.nifi.processor.Processor;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.OutputStreamCallback;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.util.StopWatch;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kohsuke.MetaInfServices;

/**
 * A fork of GetHTTP that intercepts the response, pulls out the data extract date, and feeds it
 * back for the next fetch.
 */
@CapabilityDescription("Fetches a JSON file via HTTP")
@MetaInfServices(Processor.class)
@Tags({"get", "fetch", "poll", "http", "https", "ingest", "source", "input", "datafascia", "ucsf",
    "json"})
@WritesAttribute(attribute = "filename",
    description = "the filename is set to the name of the file on the remote server")
public class UcsfWebGetProcessor extends AbstractSessionFactoryProcessor {
  public static final String HEADER_ACCEPT = "Accept";

  public static final PropertyDescriptor YAMLPATH = new PropertyDescriptor.Builder()
      .name("YAML path")
      .description("The file path to the YAML config")
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .required(true)
      .build();

  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("success")
      .description("All files are transferred to the success relationship")
      .build();

  private boolean initialized = false;
  private UsernamePasswordCredentials credentials;
  private Set<Relationship> relationships;
  private List<PropertyDescriptor> properties;
  private UcsfWebGetConfig config;
  private final UrlToFetchedTimeMap urlToFetchedTimeMap = new UrlToFetchedTimeMap();

  @Override
  protected void init(final ProcessorInitializationContext context) {
    final Set<Relationship> relationships = new HashSet<>();
    relationships.add(SUCCESS);
    this.relationships = Collections.unmodifiableSet(relationships);

    final List<PropertyDescriptor> properties = new ArrayList<>();
    properties.add(YAMLPATH);
    this.properties = Collections.unmodifiableList(properties);
  }

  @Override
  public Set<Relationship> getRelationships() {
    return relationships;
  }

  @Override
  protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return properties;
  }

  /**
   * Translates the date format provided by the UCSF web services ("/Date(1234567890)/") to
   * Java Instants
   * @param epicDate The date in EPIC's odd format.
   * @return An instant.
   */
  public static Instant epicDateToInstant(String epicDate) {
    Instant date = Instant.EPOCH;
    if (epicDate.contains("Date")) {
      String trimmed = epicDate.replace("/Date(", "").replace(")/", "");
      String[] parts = trimmed.split("-");
      String milliseconds;
      // Offsets follow a dash, but we need to handle negatives, so the time and offset to shift.
      if (trimmed.startsWith("-")) {
        milliseconds = parts[1];
      } else {
        milliseconds = parts[0];
      }
      int offset = getOffset(epicDate);
      if (milliseconds.equals("")) {
        throw new RuntimeException("Epic date: " + trimmed);
      }
      long utc = Long.parseLong(milliseconds) + offset;
      return Instant.ofEpochMilli(utc);
    } else {
      DateTimeFormatter dtf = DateTimeFormatter.ISO_INSTANT;
      try {
        date = Instant.from(dtf.parse(epicDate));
      } catch (DateTimeParseException e) {
        date = Instant.EPOCH;
      }
    }

    return date;
  }

  private static int getOffset(String epicDate) {
    String trimmed = epicDate.replace("/Date(", "").replace(")/", "");
    String[] parts = trimmed.split("-");
    char offsetChar;
    // Offsets follow a dash, but we need to handle negatives, so the time and offset to shift.
    if (trimmed.startsWith("-")) {
      offsetChar = parts[2].toCharArray()[1];
    } else {
      offsetChar = parts[1].toCharArray()[1];
    }
    return Integer.parseInt(new String(new char[]{offsetChar})) * 60 * 60 * 1000;
  }

  public void setConfig(UcsfWebGetConfig config) {
    this.config = config;
  }

  @Override
  public void onTrigger(final ProcessContext context, final ProcessSessionFactory sessionFactory)
      throws ProcessException {
    final ProcessorLog logger = getLogger();

    if (!initialized) {
      String yamlFilename = context.getProperty(YAMLPATH).getValue();
      try {
        if (config == null) {
          config = UcsfWebGetConfig.load(yamlFilename);
        }

        if (config.username != null) {
          logger.info("Adding basic authentication with username " + config.username);
          credentials = new UsernamePasswordCredentials(config.username, config.password);
        } else {
          logger.info("No username provided. No authentication being sent.");
        }

        if (config.trustStore != null && !config.trustStore.isEmpty()) {
          logger.info("Using truststore: " + config.trustStore);
          System.setProperty("javax.net.ssl.trustStore", config.trustStore);
          System.setProperty("javax.net.ssl.trustStorePassword", config.trustStorePassword);
        } else {
          logger.info("Not using a truststore.");
        }
      } catch (FileNotFoundException ex) {
        throw new ProcessException("Configuration could not be loaded. File " + yamlFilename
            + " could not be found.", ex);
      } catch (UnsupportedEncodingException ex) {
        throw new ProcessException("Configuration could not be loaded. This system does not"
            + " support UTF-8", ex);
      }
      initialized = true;
    }

    final ProcessSession session = sessionFactory.createSession();
    SSLContext sslContext = null;

    // create the connection manager
    final HttpClientConnectionManager conMan;
    if (config.trustStore == null || config.trustStore.isEmpty()) {
      logger.info("No SSL context provided. Using basic HTTP.");
      conMan = new BasicHttpClientConnectionManager();
    } else {
      try {
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, null);
      } catch (final GeneralSecurityException e) {
        throw new ProcessException(e);
      }

      final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);

      final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
          .<ConnectionSocketFactory>create().register("https", sslsf).build();

      conMan = new BasicHttpClientConnectionManager(socketFactoryRegistry);
    }

    // build the request configuration
    final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
    requestConfigBuilder.setConnectionRequestTimeout(config.connectionTimeoutMilliseconds);
    requestConfigBuilder.setConnectTimeout(config.connectionTimeoutMilliseconds);
    requestConfigBuilder.setRedirectsEnabled(false);
    requestConfigBuilder.setSocketTimeout(config.dataTimeoutMilliseconds);
    requestConfigBuilder.setRedirectsEnabled(config.followRedirects);

    // build the http client
    final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    clientBuilder.setConnectionManager(conMan);

    // include the user agent
    if (config.userAgent != null) {
      clientBuilder.setUserAgent(config.userAgent);
    }

    // set the ssl context if necessary
    if (sslContext != null) {
      clientBuilder.setSslcontext(sslContext);
    }

    // create the http client
    final HttpClient client = clientBuilder.build();

    for (String url : config.urls) {
      final StopWatch stopWatch = new StopWatch(true);
      String baseUrl = url;
      String lastTimestamp = urlToFetchedTimeMap.get(url);

      if (lastTimestamp != null) {
        url = url + "&FromDate=" + lastTimestamp;
      }
      url = url.replace("^", "%5E");

      logger.info("Using URL: " + url);

      String source = url;
      try {
        source = new URI(url).getHost();
      } catch (final URISyntaxException ex) {
        throw new ProcessException("Given url: " + url + " is not valid.", ex);
      }

      // create request
      final HttpGet get = new HttpGet(url);
      get.setConfig(requestConfigBuilder.build());
      // Manually add basic authentication.
      if (credentials != null) {
        try {
          get.addHeader(new BasicScheme().authenticate(credentials, get, null));
        } catch (AuthenticationException e) {
          throw new AssertionError("BasicScheme.authenticate should never fail, but it did", e);
        }
      }

      if (config.acceptContentType != null) {
        get.addHeader(HEADER_ACCEPT, config.acceptContentType);
      }

      try {
        final HttpResponse response = client.execute(get);
        final int statusCode = response.getStatusLine().getStatusCode();
        final String statusExplanation = response.getStatusLine().getReasonPhrase();

        if (statusCode >= 300) {
          logger.error("received status code {}:{} from {}", new Object[]{statusCode,
              statusExplanation, url});
          // doing a commit in case there were flow files in the input queue
          session.commit();
          return;
        }

        String jsonString = IOUtils.toString(response.getEntity().getContent(), "UTF-8");

        try {
          JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonString);
          String extractDateTime = jsonObject.get("ExtractDateTime").toString();
          Instant correctTime;
          try {
            correctTime = Instant.parse(extractDateTime);
          } catch (DateTimeParseException e) {
            correctTime = epicDateToInstant(extractDateTime);
          }
          // The webservice is off by its UTC offset, we need to account for that.
          Instant incorrectTime
              = correctTime.minus(getOffset(extractDateTime), ChronoUnit.MILLIS);
          urlToFetchedTimeMap.put(baseUrl, incorrectTime.toString());
        } catch (ParseException e) {
          throw new ProcessException(e);
        }

        FlowFile flowFile = session.create();
        flowFile = session.write(flowFile, new OutputStreamCallback() {
          @Override
          public void process(OutputStream out) throws IOException {
            out.write(jsonString.getBytes());
          }
        });

        flowFile = session.putAttribute(flowFile, CoreAttributes.FILENAME.key(), config.filename
            + System.currentTimeMillis());
        flowFile = session.putAttribute(flowFile, this.getClass().getSimpleName().toLowerCase()
            + ".remote.source", source);
        final long flowFileSize = flowFile.getSize();
        stopWatch.stop();
        final String dataRate = stopWatch.calculateDataRate(flowFileSize);
        session.getProvenanceReporter().receive(flowFile, url,
            stopWatch.getDuration(TimeUnit.MILLISECONDS));
        session.transfer(flowFile, SUCCESS);
        logger.info("Successfully received {} from {} at a rate of {}; transferred to success",
            new Object[]{flowFile, url, dataRate});
        session.commit();
      } catch (final IOException e) {
        context.yield();
        session.rollback();
        logger.error("Failed to retrieve file from {} due to {}; rolling back session",
            new Object[]{url, e.getMessage()}, e);
        throw new ProcessException(e);
      }
    }
  }
}
