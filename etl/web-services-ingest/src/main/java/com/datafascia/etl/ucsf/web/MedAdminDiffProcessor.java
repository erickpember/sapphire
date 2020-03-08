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
package com.datafascia.etl.ucsf.web;

import com.datafascia.common.nifi.DependencyInjectingProcessor;
import com.google.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ProcessorLog;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Processor;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.InputStreamCallback;
import org.kohsuke.MetaInfServices;

/**
 * Processor for detecting and handling diffs in UCSF medication administration.
 */
@MetaInfServices(Processor.class)
@Slf4j
public class MedAdminDiffProcessor extends DependencyInjectingProcessor {
  private Set<Relationship> relationships;
  private List<PropertyDescriptor> properties;
  private MedAdminDiffListener diffListener;

  @Inject
  private MedAdminDiffTransformer medAdminDiffTransformer;

  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("SUCCESS")
      .description("Success relationship")
      .build();
  public static final Relationship FAILURE = new Relationship.Builder()
      .name("FAILURE")
      .description("Failure relationship")
      .build();

  /**
   * Sets the listener to be called when a diff is found.
   *
   * @param newListener The listener to be called when a diff is found.
   */
  public void setDiffListener(MedAdminDiffListener newListener) {
    diffListener = newListener;
  }

  @Override
  public Set<Relationship> getRelationships() {
    return relationships;
  }

  @Override
  public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return properties;
  }

  @Override
  public void init(final ProcessorInitializationContext context) {
    List<PropertyDescriptor> initProperties = new ArrayList<>();
    this.properties = Collections.unmodifiableList(initProperties);
    Set<Relationship> initRelationships = new HashSet<>();
    initRelationships.add(SUCCESS);
    initRelationships.add(FAILURE);
    this.relationships = Collections.unmodifiableSet(initRelationships);
  }

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    final ProcessorLog plog = this.getLogger();
    final FlowFile flowfile = session.get();
    medAdminDiffTransformer.setDiffListener(diffListener);

    session.read(flowfile, new InputStreamCallback() {

      @Override
      public void process(InputStream in) throws IOException {
        try {
          String jsonString = IOUtils.toString(in);
          medAdminDiffTransformer.accept(jsonString);
        } catch (Exception e) {
          // Log exceptions, drop flowfile on the floor.
          log.error("Error handling flowfile.", e);
          plog.error("Error handling flowfile: " + e.getMessage());
        }
      }
    });
    session.transfer(flowfile, SUCCESS);
  }
}
