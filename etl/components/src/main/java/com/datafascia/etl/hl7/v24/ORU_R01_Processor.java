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
package com.datafascia.etl.hl7.v24;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import com.datafascia.etl.event.AddObservations;
import com.datafascia.etl.hl7.MessageProcessor;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.MetaInfServices;

/**
 * Extracts OBX data from an ORU message.
 * Further parsing of elements outside the OBX is not yet done.
 */
@MetaInfServices(MessageProcessor.class)
@Slf4j
public class ORU_R01_Processor extends BaseProcessor {

  // HAPI paths to retrieve the segments. Differs between some message types.
  private static final String OBX_PATH_PATTERN
      = "/PATIENT_RESULT/ORDER_OBSERVATION(%1$d)/OBSERVATION(%2$d)/OBX";
  private static final String NTE_PATH_PATTERN
      = "/PATIENT_RESULT/ORDER_OBSERVATION(%1$d)/OBSERVATION(%2$d)/NTE(%3$d)";

  @Inject
  private AddObservations addObservations;

  @Override
  public Class<? extends Message> getAcceptableMessageType() {
    return ORU_R01.class;
  }

  @Override
  public void accept(Message input) {
    ORU_R01 message = (ORU_R01) input;

    try {
      ObservationsBuilder observationsBuilder =
          new ObservationsBuilder(message, OBX_PATH_PATTERN, NTE_PATH_PATTERN);
      if (observationsBuilder.hasObservations()) {
        List<Observation> observations = observationsBuilder.toObservations();
        addObservations.accept(
            observations,
            getPatientIdentifier(message.getPATIENT_RESULT().getPATIENT().getPID()),
            getEncounterIdentifier(message.getPATIENT_RESULT().getPATIENT().getVISIT().getPV1()));
      }
    } catch (HL7Exception e) {
      log.error("Failed to process message {}", message);
      throw new IllegalStateException("Failed to process message", e);
    }
  }
}
