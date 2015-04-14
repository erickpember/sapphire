// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.MutationBuilder;
import com.datafascia.common.accumulo.MutationSetter;
import com.datafascia.common.accumulo.RowMapper;
import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Hospitalization;
import com.datafascia.domain.model.Patient;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;

/**
 * Encounter data access.
 * <p>
 * The row ID for an encounter entity has the format:
 * <pre>
 * Patient={patient key}&Encounter={encounterId}&
 * </pre>
 */
@Slf4j
public class EncounterRepository extends BaseRepository {

  private static final String COLUMN_FAMILY = Encounter.class.getSimpleName();
  private static final String IDENTIFIER = "identifier";
  private static final String ADMIT_TIME = "admitTime";
  private static final EncounterRowMapper ENCOUNTER_ROW_MAPPER = new EncounterRowMapper();

  /**
   * Constructor
   *
   * @param accumuloTemplate
   *     data access operations template
   */
  @Inject
  public EncounterRepository(AccumuloTemplate accumuloTemplate) {
    super(accumuloTemplate);
  }

  static String toRowId(Id<Patient> patientId, Id<Encounter> encounterId) {
    return toRowId(Patient.class, patientId) + toRowId(Encounter.class, encounterId);
  }

  /**
   * Generates primary key from institution encounter identifier.
   *
   * @param encounter
   *     encounter to read property from
   * @return primary key
   */
  public static Id<Encounter> getEntityId(Encounter encounter) {
    return Id.of(encounter.getIdentifier());
  }

  /**
   * Saves entity.
   *
   * @param patient
   *     parent entity
   * @param encounter
   *     to save
   */
  public void save(Patient patient, Encounter encounter) {
    encounter.setId(getEntityId(encounter));

    accumuloTemplate.save(
        Tables.PATIENT,
        toRowId(patient.getId(), encounter.getId()),
        new MutationSetter() {
          @Override
          public void putWriteOperations(MutationBuilder mutationBuilder) {
            mutationBuilder
                .columnFamily(COLUMN_FAMILY)
                .put(IDENTIFIER, encounter.getIdentifier())
                .put(ADMIT_TIME, encounter.getHospitalisation().getPeriod().getStartInclusive());
          }
        });
  }

  private static class EncounterRowMapper implements RowMapper<Encounter> {
    private Encounter encounter;

    @Override
    public void onBeginRow(Key key) {
      Hospitalization hospitalization = new Hospitalization();
      hospitalization.setPeriod(new Interval<>());

      encounter = new Encounter();
      encounter.setId(Id.of(extractEntityId(key)));
      encounter.setHospitalisation(hospitalization);
    }

    @Override
    public void onReadEntry(Map.Entry<Key, Value> entry) {
      String value = entry.getValue().toString();
      switch (entry.getKey().getColumnQualifier().toString()) {
        case IDENTIFIER:
          encounter.setIdentifier(value);
          break;
        case ADMIT_TIME:
          encounter.getHospitalisation().getPeriod().setStartInclusive(Instant.parse(value));
          break;
      }
    }

    @Override
    public Encounter onEndRow() {
      return encounter;
    }
  }

  /**
   * Reads encounter.
   *
   * @param patientId
   *     parent entity ID
   * @param encounterId
   *     encounter ID
   * @return optional entity, empty if not found
   */
  public Optional<Encounter> read(Id<Patient> patientId, Id<Encounter> encounterId) {
    Scanner scanner = accumuloTemplate.createScanner(Tables.PATIENT);
    scanner.setRange(Range.exact(toRowId(patientId, encounterId)));
    scanner.fetchColumnFamily(new Text(COLUMN_FAMILY));

    return accumuloTemplate.queryForObject(scanner, ENCOUNTER_ROW_MAPPER);
  }

  /**
   * Finds encounters for a patient.
   *
   * @param patientId
   *     parent entity ID
   * @return encounters
   */
  public List<Encounter> list(Id<Patient> patientId) {
    Scanner scanner = accumuloTemplate.createScanner(Tables.PATIENT);
    scanner.setRange(Range.prefix(PatientRepository.toRowId(patientId)));
    scanner.fetchColumnFamily(new Text(COLUMN_FAMILY));

    return accumuloTemplate.queryForList(scanner, ENCOUNTER_ROW_MAPPER);
  }
}
