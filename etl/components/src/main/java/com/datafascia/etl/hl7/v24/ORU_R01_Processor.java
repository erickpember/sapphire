// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7.v24;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import ca.uhn.hl7v2.util.Terser;
import com.datafascia.etl.event.AddObservations;
import com.datafascia.etl.hl7.MessageProcessor;
import com.google.common.base.Strings;
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
  private static final String OBX_ROOT_PATH
      = "/PATIENT_RESULT/ORDER_OBSERVATION/OBSERVATION" + SUBSCRIPT_PLACEHOLDER + "/OBX";
  private static final String NTE_ROOT_PATH
      = "/PATIENT_RESULT/ORDER_OBSERVATION/OBSERVATION" + SUBSCRIPT_PLACEHOLDER + "/NTE";

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
      Terser terser = new Terser(input);

      // See if OBX exists. Message.getObx() and other Message methods don't work for some reason.
      if (!Strings.isNullOrEmpty(terser.get(OBX_ROOT_PATH.replace(SUBSCRIPT_PLACEHOLDER, "")
          + "-1"))) {
        List<Observation> observations = toObservations(terser, OBX_ROOT_PATH, NTE_ROOT_PATH);
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