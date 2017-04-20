/*
 * Copyright 2013-2017 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.jaxrs.generator;

import org.jsonschema2pojo.AnnotationStyle;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.NoopAnnotator;
import org.jsonschema2pojo.rules.RuleFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 1/10/17. Just potential zeroes and ones
 */
class RamlToJaxRSGenerationConfig extends DefaultGenerationConfig {

  private AnnotationStyle jsonMapper;
  private Map<String, String> jsonMapperConfiguration = new HashMap<>();

  public RamlToJaxRSGenerationConfig(AnnotationStyle jsonMapper,
                                     Map<String, String> jsonMapperConfiguration) {
    this.jsonMapper = jsonMapper;
    this.jsonMapperConfiguration = jsonMapperConfiguration;
  }

  @Override
  public AnnotationStyle getAnnotationStyle() {
    return jsonMapper;
  }

  @Override
  public boolean isIncludeJsr303Annotations() {
    return getConfiguredValue("isIncludeJsr303Annotations", false);
  }

  @Override
  public boolean isUseCommonsLang3() {
    return getConfiguredValue("useCommonsLang3", false);
  }

  @Override
  public boolean isGenerateBuilders() {
    return getConfiguredValue("generateBuilders", true);
  }

  @Override
  public boolean isIncludeHashcodeAndEquals() {
    return getConfiguredValue("includeHashcodeAndEquals", false);
  }

  @Override
  public Class<? extends Annotator> getCustomAnnotator() {
    String className = getConfiguredValueStr("customAnnotator", null);
    if (className == null) {
      return NoopAnnotator.class;
    } else {
      try {
        return (Class<? extends Annotator>) Class.forName(className);
      } catch (ClassNotFoundException e) {

        throw new GenerationException(e);
      }
    }
  }

  @Override
  public boolean isIncludeToString() {
    return getConfiguredValue("includeToString", false);
  }

  @Override
  public boolean isUseLongIntegers() {
    return getConfiguredValue("useLongIntegers", false);
  }

  @Override
  public boolean isIncludeConstructors() {
    return getConfiguredValue("includeConstructors", super.isIncludeConstructors());
  }

  @Override
  public boolean isConstructorsRequiredPropertiesOnly() {
    return getConfiguredValue("constructorsRequiredPropertiesOnly",
                              super.isConstructorsRequiredPropertiesOnly());
  }

  @Override
  public boolean isIncludeAccessors() {
    return getConfiguredValue("includeAccessors", super.isIncludeAccessors());
  }

  @Override
  public boolean isUseJodaDates() {
    return getConfiguredValue("useJodaDates", false);
  }

  @Override
  public boolean isUseJodaLocalDates() {
    return getConfiguredValue("useJodaLocalDates", false);
  }

  @Override
  public boolean isUseJodaLocalTimes() {
    return getConfiguredValue("useJodaLocalTimes", false);
  }

  @Override
  public String getDateTimeType() {
    return getConfiguredValueStr("dateTimeType", null);
  }

  @Override
  public String getDateType() {
    return getConfiguredValueStr("dateType", null);
  }

  @Override
  public String getTimeType() {
    return getConfiguredValueStr("timeType", null);
  }


  @Override
  public boolean isUsePrimitives() {
    return getConfiguredValue("usePrimitives", false);
  }

  @Override
  public boolean isUseDoubleNumbers() {
    return getConfiguredValue("useDoubleNumbers", true);
  }

  @Override
  public Class<? extends RuleFactory> getCustomRuleFactory() {
    String factory = getConfiguredValueStr("customRuleFactory", null);
    if (factory == null) {
      return org.jsonschema2pojo.rules.RuleFactory.class;
    } else {
      try {
        return (Class<? extends RuleFactory>) Class.forName(factory);
      } catch (ClassNotFoundException e) {
        throw new GenerationException(e);
      }
    }
  }

  @Override
  public String getOutputEncoding() {
    return getConfiguredValueStr("outputEncoding", "UTF-8");
  }

  @Override
  public boolean isParcelable() {
    return getConfiguredValue("parcelable", false);
  }

  @Override
  public boolean isSerializable() {
    return getConfiguredValue("serializable", false);
  }

  @Override
  public boolean isInitializeCollections() {
    return getConfiguredValue("initializeCollections", true);
  }

  @Override
  public String getClassNamePrefix() {
    return getConfiguredValueStr("classNamePrefix", "");
  }

  @Override
  public String getClassNameSuffix() {
    return getConfiguredValueStr("classNameSuffix", "");
  }

  @Override
  public boolean isUseBigIntegers() {
    return getConfiguredValue("useBigIntegers", false);
  }

  @Override
  public boolean isUseBigDecimals() {
    return getConfiguredValue("useBigDecimals", false);
  }

  @Override
  public boolean isIncludeAdditionalProperties() {
    return getConfiguredValue("includeAdditionalProperties", true);
  }

  @Override
  public String getTargetVersion() {
    return getConfiguredValueStr("targetVersion", "1.6");
  }

  @Override
  public boolean isIncludeDynamicAccessors() {
    return getConfiguredValue("includeDynamicAccessors", false);
  }

  private String getConfiguredValueStr(final String key, final String def) {
    if (jsonMapperConfiguration == null || jsonMapperConfiguration.isEmpty()) {
      return def;
    }
    final String val = jsonMapperConfiguration.get(key);
    return val != null ? val : def;
  }

  private boolean getConfiguredValue(final String key, final boolean def) {
    if (jsonMapperConfiguration == null || jsonMapperConfiguration.isEmpty()) {
      return def;
    }

    final String val = jsonMapperConfiguration.get(key);
    return val != null ? Boolean.parseBoolean(val) : def;
  }

}
