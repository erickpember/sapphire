// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.codes;

import com.datafascia.common.persist.Code;
import com.datafascia.common.persist.CodeToEnumMapper;
import java.util.Optional;

/**
 * Enumerates the drug names + routes, as formatted for Emerge, AKA MedsSet.
 */
public enum MedsSetEnum implements Code<String> {
  CONTINUOUS_INFUSION_LORAZEPAM_IV("Continuous Infusion Lorazepam IV"),
  CONTINUOUS_INFUSION_MIDAZOLAM_IV("Continuous Infusion Midazolam IV"),
  INTERMITTENT_ALPRAZALOM_ENTERAL("Intermittent Alprazalom Enteral"),
  INTERMITTENT_CHLORADIAZEPOXIDE_ENTERAL("Intermittent Chloradiazepoxide Enteral"),
  INTERMITTENT_CISATRACURIUM_IV("Intermittent Cisatracurium IV"),
  INTERMITTENT_CLONAZEPAM_ENTERAL("Intermittent Clonazepam Enteral"),
  INTERMITTENT_DIAZEPAM_ENTERAL("Intermittent Diazepam Enteral"),
  INTERMITTENT_DIAZEPAM_IV("Intermittent Diazepam IV"),
  INTERMITTENT_LORAZEPAM_ENTERAL("Intermittent Lorazepam Enteral"),
  INTERMITTENT_LORAZEPAM_IV("Intermittent Lorazepam IV"),
  INTERMITTENT_MIDAZOLAM_IV("Intermittent Midazolam IV"),
  INTERMITTENT_PANCURONIUM_IV("Intermittent Pancuronium IV"),
  INTERMITTENT_ROCURONIUM_IV("Intermittent Rocuronium IV"),
  INTERMITTENT_VECURONIUM_IV("Intermittent Vecuronium IV"),
  CONTINUOUS_INFUSION_DEXMEDETOMIDINE_IV("Continuous Infusion Dexmedetomidine IV"),
  CONTINUOUS_INFUSION_PROPOFOL_IV("Continuous Infusion Propofol IV"),

  CONTINUOUS_INFUSION_HEPARIN_IV("Continuous Infusion Heparin IV"),
  CONTINUOUS_INFUSION_ARGATROBAN_IV("Continuous Infusion Argatroban IV"),
  CONTINUOUS_INFUSION_BIVALIRUDIAN_IV("Continuous Infusion Bivalirudian IV"),
  INTERMITTENT_ENOXAPARIN("Intermittent Enoxaparin"),
  INTERMITTENT_HEPARIN_SC("Intermittent Heparin SC"),
  INTERMITTENT_DABIGATRAN_ENTERAL("Intermittent Dabigatran Enteral"),
  INTERMITTENT_APIXABAN_ENTERAL("Intermittent Apixaban Enteral"),
  INTERMITTENT_RIVAROXABAN_ENTERAL("Intermittent Rivaroxaban Enteral"),
  INTERMITTENT_EDOXABAN_ENTERAL("Intermittent Edoxaban Enteral"),
  INTERMITTENT_FONDAPARINUX_SC("Intermittent Fondaparinux SC"),
  INTERMITTENT_WARFARIN_ENTERAL("Intermittent Warfarin Enteral"),

  STRESS_ULCER_PROPHYLACTICS("Stress Ulcer Prophylactics"),

  ANY_SEDATIVE_INFUSION("Any sedative infusion"),
  ANY_INFUSION_NMBA("Any infusion NMBA"),
  ANY_BOLUS_NMBA("Any bolus NMBA"),

  LAST_MEDS_SET_NAME("DO NOT USE");

  private final String code;

  private static final CodeToEnumMapper<String, MedsSetEnum> CODE_TO_ENUM_MAPPER
      = new CodeToEnumMapper<>(MedsSetEnum.class);

  MedsSetEnum(String code) {
    this.code = code;
  }

  @Override
  public String getCode() {
    return code;
  }

  /**
   * Converts code to enum constant.
   *
   * @param code
   *     input code
   * @return optional enum constant, empty if code is unknown
   */
  public static Optional<MedsSetEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
