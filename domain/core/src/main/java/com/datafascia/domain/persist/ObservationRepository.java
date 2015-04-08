// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.MutationBuilder;
import com.datafascia.common.accumulo.MutationSetter;
import com.datafascia.common.accumulo.RowMapper;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.model.CodeableConcept;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Observation;
import com.datafascia.domain.model.ObservationValue;
import com.datafascia.domain.model.Patient;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;

/**
 * Observation data access.
 * <p>
 * The row ID for an observation entity has the format:
 * <pre>
 * Patient={patientId}&Encounter={encounterId}&Observation={observationId}&
 * </pre>
 */
@Slf4j
public class ObservationRepository extends BaseRepository {

  private static final String COLUMN_FAMILY = Observation.class.getSimpleName();
  private static final String CODE = "code";
  private static final String VALUE_STRING = "valueString";
  private static final String ISSUED = "issued";

  private static final ObservationRowMapper OBSERVATION_ROW_MAPPER = new ObservationRowMapper();

  private static class ObservationRowMapper implements RowMapper<Observation> {
    private Observation observation;

    @Override
    public void onBeginRow(Key key) {
      observation = new Observation();
      observation.setId(Id.of(extractEntityId(key)));
    }

    @Override
    public void onReadEntry(Map.Entry<Key, Value> entry) {
      String value = entry.getValue().toString();
      switch (entry.getKey().getColumnQualifier().toString()) {
        case CODE:
          observation.setName(new CodeableConcept(value, value));
          break;
        case VALUE_STRING:
          ObservationValue observationValue = new ObservationValue();
          observationValue.setText(value);
          observation.setValues(observationValue);
          break;
        case ISSUED:
          observation.setIssued(Instant.parse(value));
          break;
      }
    }

    @Override
    public Observation onEndRow() {
      return observation;
    }
  }

  /**
   * Constructor
   *
   * @param accumuloTemplate
   *     data access operations template
   */
  @Inject
  public ObservationRepository(AccumuloTemplate accumuloTemplate) {
    super(accumuloTemplate);
  }

  private static String toRowId(
      Id<Patient> patientId, Id<Encounter> encounterId, Id<Observation> observationId) {

    return EncounterRepository.toRowId(patientId, encounterId) +
        toRowId(Observation.class, observationId);
  }

  private static Id<Observation> getEntityId(Observation observation) {
    return (observation.getId() != null)
        ? observation.getId()
        : Id.of(UUID.randomUUID().toString());
  }

  /**
   * Saves entity.
   *
   * @param patient
   *     parent entity
   * @param encounter
   *     parent entity
   * @param observation
   *     to save
   */
  public void save(Patient patient, Encounter encounter, Observation observation) {
    observation.setId(getEntityId(observation));

    accumuloTemplate.save(
        Tables.PATIENT,
        toRowId(patient.getId(), encounter.getId(), observation.getId()),
        new MutationSetter() {
          @Override
          public void putWriteOperations(MutationBuilder mutationBuilder) {
            mutationBuilder
                .columnFamily(COLUMN_FAMILY)
                .put(CODE, observation.getName().getCode())
                .put(VALUE_STRING, observation.getValues().getText())
                .put(ISSUED, observation.getIssued());
          }
        });
  }

  /**
   * Finds observations for an encounter.
   *
   * @param patientId
   *     parent entity ID
   * @param encounterId
   *     encounter ID
   * @return observations
   */
  public List<Observation> list(Id<Patient> patientId, Id<Encounter> encounterId) {
    Scanner scanner = accumuloTemplate.createScanner(Tables.PATIENT);
    scanner.setRange(Range.prefix(EncounterRepository.toRowId(patientId, encounterId)));
    scanner.fetchColumnFamily(new Text(COLUMN_FAMILY));

    return accumuloTemplate.queryForList(scanner, OBSERVATION_ROW_MAPPER);
  }
}
