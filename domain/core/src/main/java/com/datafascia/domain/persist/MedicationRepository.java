// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.MutationBuilder;
import com.datafascia.common.accumulo.MutationSetter;
import com.datafascia.common.accumulo.RowMapper;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.model.CodeableConcept;
import com.datafascia.domain.model.Content;
import com.datafascia.domain.model.Ingredient;
import com.datafascia.domain.model.Medication;
import com.datafascia.domain.model.MedicationAdministration;
import com.datafascia.domain.model.MedicationPackage;
import com.datafascia.domain.model.Product;
import com.datafascia.domain.model.ProductBatch;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;

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

  private static final MedicationRowMapper MEDICATION_ROW_MAPPER = new MedicationRowMapper();
  private static final TypeReference<List<Ingredient>> INGREDIENT_LIST_TYPE =
      new TypeReference<List<Ingredient>>() { };
  private static final TypeReference<List<ProductBatch>> BATCH_LIST_TYPE =
      new TypeReference<List<ProductBatch>>() { };
  private static final TypeReference<List<Content>> CONTENT_LIST_TYPE =
      new TypeReference<List<Content>>() { };

  private static class MedicationRowMapper implements RowMapper<Medication> {
    private Medication medication;

    @Override
    public void onBeginRow(Key key) {
      medication = new Medication();
      medication.setId(Id.of(extractEntityId(key)));
      medication.setProduct(new Product());
      medication.setPackage(new MedicationPackage());
    }

    @Override
    public void onReadEntry(Map.Entry<Key, Value> entry) {
      byte[] value = entry.getValue().get();
      switch (entry.getKey().getColumnQualifier().toString()) {
        case NAME:
          medication.setName(decodeString(value));
          break;
        case CODE_CODING_CODE:
          String code = decodeString(value);
          medication.setCode(new CodeableConcept(Arrays.asList(code), code));
          break;
        case IS_BRAND:
          medication.setIsBrand(decodeBoolean(value));
          break;
        case MANUFACTURER_ID:
          medication.setManufacturerId(Id.of(decodeString(value)));
          break;
        case KIND:
          medication.setKind(decodeString(value));
          break;
        case PRODUCT_FORM_CODING_CODE:
          String formCode = decodeString(value);
          medication.getProduct().setForm(new CodeableConcept(Arrays.asList(formCode), formCode));
          break;
        case PRODUCT_INGREDIENTS:
          medication.getProduct().setIngredients(decode(value, INGREDIENT_LIST_TYPE));
          break;
        case PRODUCT_BATCHES:
          medication.getProduct().setBatches(decode(value, BATCH_LIST_TYPE));
          break;
        case PACKAGE_CONTAINER_CODING_CODE:
          String containerCode = decodeString(value);
          medication.getPackage().setContainer(new CodeableConcept(Arrays.asList(containerCode),
              containerCode));
          break;
        case PACKAGE_CONTENTS:
          medication.getPackage().setContents(decode(value, CONTENT_LIST_TYPE));
          break;
      }
    }

    @Override
    public Medication onEndRow() {
      return medication;
    }
  }

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
                .put(CODE_CODING_CODE, medication.getCode().getCodings().get(0))
                .put(IS_BRAND, medication.getIsBrand())
                .put(MANUFACTURER_ID, medication.getManufacturerId())
                .put(KIND, medication.getKind())
                .put(PRODUCT_FORM_CODING_CODE, medication.getProduct().getForm().getCodings()
                    .get(0))
                .put(PRODUCT_INGREDIENTS, medication.getProduct().getIngredients())
                .put(PRODUCT_BATCHES, medication.getProduct().getBatches())
                .put(
                    PACKAGE_CONTAINER_CODING_CODE,
                    medication.getPackage().getContainer().getCodings().get(0))
                .put(PACKAGE_CONTENTS, medication.getPackage().getContents());
          }
        });
  }

  /**
   * Reads medication.
   *
   * @param medicationId
   *     entity ID to read
   * @return optional entity, empty if not found
   */
  public Optional<Medication> read(Id<Medication> medicationId) {
    Scanner scanner = accumuloTemplate.createScanner(Tables.PATIENT);
    scanner.setRange(Range.exact(toRowId(medicationId)));
    scanner.fetchColumnFamily(new Text(COLUMN_FAMILY));

    return accumuloTemplate.queryForObject(scanner, MEDICATION_ROW_MAPPER);
  }
}
