// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import org.apache.avro.reflect.Union;

/**
 * Marker interface for data included with event.
 */
@Union({ AdmitPatientData.class, ObservationListData.class })
public interface EventData {
}
