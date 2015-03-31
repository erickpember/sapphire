// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An event containing a list of observations.
 */
@AllArgsConstructor @Builder @Data @NoArgsConstructor
public class ObservationListData implements EventData {

  private List<ObservationData> observations;
}
