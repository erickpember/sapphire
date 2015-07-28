// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.Dates;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Creates procedure from observations.
 */
public class ProcedureBuilder {

  private static final String LINE_TYPE0 = "304890080";
  private static final String LINE_TYPE9 = "304890099";
  private static final String LINE_PLACEMENT_DATE = "304890077";
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

  private static final String REMOVED = "[REMOVED] ";
  private static final DateTimeFormatter LOCAL_TIME = DateTimeFormatter.ofPattern("HHmmss");

  private static final String LEFT = "Left";
  private static final String LOWER = "Lower";
  private static final String MEDIAL = "Medial";
  private static final String RIGHT = "Right";
  private static final String OTHER_COMMENT = "Other (Comment)";
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
  private final Map<String, Observation> codeToObservationMap = new HashMap<>();

  /**
   * Constructor
   *
   * @param encounter
   *    encounter while procedure was performed
   */
  public ProcedureBuilder(Encounter encounter) {
    this.encounter = encounter;
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

  private String extractLineName(LineType basicLineType) {
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
          return OTHER;
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
          return OTHER;
        } else if (orientation1.contains(OTHER_COMMENT)) {
          return OTHER;
        }
    }

    return UNKNOWN;
  }

  private String extractLineLocation(LineType basicLineType) {
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
        } else if (location0.contains(OTHER_COMMENT)) {
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

  private Optional<DateTimeDt> extractLinePlacementDateTime() {
    Optional<LocalDate> placementDate = getValue(LINE_PLACEMENT_DATE)
        .map(string -> LocalDate.parse(string, DateTimeFormatter.BASIC_ISO_DATE));
    if (!placementDate.isPresent()) {
      return Optional.empty();
    }

    LocalTime placementTime = getValue(LINE_PLACEMENT_TIME)
        .map(string -> LocalTime.parse(string, LOCAL_TIME))
        .orElse(LocalTime.MIDNIGHT);

    return Optional.of(Dates.toDateTime(placementDate.get(), placementTime));
  }

  private static String computeLineProcedureType(String... parts) {
    StringJoiner procedureCode = new StringJoiner(" ");
    for (String part : parts) {
      if (!part.equals(UNKNOWN)) {
        procedureCode.add(part);
      }
    }

    return procedureCode.toString();
  }

  private static String computeLineProcedureBodySite(String location, String orientation) {
    String bodySite;
    if (location.equals(UNKNOWN) || orientation.equals(UNKNOWN)) {
      bodySite = UNKNOWN;
    } else if (location.equals(OTHER) || orientation.equals(OTHER)) {
      bodySite = OTHER;
    } else {
      bodySite = location + " (" + orientation + ")";
    }

    return bodySite;
  }

  private Optional<Procedure> toLineProcedure() {
    Optional<Observation> lineType = getObservation(LINE_TYPE0, LINE_TYPE9);
    if (!lineType.isPresent()) {
      return Optional.empty();
    }

    String text = lineType.get().getCode().getText();
    int basicNameIndex = text.indexOf('-');
    if (basicNameIndex < 0) {
      throw new IllegalStateException(text + " does not contain -");
    }
    String basicName = text.substring(basicNameIndex + 1);
    boolean removed = basicName.startsWith(REMOVED);
    if (removed) {
      basicName = basicName.substring(REMOVED.length());
    }

    Optional<LineType> optionalLineType = LineType.of(basicName);
    if (!optionalLineType.isPresent()) {
      return Optional.empty();
    }
    LineType basicLineType = optionalLineType.get();

    String lineName = extractLineName(basicLineType);
    String orientation = extractLineOrientation(basicLineType);
    String location = extractLineLocation(basicLineType);
    String tunneledStatus = extractLineTunneledStatus(basicLineType);
    Optional<DateTimeDt> placementDateTime = extractLinePlacementDateTime();

    Procedure procedure = new Procedure()
        .setStatus(removed ? ProcedureStatusEnum.COMPLETED : ProcedureStatusEnum.IN_PROGRESS)
        .setType(new CodeableConceptDt(
            CodingSystems.PROCEDURE_TYPE, computeLineProcedureType(tunneledStatus, lineName)))
        .setEncounter(new ResourceReferenceDt(encounter));
    procedure.addBodySite()
        .setSite(new CodeableConceptDt(
            CodingSystems.BODY_SITE, computeLineProcedureBodySite(location, orientation)));
    if (placementDateTime.isPresent()) {
      procedure.setPerformed(placementDateTime.get());
    }
    return Optional.of(procedure);
  }

  /**
   * Creates procedure.
   *
   * @return optional procedure, empty if procedure cannot be created.
   */
  public Optional<Procedure> build() {
    return toLineProcedure();
  }
}
