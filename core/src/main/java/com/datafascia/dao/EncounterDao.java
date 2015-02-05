// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dao;

import com.datafascia.accumulo.QueryTemplate;
import com.datafascia.models.CodeableConcept;
import com.datafascia.models.Encounter;
import com.datafascia.models.Hospitalization;
import com.datafascia.models.Observation;
import com.datafascia.models.ObservationValue;
import com.datafascia.models.Period;
import com.datafascia.models.Quantity;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.data.Value;

/**
 * Encounter data access to/from Accumulo.
 */
@Singleton @Slf4j
public class EncounterDao extends OpalDao {
  public static final String WEIGHT = "Weight";
  public static final String HEIGHT = "Height";
  private static final String ADMIN_DATE_FORMAT = "yyyyMMddHHmmssX";

  @Inject
  public EncounterDao(QueryTemplate queryTemplate) {
    super(queryTemplate);
  }

  /**
   * Returns a populated encounter object by its ID.
   *
   * @param id ID of the encounter to return.
   * @return A populated Encounter object.
   */
  public Encounter getEncounter(String id) {
    Encounter encounter = new Encounter();

    Optional<Date> odate = getAdmissionDate(id);
    if (odate.isPresent()) {
      Period period = new Period();
      period.setStart(odate.get());

      Hospitalization hospit = new Hospitalization();
      hospit.setPeriod(period);

      encounter.setHospitalisation(hospit);
    }

    List<Observation> observations = new ArrayList<Observation>();

    Optional<Observation> weight = getWeight(id);
    if (weight.isPresent()) {
      observations.add(weight.get());
    }

    Optional<Observation> height = getHeight(id);
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
   * @return A date for the admission.
   */
  public Optional<Date> getAdmissionDate(String id) {
    Optional<Value> valdate = getFieldValue(ObjectStore, PatientVisitMap, id, admitTime);
    if (!valdate.isPresent()) {
      return Optional.empty();
    }

    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat(ADMIN_DATE_FORMAT);
      Date date = dateFormat.parse(decodeString(valdate.get()) + "-00");
      return Optional.of(date);
    } catch (ParseException ex) {
      return Optional.empty();
    }
  }

  /**
   * Returns the weight for a given visit.
   *
   * @param id ID of the visit to fetch.
   * @return An optional observation for the weight.
   */
  public Optional<Observation> getWeight(String id) {
    Optional<Value> weightOpt = getFieldValue(ObjectStore, PatientVisitMap, id, admitWeight);
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
   * @return An optional observation for the height.
   */
  public Optional<Observation> getHeight(String id) {
    Optional<Value> heightOpt = getFieldValue(ObjectStore, PatientVisitMap, id, admitHeight);
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

  /**
   * Finds update identifiers for the visit.
   *
   * @param visitId
   *     visit identifier
   * @return collection of update identifiers, empty if none found
   */
  public List<String> findUpdateIds(String visitId) {
    Optional<Value> value = getFieldValue(
        ObjectStore, Kinds.PATIENT_VISIT_MAP, visitId, VisitFields.UPDATE_OIIDS);
    if (value.isPresent()) {
      return Arrays.asList(decodeStringArray(value.get()));
    } else {
      return Collections.emptyList();
    }
  }
}
