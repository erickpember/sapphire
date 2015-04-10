// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data included when admitting patient.
 */
@AllArgsConstructor @Builder @Data @NoArgsConstructor
public class AdmitPatientData implements EventData {

  private PatientData patient;
  private EncounterData encounter;
}
