// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
// Forked from GetHTTP which is available under the Apache 2.0 license.
package com.datafascia.etl.ucsf.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.net.ssl.SSLContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
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
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.OutputStreamCallback;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.ssl.SSLContextService;
import org.apache.nifi.util.StopWatch;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * A fork of GetHTTP that intercepts the response, pulls out the data extract date, and feeds it
 * back for the next fetch.
 */
@Tags({"get", "fetch", "poll", "http", "https", "ingest", "source", "input", "datafascia", "ucsf",
    "json"})
@CapabilityDescription("Fetches a JSON file via HTTP")
@WritesAttribute(attribute = "filename",
    description = "the filename is set to the name of the file on the remote server")
@Slf4j
public class UcsfWebGetProcessor extends AbstractSessionFactoryProcessor {
  public static final String HEADER_ACCEPT = "Accept";

  public static final PropertyDescriptor URL = new PropertyDescriptor.Builder()
      .name("Base URL")
      .description("The base URL to pull from")
      .required(true)
      .addValidator(StandardValidators.URL_VALIDATOR)
      .addValidator(
          StandardValidators.createRegexMatchingValidator(Pattern.compile("https?\\://.*")))
      .build();
  public static final PropertyDescriptor FOLLOW_REDIRECTS = new PropertyDescriptor.Builder()
      .name("Follow Redirects")
      .description("If we receive a 3xx HTTP Status Code from the server, indicates whether or " +
          "not we should follow the redirect that the server specifies")
      .defaultValue("false")
      .allowableValues("true", "false")
      .build();
  public static final PropertyDescriptor CONNECTION_TIMEOUT = new PropertyDescriptor.Builder()
      .name("Connection Timeout")
      .description("How long to wait when attempting to connect to the remote server before " +
          "giving up")
      .required(true)
      .defaultValue("30 sec")
      .addValidator(StandardValidators.TIME_PERIOD_VALIDATOR)
      .build();
  public static final PropertyDescriptor ACCEPT_CONTENT_TYPE = new PropertyDescriptor.Builder()
      .name("Accept Content-Type")
      .description("If specified, requests will only accept the provided Content-Type")
      .required(false)
      .defaultValue("application/json")
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .build();
  public static final PropertyDescriptor DATA_TIMEOUT = new PropertyDescriptor.Builder()
      .name("Data Timeout")
      .description("How long to wait between receiving segments of data from the remote server " +
          "before giving up and discarding the partial file")
      .required(true)
      .defaultValue("30 sec")
      .addValidator(StandardValidators.TIME_PERIOD_VALIDATOR)
      .build();
  public static final PropertyDescriptor FILENAME = new PropertyDescriptor.Builder()
      .name("Filename")
      .description("The filename to assign to the file when pulled")
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .required(true)
      .build();
  public static final PropertyDescriptor USERNAME = new PropertyDescriptor.Builder()
      .name("Username")
      .description("Username required to access the URL")
      .required(false)
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .build();
  public static final PropertyDescriptor PASSWORD = new PropertyDescriptor.Builder()
      .name("Password")
      .description("Password required to access the URL")
      .required(false)
      .sensitive(true)
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .build();
  public static final PropertyDescriptor USER_AGENT = new PropertyDescriptor.Builder()
      .name("User Agent")
      .description("What to report as the User Agent when we connect to the remote server")
      .required(false)
      .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
      .build();
  public static final PropertyDescriptor SSL_CONTEXT_SERVICE = new PropertyDescriptor.Builder()
      .name("SSL Context Service")
      .description("The Controller Service to use in order to obtain an SSL Context")
      .required(false)
      .identifiesControllerService(SSLContextService.class)
      .build();

  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("success")
      .description("All files are transferred to the success relationship")
      .build();

  private Set<Relationship> relationships;
  private List<PropertyDescriptor> properties;

  private static String lastTimestamp;

  @Override
  protected void init(final ProcessorInitializationContext context) {
    final Set<Relationship> relationships = new HashSet<>();
    relationships.add(SUCCESS);
    this.relationships = Collections.unmodifiableSet(relationships);

    final List<PropertyDescriptor> properties = new ArrayList<>();
    properties.add(URL);
    properties.add(FILENAME);
    properties.add(SSL_CONTEXT_SERVICE);
    properties.add(USERNAME);
    properties.add(PASSWORD);
    properties.add(CONNECTION_TIMEOUT);
    properties.add(DATA_TIMEOUT);
    properties.add(USER_AGENT);
    properties.add(ACCEPT_CONTENT_TYPE);
    properties.add(FOLLOW_REDIRECTS);
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

  private SSLContext createSSLContext(final SSLContextService service)
      throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException,
      KeyManagementException, UnrecoverableKeyException {
    final KeyStore truststore = KeyStore.getInstance(service.getTrustStoreType());
    try (final InputStream in = new FileInputStream(new File(service.getTrustStoreFile()))) {
      truststore.load(in, service.getTrustStorePassword().toCharArray());
    }

    final KeyStore keystore = KeyStore.getInstance(service.getKeyStoreType());
    try (final InputStream in = new FileInputStream(new File(service.getKeyStoreFile()))) {
      keystore.load(in, service.getKeyStorePassword().toCharArray());
    }

    final SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(truststore,
        new TrustSelfSignedStrategy()).loadKeyMaterial(keystore,
        service.getKeyStorePassword().toCharArray()).build();

    return sslContext;
  }

  /**
   * Translates the date format provided by the UCSF web services ("/Date(1234567890)/") to
   * ISO8601 UTC.
   * @param epicDate The date in EPIC's odd format.
   * @return The formatted string.
   */
  public static String epicDateToISO8601(String epicDate) {
    Instant date = epicDateToInstant(epicDate);
    return date.toString();
  }

  /**
   * Translates the date format provided by the UCSF web services ("/Date(1234567890)/") to
   * Java Instants
   * @param epicDate The date in EPIC's odd format.
   * @return An instant.
   */
  public static Instant epicDateToInstant(String epicDate) {
    String trimmed = epicDate.replace("/Date(", "").replace(")/", "");
    String[] parts = trimmed.split("-");
    String milliseconds;
    char offsetChar;
    // Offsets follow a dash, but we need to handle negatives, so the time and offset to shift.
    if (trimmed.startsWith("-")) {
      milliseconds = parts[1];
      offsetChar = parts[2].toCharArray()[1];
    } else {
      milliseconds = parts[0];
      offsetChar = parts[1].toCharArray()[1];
    }
    int offset = Integer.parseInt(new String(new char[]{offsetChar})) * 60 * 60 * 1000;
    if (milliseconds.equals("")) {
      throw new RuntimeException("Epic date: " + trimmed);
    }
    long utc = Long.parseLong(milliseconds) - offset;
    return Instant.ofEpochMilli(utc);
  }

  @Override
  public void onTrigger(final ProcessContext context, final ProcessSessionFactory sessionFactory)
      throws ProcessException {
    final ProcessorLog logger = getLogger();

    final ProcessSession session = sessionFactory.createSession();

    // get the URL
    String url;
    if (lastTimestamp != null) {
      url = context.getProperty(URL).getValue() + "&FromDate=" + lastTimestamp;
    } else {
      url = context.getProperty(URL).getValue();
    }
    url = url.replace("^", "%5E");

    log.info("Using URL: " + url);

    String source = url;
    try {
      source = new URI(url).getHost();
    } catch (final URISyntaxException swallow) {
      // this won't happen as the url has already been validated
    }

    // get the ssl context service
    final SSLContextService sslContextService = context.getProperty(SSL_CONTEXT_SERVICE)
        .asControllerService(SSLContextService.class);

    // create the connection manager
    final HttpClientConnectionManager conMan;
    if (sslContextService == null) {
      conMan = new BasicHttpClientConnectionManager();
    } else {
      final SSLContext sslContext;
      try {
        sslContext = createSSLContext(sslContextService);
      } catch (final GeneralSecurityException | IOException e) {
        throw new ProcessException(e);
      }

      final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
          new String[]{"TLSv1"}, null,
          SSLConnectionSocketFactory.getDefaultHostnameVerifier());

      final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
          .<ConnectionSocketFactory>create().register("https", sslsf).build();

      conMan = new BasicHttpClientConnectionManager(socketFactoryRegistry);
    }

    try {
      // build the request configuration
      final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
      requestConfigBuilder.setConnectionRequestTimeout(context.getProperty(DATA_TIMEOUT)
          .asTimePeriod(TimeUnit.MILLISECONDS).intValue());
      requestConfigBuilder.setConnectTimeout(context.getProperty(CONNECTION_TIMEOUT)
          .asTimePeriod(TimeUnit.MILLISECONDS).intValue());
      requestConfigBuilder.setRedirectsEnabled(false);
      requestConfigBuilder.setSocketTimeout(context.getProperty(DATA_TIMEOUT)
          .asTimePeriod(TimeUnit.MILLISECONDS).intValue());
      requestConfigBuilder.setRedirectsEnabled(context.getProperty(FOLLOW_REDIRECTS).asBoolean());

      // build the http client
      final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
      clientBuilder.setConnectionManager(conMan);

      // include the user agent
      final String userAgent = context.getProperty(USER_AGENT).getValue();
      if (userAgent != null) {
        clientBuilder.setUserAgent(userAgent);
      }

      // set the ssl context if necessary
      if (sslContextService != null) {
        clientBuilder.setSslcontext(sslContextService
            .createSSLContext(SSLContextService.ClientAuth.REQUIRED));
      }

      final String username = context.getProperty(USERNAME).getValue();
      final String password = context.getProperty(PASSWORD).getValue();

      // set the credentials if appropriate
      if (username != null) {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        if (password == null) {
          credentialsProvider.setCredentials(AuthScope.ANY,
              new UsernamePasswordCredentials(username));
        } else {
          credentialsProvider.setCredentials(AuthScope.ANY,
              new UsernamePasswordCredentials(username, password));
        }
        clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
      }

      // create the http client
      final HttpClient client = clientBuilder.build();

      // create request
      final HttpGet get = new HttpGet(url);
      get.setConfig(requestConfigBuilder.build());

      final String accept = context.getProperty(ACCEPT_CONTENT_TYPE).getValue();
      if (accept != null) {
        get.addHeader(HEADER_ACCEPT, accept);
      }

      try {
        final StopWatch stopWatch = new StopWatch(true);
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
          lastTimestamp = epicDateToISO8601(extractDateTime);
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

        flowFile = session.putAttribute(flowFile, CoreAttributes.FILENAME.key(),
            context.getProperty(FILENAME).getValue());
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

    } finally {
      conMan.shutdown();
    }
  }
}
