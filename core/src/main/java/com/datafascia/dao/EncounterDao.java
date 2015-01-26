// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dao;

import com.datafascia.models.CodeableConcept;
import com.datafascia.models.Encounter;
import com.datafascia.models.Hospitalization;
import com.datafascia.models.Observation;
import com.datafascia.models.ObservationValue;
import com.datafascia.models.Period;
import com.datafascia.models.Quantity;
import com.datafascia.string.StringUtils;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;

/**
 * Encounter data access to/from Accumulo.
 */
@Slf4j
public class EncounterDao extends OpalDao {
  public static final String WEIGHT = "Weight";
  public static final String HEIGHT = "Height";
  private final SimpleDateFormat adminDateFormatter = new SimpleDateFormat("yyyyMMddHHmmssX");

  public EncounterDao(Connector connector) {
    super(connector);
  }

  /**
   * Returns a populated encounter object by its ID.
   *
   * @param id ID of the encounter to return.
   * @param auths Authorizations to filter on.
   * @return A populated Encounter object.
   */
  public Encounter getEncounter(String id, String auths) {
    Encounter encounter = new Encounter();
    Authorizations authorizations = new Authorizations(auths);

    Optional<Date> odate = getAdmissionDate(id, authorizations);
    if (odate.isPresent()) {
      Period period = new Period();
      period.setStart(odate.get());

      Hospitalization hospit = new Hospitalization();
      hospit.setPeriod(period);

      encounter.setHospitalisation(hospit);
    }

    List<Observation> observations = new ArrayList<Observation>();

    Optional<Observation> weight = getWeight(id, authorizations);
    if (weight.isPresent()) {
      observations.add(weight.get());
    }

    Optional<Observation> height = getHeight(id, authorizations);
    if (height.isPresent()) {
      observations.add(height.get());
    }

    encounter.setObservations(observations);

    return encounter;
  }

  /**
   * Returns the admission date of a given visit.
   *
   * @param id ID of the visit to fetch.
   * @param auths Authorizations to filter on.
   * @return A date for the admission.
   */
  public Optional<Date> getAdmissionDate(String id, Authorizations auths) {
    Optional<Value> valdate = getFieldValue(ObjectStore, PatientVisitMap, id, admitTime, auths);
    if (!valdate.isPresent()) {
      return Optional.empty();
    }

    Date date;
    try {
      date = adminDateFormatter.parse(StringUtils.trimQuote(valdate.get().toString()) + "-00");
    } catch (ParseException ex) {
      return Optional.empty();
    }

    return Optional.of(date);
  }

  /**
   * Returns the weight for a given visit.
   *
   * @param id ID of the visit to fetch.
   * @param auths Authorizations to filter on.
   * @return An optional observation for the weight.
   */
  public Optional<Observation> getWeight(String id, Authorizations auths) {
    Optional<Value> weightOpt = getFieldValue(ObjectStore, PatientVisitMap, id, admitWeight, auths);
    String[] weightl = new String[0];
    if (weightOpt.isPresent()) {
      Value weightVal = (Value) weightOpt.get();
      weightl = decodeString(weightVal).split(" ");
    }

    if (weightl.length != 2) {
      return Optional.empty();
    }

    Quantity qWeight = new Quantity();
    qWeight.setValue(new BigDecimal(weightl[0]));
    qWeight.setUnits(weightl[1]);
    ObservationValue ovWeight = new ObservationValue();
    ovWeight.setQuantity(qWeight);
    Observation oWeight = new Observation();
    oWeight.setValues(ovWeight);
    oWeight.setName(new CodeableConcept(){{ setCode(WEIGHT); }});

    return Optional.of(oWeight);
  }

  /**
   * Returns the height for a given visit.
   *
   * @param id ID of the visit to fetch.
   * @param auths Authorizations to filter on.
   * @return An optional observation for the height.
   */
  public Optional<Observation> getHeight(String id, Authorizations auths) {
    Optional<Value> heightOpt = getFieldValue(ObjectStore, PatientVisitMap, id, admitHeight, auths);
    String[] heightl = new String[0];
    if (heightOpt.isPresent()) {
      Value heightVal = (Value) heightOpt.get();
      heightl = decodeString(heightVal).split(" ");
    }

    if (heightl.length != 2) {
      return Optional.empty();
    }

    Quantity qHeight = new Quantity();
    qHeight.setValue(new BigDecimal(heightl[0]));
    qHeight.setUnits(heightl[1]);
    ObservationValue ovHeight = new ObservationValue();
    ovHeight.setQuantity(qHeight);
    Observation oHeight = new Observation();
    oHeight.setValues(ovHeight);
    oHeight.setName(new CodeableConcept(){{ setCode(HEIGHT); }});

    return Optional.of(oHeight);
  }
}
