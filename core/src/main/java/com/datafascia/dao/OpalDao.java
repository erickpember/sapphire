// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dao;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;

/**
 * Handles interacting with Accumulo instances that are formatted in the style of Opal.
 */
@Slf4j
public class OpalDao {
  protected static final String ObjectStore = "ObjectStore";
  protected static final String PatientVisitMap = "PatientVisitMap";
  protected static final String admitWeight = "df_obxWeight";
  protected static final String admitHeight = "df_obxHeight";
  protected static final String admitTime = "dF_pv1AdmitDateTime";
  protected static final String LastVisitOiid = "LastVisitOiid";
  protected static final String SEPERATOR = "\0";
  protected static final String PatientObject = "PatientObject";
  protected static final String OPAL_DATA = "opal_dF_data";

  private final Connector connector;

  public OpalDao(Connector connector) {
    this.connector = connector;
  }

  /**
   * Returns a usable scanner of the opal_dF_data table.
   * @param auths The authorizations to filter on.
   * @return A scanner for the opal_dF_data table.
   * @throws TableNotFoundException
   */
  public Scanner getScanner(Authorizations auths) throws TableNotFoundException {
    return connector.createScanner(OPAL_DATA, auths);
  }

  private String[] splitKey(String key) {
    return key.split(SEPERATOR);
  }

  /**
   * Returns a key/value pairing of fields for a given Opal object.
   *
   * @param namespace The Opal namespace (e.g. ObjectStore)
   * @param kind The Opal kind (e.g. PatientVisitMap)
   * @param id The id of the Opal object.
   * @param auths The auths to connect with.
   * @return An optional map pairing Opal fields to values.
   * @throws org.apache.accumulo.core.client.TableNotFoundException
   */
  protected Optional<Map<String, Value>> getObjectFields(String namespace, String kind, String id,
      Authorizations auths) throws TableNotFoundException {
    HashMap<String, Value> map = new HashMap<String, Value>();

    Scanner scanner = getScanner(auths);
    scanner.setRange(Range.exact(namespace + SEPERATOR + kind + SEPERATOR + id));
    Iterator<Entry<Key, Value>> iter = scanner.iterator();
    if (!iter.hasNext()) {
      return Optional.empty();
    }

    while (iter.hasNext()) {
      Entry<Key, Value> e = iter.next();
      Value value = e.getValue();
      String[] colfStr = splitKey(e.getKey().getColumnFamily().toString());
      map.put(colfStr[0], value);
    }

    return Optional.of(map);
  }

  /**
   * Returns the value of a given field in an Opal Object.
   *
   * @param namespace The Opal namespace (e.g. ObjectStore)
   * @param kind The Opal kind (e.g. PatientVisitMap)
   * @param id The id of the Opal object.
   * @param requestedFieldName The name of the field to pull.
   * @param auths The auths to connect with.
   * @return A given field value for an Opal object.
   * @throws TableNotFoundException
   */
  protected Optional<Value> getFieldValue(String namespace, String kind, String id,
      String requestedFieldName, Authorizations auths) throws TableNotFoundException {
    Scanner scanner = getScanner(auths);
    scanner.setRange(Range.exact(namespace + SEPERATOR + kind + SEPERATOR + id));
    Iterator<Entry<Key, Value>> iter = scanner.iterator();

    while (iter.hasNext()) {
      Entry<Key, Value> e = iter.next();

      // The column family contains the field name and the field type.
      String[] colfStr = splitKey(e.getKey().getColumnFamily().toString());
      String fieldType = colfStr[0];
      String fieldName = colfStr[1];

      if (fieldName.equals(requestedFieldName)) {
        return Optional.of(e.getValue());
      }
    }

    return Optional.empty();
  }
}
