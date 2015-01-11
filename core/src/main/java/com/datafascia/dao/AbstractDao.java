// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.security.Authorizations;

/**
 * Base class for all data access objects
 */
@Slf4j
public abstract class AbstractDao {
  /** Nul Separator */
  public static final String NUL = Character.toString('\0');
  /** The opal data table */
  public static final String OPAL_DF_DATA = "opal_dF_data";
  /** Visit map key prefix */
  public static final String VISIT_KEY_PREFIX = "ObjectStore" + NUL + "PatientVisitMap" + NUL;
  /** Patient object key prefix */
  public static final String PATIENT_OBJECT_KEY_PREFIX = "ObjectStore" + NUL + "PatientObject" + NUL;

  /** Patient present column family */
  public static final String PATIENT_PRESENT = "BOOLEAN" + NUL + "PatientPresent";
  /** Visit identifier column family */
  public static final String VISIT_ID = "STRING" + NUL + "LastVisitOiid";

  /* Authorization to table */
  protected static final Authorizations auths = new Authorizations("System");
}
