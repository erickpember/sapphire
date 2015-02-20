// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import com.datafascia.domain.model.IngestMessage;
import com.datafascia.domain.persist.IngestMessageDao;
import javax.inject.Inject;
import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

/**
 * Saves message to archive.
 */
public class SaveMessage extends BaseFilter {
  private static final long serialVersionUID = 1L;

  public static final String ID = SaveMessage.class.getSimpleName();

  @Inject
  private transient IngestMessageDao ingestMessageDao;

  @Override
  public boolean isKeep(TridentTuple tuple) {
    IngestMessage message = (IngestMessage) tuple.getValueByField(F.INGEST_MESSAGE);
    ingestMessageDao.save(message);
    return true;
  }
}
