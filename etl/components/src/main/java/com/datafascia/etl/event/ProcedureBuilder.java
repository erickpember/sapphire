// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.api.ResourceMetadataKeyEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.Dates;
import com.datafascia.emerge.ucsf.codes.ProcedureCategoryEnum;
import com.google.common.base.Strings;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import lombok.extern.slf4j.Slf4j;

/**
 * Creates procedure from observations.
 * <p>
 * Represents a central line insertion in these procedure properties:
 * <dl>
 *   <dt>identifier(0).value
 *   <dd>identifier in case multiple lines of the same type are inserted
 *   <dt>code.coding(0).code
 *   <dd>line type
 *   <dt>bodySite(0).coding(0).code
 *   <dd>body site
 *   <dt>bodySite(1).coding(0).code
 *   <dd>body orientation
 *   <dt>performedDateTime
 *   <dd>when line placement was performed
 *   <dt>meta.lastUpdated
 *   <dd>when content of this resource changed
 *   <dt>category.coding(0).code
 *   <dd>CENTRAL_LINE
 *   <dt>status
 *   <dd>indicates line is active
 * </dl>
 */
@Slf4j
public class ProcedureBuilder {

  private static final String LINE_TYPE0 = "304890080";
  private static final String LINE_TYPE9 = "304890099";
  private static final String LINE_PLACEMENT_DATE = "304890002";
  private static final String LINE_PLACEMENT_TIME = "304890078";
  private static final String LINE_ORIENTATION1 = "304890081";
  private static final String LINE_ORIENTATION2 = "304890092";
  private static final String LINE_ORIENTATION3 = "304890103";
  private static final String LINE_LOCATION2 = "304890082";
  private static final String LINE_LOCATION7 = "304890097";
  private static final String LINE_LOCATION0 = "304890100";
  private static final String LINE_LOCATION4 = "304890104";
  private static final String LINE_ACCESS_TYPE = "304890091";
  private static final String LINE_ACCESS_LOCATION = "304890093";
  private static final String LINE_LUMENS = "304890094";
  private static final String LINE_REMOVAL_DATE = "304890084";
  private static final String LINE_REMOVAL_TIME = "304890085";
  private static final String LINE_REMOVAL_REASON86 = "304890086";
  private static final String LINE_REMOVAL_REASON96 = "304890096";
  private static final String LINE_REMOVAL_REASON28 = "304894228";

  private static final String REMOVED = "[REMOVED]";
  private static final DateTimeFormatter LOCAL_TIME = DateTimeFormatter.ofPattern("HHmmss");

  private static final String LEFT = "Left";
  private static final String LOWER = "Lower";
  private static final String MEDIAL = "Medial";
  private static final String RIGHT = "Right";
  private static final String OTHER_COMMENT = "Other (Comment)";
  private static final String N_A = "N/A";
  private static final String OTHER = "Other";
  private static final String UNKNOWN = "Unknown";

  private static final String ANKLE = "Ankle";
  private static final String ARM = "Arm";
  private static final String CHEST = "Chest";
  private static final String FEMORAL_VEIN = "Femoral vein";
  private static final String FEMORAL = "Femoral";
  private static final String INTERNAL_JUGULAR = "Internal jugular";
  private static final String NECK = "Neck";
  private static final String SUBCLAVIAN = "Subclavian";

  private static final String NON_TUNNELED_CATHETER = "Non-tunneled Catheter";
  private static final String NON_TUNNELED = "Non-tunneled";
  private static final String TUNNELED_CATHETER = "Tunneled Catheter";
  private static final String TUNNELED = "Tunneled";

  private final Encounter encounter;
  private final Clock clock;
  private final Map<String, Observation> codeToObservationMap = new HashMap<>();
  private boolean removed;

  /**
   * Constructor
   *
   * @param encounter
   *    encounter while procedure was performed
   * @param clock
   *    to read current time from
   */
  public ProcedureBuilder(Encounter encounter, Clock clock) {
    this.encounter = encounter;
    this.clock = clock;
  }

  /**
   * Adds observation.
   *
   * @param observation
   *     to add
   */
  public void add(Observation observation) {
    String code = observation.getCode().getCodingFirstRep().getCode();
    codeToObservationMap.put(code, observation);
  }

  private Optional<Observation> getObservation(String... codes) {
    for (String code : codes) {
      Observation observation = codeToObservationMap.get(code);
      if (observation != null) {
        return Optional.of(observation);
      }
    }

    return Optional.empty();
  }

  private Optional<String> getValue(String code) {
    return getObservation(code)
        .map(observation -> ((StringDt) observation.getValue()).getValue());
  }

  private static String normalizeSpace(String input) {
    return input.replaceAll("\\s+", " ");
  }

  private Optional<LineType> extractBasicLineType(String inputLineType) {
    String inputText = normalizeSpace(inputLineType);

    int inputCodeStart = inputText.indexOf('-');
    if (inputCodeStart < 0) {
      throw new IllegalStateException(inputText + " does not contain -");
    }

    String inputCode = inputText.substring(inputCodeStart + 1);
    removed = inputCode.startsWith(REMOVED);
    if (removed) {
      inputCode = inputCode.substring(REMOVED.length()).trim();
    }

    return LineType.of(inputCode);
  }

  private String extractLineType(LineType basicLineType) {
    if (basicLineType == LineType.HEMODIALYSIS_PHERESIS_CATHETER) {
      String lumens = getValue(LINE_LUMENS).orElse("");
      switch (lumens) {
        case "2":
          return "Double Lumen " + LineType.HEMODIALYSIS_PHERESIS_CATHETER.getCode();
        case "3":
          return "Triple Lumen " + LineType.HEMODIALYSIS_PHERESIS_CATHETER.getCode();
        default:
          return LineType.HEMODIALYSIS_PHERESIS_CATHETER.getCode();
      }
    }

    return basicLineType.getCode();
  }

  private String extractLineOrientation(LineType basicLineType) {
    switch (basicLineType) {
      case HEMODIALYSIS_PHERESIS_CATHETER:
        String orientation2 = getValue(LINE_ORIENTATION2).orElse("");
        if (orientation2.contains(LEFT)) {
          return LEFT;
        } else if (orientation2.contains(RIGHT)) {
          return RIGHT;
        }
        break;

      case IMPLANTED_PORT_SINGLE_LUMEN:
      case IMPLANTED_PORT_DOUBLE_LUMEN:
        String orientation3 = getValue(LINE_ORIENTATION3).orElse("");
        if (orientation3.contains(LEFT)) {
          return LEFT;
        } else if (orientation3.contains(RIGHT)) {
          return RIGHT;
        } else if (orientation3.contains(MEDIAL) || orientation3.contains(OTHER_COMMENT)) {
          return N_A;
        }
        break;

      default:
        String orientation1 = getValue(LINE_ORIENTATION1).orElse("");
        if (orientation1.contains(LEFT)) {
          return LEFT;
        } else if (orientation1.contains(RIGHT)) {
          return RIGHT;
        } else if ((basicLineType == LineType.PICC_SINGLE_LUMEN
            || basicLineType == LineType.PICC_DOUBLE_LUMEN
            || basicLineType == LineType.PICC_TRIPLE_LUMEN)
            && orientation1.contains(LOWER)) {
          return N_A;
        } else if (orientation1.contains(OTHER_COMMENT)) {
          return N_A;
        }
    }

    return UNKNOWN;
  }

  private String extractLineBodySite(LineType basicLineType) {
    String location0 = getValue(LINE_LOCATION0).orElse("");

    switch (basicLineType) {
      case HEMODIALYSIS_PHERESIS_CATHETER:
        String accessLocation = getValue(LINE_ACCESS_LOCATION).orElse("");
        if (accessLocation.contains(ANKLE)
            || accessLocation.contains("Forearm")
            || accessLocation.contains("Thigh")
            || accessLocation.contains("Wrist")
            || accessLocation.contains(OTHER_COMMENT)) {
          return OTHER;
        } else if (accessLocation.contains(ARM)) {
          return ARM;
        } else if (accessLocation.contains(CHEST)) {
          return CHEST;
        } else if (accessLocation.contains(FEMORAL_VEIN)) {
          return FEMORAL;
        } else if (accessLocation.contains(NECK)) {
          return INTERNAL_JUGULAR;
        } else if (accessLocation.contains(SUBCLAVIAN)) {
          return SUBCLAVIAN;
        }
        break;

      case IMPLANTED_PORT_SINGLE_LUMEN:
      case IMPLANTED_PORT_DOUBLE_LUMEN:
        String location4 = getValue(LINE_LOCATION4).orElse("");
        if (location4.contains(ARM)) {
          return ARM;
        } else if (location4.contains(CHEST)) {
          return CHEST;
        } else if (location4.contains(OTHER_COMMENT)) {
          return OTHER;
        }
        break;

      case PICC_SINGLE_LUMEN:
      case PICC_DOUBLE_LUMEN:
      case PICC_TRIPLE_LUMEN:
        if (location0.contains(ARM)) {
          return ARM;
        } else if (location0.contains(CHEST)) {
          return CHEST;
        } else if (location0.contains(ANKLE)
            || location0.contains("Antecubital")
            || location0.contains("Axilla")
            || location0.contains("Leg")
            || location0.contains("Scalp")
            || location0.contains(OTHER_COMMENT)) {
          return OTHER;
        }
        break;

      case PULMONARY_ARTERY_CATHETER:
        String location7 = getValue(LINE_LOCATION7).orElse("");
        if (location7.contains(FEMORAL)) {
          return FEMORAL;
        } else if (location7.contains(INTERNAL_JUGULAR)) {
          return INTERNAL_JUGULAR;
        } else if (location7.contains(SUBCLAVIAN)) {
          return SUBCLAVIAN;
        } else if (location7.contains(OTHER_COMMENT)) {
          return OTHER;
        }
        break;

      default:
        String location2 = getValue(LINE_LOCATION2).orElse("");
        if (location2.contains(FEMORAL)) {
          return FEMORAL;
        } else if (location2.contains(INTERNAL_JUGULAR)) {
          return INTERNAL_JUGULAR;
        } else if (location2.contains(SUBCLAVIAN)) {
          return SUBCLAVIAN;
        } else if (location2.contains(OTHER_COMMENT)) {
          return OTHER;
        }
    }

    return UNKNOWN;
  }

  private String extractLineTunneledStatus(LineType basicLineType) {
    switch (basicLineType) {
      case CVC_SINGLE_LUMEN:
      case CVC_DOUBLE_LUMEN:
      case CVC_TRIPLE_LUMEN:
      case CVC_QUADRUPLE_LUMEN:
        String lineType0 = getValue(LINE_TYPE0).orElse("");
        if (lineType0.contains(NON_TUNNELED)) {
          return NON_TUNNELED;
        } else if (lineType0.contains(TUNNELED)) {
          return TUNNELED;
        }
        break;

      case HEMODIALYSIS_PHERESIS_CATHETER:
        String accessType = getValue(LINE_ACCESS_TYPE).orElse("");
        if (accessType.contains(NON_TUNNELED_CATHETER)) {
          return NON_TUNNELED;
        } else if (accessType.contains(TUNNELED_CATHETER)) {
          return TUNNELED;
        }
        break;
    }

    return UNKNOWN;
  }

  private Optional<DateTimeDt> extractLineDateTime(String dateCode, String timeCode) {
    Optional<LocalDate> date = getValue(dateCode)
        .map(string -> LocalDate.parse(string, DateTimeFormatter.BASIC_ISO_DATE));
    if (!date.isPresent()) {
      return Optional.empty();
    }

    LocalTime time = getValue(timeCode)
        .map(string -> LocalTime.parse(string, LOCAL_TIME))
        .orElse(LocalTime.MIDNIGHT);

    return Optional.of(Dates.toDateTime(date.get(), time, clock.getZone()));
  }

  private static String computeLineProcedureCode(String... parts) {
    StringJoiner procedureCode = new StringJoiner(" ");
    for (String part : parts) {
      if (!part.equals(UNKNOWN)) {
        procedureCode.add(part);
      }
    }

    return procedureCode.toString();
  }

  private Optional<Procedure> insertLine() {
    Optional<Observation> inputLineType = getObservation(
        LINE_TYPE0,
        LINE_TYPE9,
        LINE_PLACEMENT_DATE,
        LINE_PLACEMENT_TIME,
        LINE_ORIENTATION1,
        LINE_ORIENTATION2,
        LINE_ORIENTATION3,
        LINE_LOCATION2,
        LINE_LOCATION7,
        LINE_LOCATION0,
        LINE_LOCATION4,
        LINE_ACCESS_TYPE,
        LINE_ACCESS_LOCATION,
        LINE_LUMENS);
    if (!inputLineType.isPresent()) {
      return Optional.empty();
    }

    Optional<LineType> optionalLineType =
        extractBasicLineType(inputLineType.get().getCode().getText());
    if (!optionalLineType.isPresent()) {
      return Optional.empty();
    }
    LineType basicLineType = optionalLineType.get();

    String tunneledStatus = extractLineTunneledStatus(basicLineType);
    String lineType = extractLineType(basicLineType);
    String bodySite = extractLineBodySite(basicLineType);
    String orientation = extractLineOrientation(basicLineType);
    Optional<DateTimeDt> placementDateTime =
        extractLineDateTime(LINE_PLACEMENT_DATE, LINE_PLACEMENT_TIME);

    Procedure procedure = new Procedure()
        .setStatus(
            removed ? ProcedureStatusEnum.COMPLETED : ProcedureStatusEnum.IN_PROGRESS)
        .setCategory(
            ProcedureCategoryEnum.CENTRAL_LINE.toCodeableConcept())
        .addIdentifier(
            inputLineType.get().getIdentifierFirstRep())
        .setCode(
            new CodeableConceptDt(
                CodingSystems.PROCEDURE, computeLineProcedureCode(tunneledStatus, lineType)))
        .setEncounter(
            new ResourceReferenceDt(encounter))
        .addBodySite(
            new CodeableConceptDt(
                CodingSystems.BODY_SITE, bodySite))
        .addBodySite(
            new CodeableConceptDt(
                CodingSystems.BODY_ORIENTATION, orientation));
    if (placementDateTime.isPresent()) {
      procedure.setPerformed(placementDateTime.get());
    }

    InstantDt updated = new InstantDt(Date.from(Instant.now(clock)));
    ResourceMetadataKeyEnum.UPDATED.put(procedure, updated);

    return Optional.of(procedure);
  }

  private Optional<Observation> getRemovedLine() {
    return getObservation(
        LINE_REMOVAL_DATE,
        LINE_REMOVAL_REASON86,
        LINE_REMOVAL_REASON96,
        LINE_REMOVAL_REASON28);
  }

  private Optional<Procedure> removeLine() {
    Optional<Observation> inputLineType = getRemovedLine();

    Optional<LineType> optionalLineType =
        extractBasicLineType(inputLineType.get().getCode().getText());
    if (!optionalLineType.isPresent()) {
      return Optional.empty();
    }
    LineType basicLineType = optionalLineType.get();

    String tunneledStatus = extractLineTunneledStatus(basicLineType);
    String lineType = extractLineType(basicLineType);
    Optional<DateTimeDt> removalDateTime =
        extractLineDateTime(LINE_REMOVAL_DATE, LINE_REMOVAL_TIME);

    Procedure procedure = new Procedure()
        .setStatus(
            ProcedureStatusEnum.COMPLETED)
        .setCategory(
            ProcedureCategoryEnum.CENTRAL_LINE.toCodeableConcept())
        .addIdentifier(
            inputLineType.get().getIdentifierFirstRep())
        .setCode(
            new CodeableConceptDt(
                CodingSystems.PROCEDURE, computeLineProcedureCode(tunneledStatus, lineType)))
        .setEncounter(
            new ResourceReferenceDt(encounter));
    if (removalDateTime.isPresent()) {
      procedure.setPerformed(removalDateTime.get());
    }

    InstantDt updated = new InstantDt(Date.from(Instant.now(clock)));
    ResourceMetadataKeyEnum.UPDATED.put(procedure, updated);

    return Optional.of(procedure);
  }

  private Optional<Procedure> toLineProcedure() {
    if (getRemovedLine().isPresent()) {
      return removeLine();
    }

    return insertLine();
  }

  /**
   * Creates procedure.
   *
   * @return optional procedure, empty if procedure cannot be created.
   */
  public Optional<Procedure> build() {
    Optional<Procedure> procedure = toLineProcedure();
    if (procedure.isPresent() &&
        Strings.isNullOrEmpty(procedure.get().getIdentifierFirstRep().getValue())) {
      log.error("Discarded procedure with missing identifier for encounter ID {}",
          encounter.getIdentifierFirstRep().getValue());
      return Optional.empty();
    } else {
      return procedure;
    }
  }
}
