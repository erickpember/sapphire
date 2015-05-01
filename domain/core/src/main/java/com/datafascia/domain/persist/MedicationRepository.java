// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.MutationBuilder;
import com.datafascia.common.accumulo.MutationSetter;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.model.Medication;
import com.datafascia.domain.model.MedicationAdministration;
import java.util.UUID;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Medication data access.
 * <p>
 * The row ID for an medication entity has the format:
 * <pre>
 * Medication={medicationId}&
 * </pre>
 */
@Slf4j
public class MedicationRepository extends BaseRepository {

  private static final String COLUMN_FAMILY = MedicationAdministration.class.getSimpleName();
  private static final String NAME = "name";
  private static final String CODE_CODING_CODE = "code.coding.code";
  private static final String IS_BRAND = "isBrand";
  private static final String MANUFACTURER_ID = "manufacturerId";
  private static final String KIND = "kind";
  private static final String PRODUCT_FORM_CODING_CODE = "product.form.coding.code";
  private static final String PRODUCT_INGREDIENTS = "product.ingredients";
  private static final String PRODUCT_BATCHES = "product.batches";
  private static final String PACKAGE_CONTAINER_CODING_CODE = "package.container.coding.code";
  private static final String PACKAGE_CONTENTS = "package.contents";

  /**
   * Constructor
   *
   * @param accumuloTemplate
   *     data access operations template
   */
  @Inject
  public MedicationRepository(AccumuloTemplate accumuloTemplate) {
    super(accumuloTemplate);
  }

  private static String toRowId(Id<Medication> medicationId) {
    return toRowId(Medication.class, medicationId);
  }

  private static Id<Medication> getEntityId(Medication medication) {
    return (medication.getId() != null)
        ? medication.getId()
        : Id.of(UUID.randomUUID().toString());
  }

  /**
   * Saves entity.
   *
   * @param medication
   *     to save
   */
  public void save(Medication medication) {
    medication.setId(getEntityId(medication));

    accumuloTemplate.save(
        Tables.PATIENT,
        toRowId(medication.getId()),
        new MutationSetter() {
          @Override
          public void putWriteOperations(MutationBuilder mutationBuilder) {
            mutationBuilder.columnFamily(COLUMN_FAMILY)
                .put(NAME, medication.getName())
                .put(CODE_CODING_CODE, medication.getCode().getCode())
                .put(IS_BRAND, medication.getIsBrand())
                .put(MANUFACTURER_ID, medication.getManufacturerId())
                .put(KIND, medication.getKind())
                .put(PRODUCT_FORM_CODING_CODE, medication.getProduct().getForm().getCode())
                .put(PRODUCT_INGREDIENTS, medication.getProduct().getIngredients())
                .put(PRODUCT_BATCHES, medication.getProduct().getBatches())
                .put(
                    PACKAGE_CONTAINER_CODING_CODE,
                    medication.getPackage().getContainer().getCode())
                .put(PACKAGE_CONTENTS, medication.getPackage().getContents());
          }
        });
  }
}
