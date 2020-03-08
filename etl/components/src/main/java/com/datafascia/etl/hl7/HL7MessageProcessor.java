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
package com.datafascia.etl.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import java.util.function.Consumer;
import javax.inject.Inject;

/**
 * Updates application state in response to HL7 message.
 */
public class HL7MessageProcessor implements Consumer<String> {

  @Inject
  private Parser parser;

  @Inject
  private MessageRouter messageRouter;

  @Override
  public void accept(String hl7) {
    try {
      Message message = parser.parse(hl7);
      messageRouter.accept(message);
    } catch (HL7Exception e) {
      throw new IllegalStateException("Cannot parse HL7 " + hl7, e);
    }
  }
}
