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
package com.datafascia.common.configuration;

import com.datafascia.common.configuration.guice.ConfigureModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Tests injection of configuration values into objects.
 */
public class ConfigureTest {

  @ConfigurationNode("test-node")
  private static class InjectInto {

    @Configure
    private boolean booleanField;

    @Configure
    private int intField;

    @Configure
    private String stringField;

    private boolean booleanMethodValue;
    private int intMethodValue;
    private String stringMethodValue;

    private boolean booleanSetterValue;
    private int intSetterValue;
    private String stringSetterValue;

    @Configure
    public void booleanMethod(boolean value) {
      this.booleanMethodValue = value;
    }

    @Configure
    public void intMethod(int value) {
      this.intMethodValue = value;
    }

    @Configure
    public void stringMethod(String value) {
      this.stringMethodValue = value;
    }

    @Configure
    public void setBoolean(boolean value) {
      this.booleanSetterValue = value;
    }

    @Configure
    public void setInt(int value) {
      this.intSetterValue = value;
    }

    @Configure
    public void setString(String value) {
      this.stringSetterValue = value;
    }
  }

  private static Injector injector;

  @BeforeClass
  public static void beforeClass() {
    injector = Guice.createInjector(new ConfigureModule());
  }

  @Test
  public void should_inject_fields() {
    InjectInto instance = injector.getInstance(InjectInto.class);
    assertEquals(instance.booleanField, true);
    assertEquals(instance.intField, 1);
    assertEquals(instance.stringField, "stringField-value");
  }

  @Test
  public void should_inject_methods() {
    InjectInto instance = injector.getInstance(InjectInto.class);
    assertEquals(instance.booleanMethodValue, true);
    assertEquals(instance.intMethodValue, 2);
    assertEquals(instance.stringMethodValue, "stringMethod-value");
  }

  @Test
  public void should_inject_setters() {
    InjectInto instance = injector.getInstance(InjectInto.class);
    assertEquals(instance.booleanSetterValue, true);
    assertEquals(instance.intSetterValue, 3);
    assertEquals(instance.stringSetterValue, "setString-value");
  }
}
