// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.domain.fhir;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.primitive.StringDt;
import java.util.List;
import java.util.StringJoiner;

/**
 * Human Name formatting utility methods
 */
public class HumanNames {

  // Private constructor disallows creating instances of this class.
  private HumanNames() {
  }

  /**
   * Converts a HumanNameDt to a String using this pattern:
   * Given0 Given1 ... GivenN Family0 Family1 ... FamilyN
   * Does not include prefixes or suffixes.
   *
   * @param name
   *     the human name resource
   * @return
   *     a string representation of the name
   */
  public static String toFullName(HumanNameDt name) {
    return new NameBuilder(name).allGiven().allFamily().build();
  }

  /**
   * Converts a HumanNameDt to a String using this pattern:
   * Prefix0..N Given0..N Family0..N Suffix0..N
   *
   * @param name
   *     the human name resource
   * @return
   *     a string representation of the name
   */
  public static String toFullNameWithPrefixSuffix(HumanNameDt name) {
    return new NameBuilder(name).allPrefix().allGiven().allFamily().allSuffix().build();
  }

  /**
   * Converts a HumanNameDt to a String using this pattern:
   * Given0 Given1 Family0
   *
   * @param name
   *     the human name resource
   * @return
   *     a string representation of the name
   */
  public static String toFirstMiddleLastName(HumanNameDt name) {
    return new NameBuilder(name).firstGiven().secondGiven().firstFamily().build();
  }

  /**
   * Converts a HumanNameDt to a String using this pattern:
   * Family0 Given0 Given1's first letter
   * Example: Jim Bob Johnson becomes Johnson Jim B
   *
   * @param name
   *     the human name resource
   * @return
   *     a string representation of the name
   */
  public static String toLastFirstMiddleInitial(HumanNameDt name) {
    return new NameBuilder(name).firstFamily().firstGiven().secondGivenInitial().build();
  }

  /**
   * Takes a HumanNameDt and assembles it into a string using the builder pattern.
   */
  public static class NameBuilder {
    private HumanNameDt name = null;
    private final StringJoiner nameBuilder = new StringJoiner(" ");

    /**
     * Constructs a name builder
     *
     * @param name
     *     A HumanNameDt resource we want to turn into a string.
     */
    public NameBuilder(HumanNameDt name) {
      this.name = name;
    }

    private void addList(List<StringDt> nameParts) {
      for (StringDt part : nameParts) {
        nameBuilder.add(part.getValue());
      }
    }

    private void addSingleName(StringDt namePart) {
      if (namePart != null) {
        nameBuilder.add(namePart.getValue());
      }
    }

    private void addInitial(StringDt namePart) {
      if (namePart != null && !namePart.getValue().isEmpty()) {
        nameBuilder.add(namePart.getValue().substring(0, 1));
      }
    }

    /**
     * Adds all given names to the builder.
     *
     * @return
     *    This builder class.
     */
    public final NameBuilder allGiven() {
      addList(name.getGiven());
      return this;
    }

    /**
     * Adds the 0th given name to the builder.
     *
     * @return
     *    This builder class.
     */
    public final NameBuilder firstGiven() {
      addSingleName(name.getGiven().get(0));
      return this;
    }

    /**
     * Adds the 0th given name to the builder.
     *
     * @return
     *    This builder class.
     */
    public final NameBuilder secondGiven() {
      addSingleName(name.getGiven().get(1));
      return this;
    }

    /**
     * Adds the 0th given name to the builder.
     *
     * @return
     *    This builder class.
     */
    public final NameBuilder secondGivenInitial() {
      addInitial(name.getGiven().get(1));
      return this;
    }

    /**
     * Adds all family names to the builder.
     *
     * @return
     *    This builder class.
     */
    public final NameBuilder allFamily() {
      addList(name.getFamily());
      return this;
    }

    /**
     * Adds the 0th family name to the builder.
     *
     * @return
     *    This builder class.
     */
    public final NameBuilder firstFamily() {
      addSingleName(name.getFamily().get(0));
      return this;
    }

    /**
     * Adds the 0th character of the 0th family name to the builder.
     *
     * @return
     *    This builder class.
     */
    public final NameBuilder firstFamilyInitial() {
      addInitial(name.getGiven().get(1));
      return this;
    }

    /**
     * Adds all name prefixes to the builder.
     *
     * @return
     *    This builder class.
     */
    public final NameBuilder allPrefix() {
      addList(name.getPrefix());
      return this;
    }

    /**
     * Adds all name suffixes to the builder.
     *
     * @return
     *    This builder class.
     */
    public final NameBuilder allSuffix() {
      addList(name.getPrefix());
      return this;
    }

    /**
     * Return the assembled name as string.
     *
     * @return
     *    The name as constructed in NameBuilder as a string.
     */
    public String build() {
      return nameBuilder.toString();
    }

    @Override
    public String toString() {
      return build();
    }
  }
}
