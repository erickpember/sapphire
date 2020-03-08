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

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ADT_A09;
import com.datafascia.etl.hl7.MessageProcessor;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.MetaInfServices;

/**
 * Processes discharge patient message.
 */
@MetaInfServices(MessageProcessor.class)
@Slf4j
public class ADT_A09_Processor extends AdmitDischargeProcessor {

  @Override
  public Class<? extends Message> getAcceptableMessageType() {
    return ADT_A09.class;
  }

  @Override
  public void accept(Message input) {
    ADT_A09 message = (ADT_A09) input;

    try {
      String triggerEvent = message.getMSH().getMessageType().getTriggerEvent().getValue();
      if (triggerEvent.equals("A12")) {
        admitPatient(
            message,
            message.getMSH(),
            message.getPID(),
            message.getPV1(),
            Collections.emptyList(),
            Collections.emptyList());
      } else {
        dischargePatient(
            message,
            message.getMSH(),
            message.getPID(),
            message.getPV1(),
            Collections.emptyList(),
            Collections.emptyList());
      }
    } catch (HL7Exception e) {
      log.error("Failed to process message {}", message);
      throw new IllegalStateException("Failed to process message", e);
    }
  }
}
