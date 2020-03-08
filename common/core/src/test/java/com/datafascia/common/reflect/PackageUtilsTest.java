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
package com.datafascia.common.reflect;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Test code for PackageUtils
 */
@Test
public class PackageUtilsTest {
  private final String thisPackageName = this.getClass().getPackage().toString().split(" ")[1];
  private final Class testedClass = PackageUtils.class;
  private final Class testedAnnotation = Slf4j.class;
  private final Class testedAnnotation2 = Test.class;

  @Test
  public void testClasses() {
    Set<Class<?>> classes = PackageUtils.classes(thisPackageName);
    assertTrue(classes.contains(this.getClass()));
    assertTrue(classes.contains(testedClass));
    assertFalse(classes.contains(Object.class));
  }

  @Test
  public void testWithTypeAnnotations() {
    Set<Class<?>> classes = PackageUtils.withTypeAnnotations(thisPackageName, testedAnnotation);
    assertFalse(classes.contains(this.getClass()));
    // Annotations without @Retention(RetentionPolicy.RUNTIME) are not preserved at runtime
    assertFalse(classes.contains(testedClass));
    assertFalse(classes.contains(Object.class));

    classes = PackageUtils.withTypeAnnotations(thisPackageName, testedAnnotation2);
    assertTrue(classes.contains(this.getClass()));
    assertFalse(classes.contains(testedClass));
    assertFalse(classes.contains(Object.class));
  }
}
