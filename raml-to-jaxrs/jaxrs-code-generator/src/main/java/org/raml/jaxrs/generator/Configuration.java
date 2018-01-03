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
import org.jsonschema2pojo.GenerationConfig;
import org.raml.jaxrs.generator.extension.resources.GlobalResourceExtension;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/25/16. Just potential zeroes and ones
 */
public class Configuration {


  private String modelPackage;
  private File outputDirectory;
  private AnnotationStyle jsonMapper;
  private Map<String, String> jsonMapperConfiguration = new HashMap<>();
  private String[] typeConfiguration = new String[0];
  private String resourcePackage;
  private String supportPackage;

  private Class<GlobalResourceExtension> defaultCreationExtension;
  private Class<GlobalResourceExtension> defaultFinishExtension;


  public void setupBuild(CurrentBuild build) {

    build.setConfiguration(this);
  }

  public void setJsonMapper(AnnotationStyle jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  public void setJsonMapperConfiguration(Map<String, String> jsonMapperConfiguration) {
    this.jsonMapperConfiguration = jsonMapperConfiguration;
  }

  public String getSupportPackage() {
    if (supportPackage == null) {
      return resourcePackage;
    }
    return supportPackage;
  }

  public void setSupportPackage(String supportPackage) {
    this.supportPackage = supportPackage;
  }

  public String getModelPackage() {

    if (modelPackage == null) {
      return resourcePackage;
    }
    return modelPackage;
  }

  public void setModelPackage(String modelPackage) {
    this.modelPackage = modelPackage;
  }

  public String getResourcePackage() {
    return resourcePackage;
  }

  public void setResourcePackage(String resourcePackage) {
    this.resourcePackage = resourcePackage;
  }

  public String[] getTypeConfiguration() {
    return typeConfiguration;
  }

  public void setTypeConfiguration(String[] typeConfiguration) {

    this.typeConfiguration = typeConfiguration;
  }

  public File getOutputDirectory() {
    return outputDirectory;
  }

  public void setOutputDirectory(File outputDirectory) {
    this.outputDirectory = outputDirectory;
  }

  public static Configuration defaultConfiguration() {

    Configuration configuration = new Configuration();
    configuration.setModelPackage("model");
    configuration.setResourcePackage("resource");
    configuration.setSupportPackage("support");
    configuration.setOutputDirectory(new File("."));
    // configuration.setJsonMapper(AnnotationStyle.valueOf(jsonMapper.toUpperCase()));
    // configuration.setJsonMapperConfiguration(jsonMapperConfiguration);
    configuration.setTypeConfiguration(new String[] {"jackson"});

    return configuration;

  }


  public GenerationConfig createJsonSchemaGenerationConfig()
  {
    return new RamlToJaxRSGenerationConfig(jsonMapper, jsonMapperConfiguration);
  }

  public void defaultResourceCreationExtension(Class<GlobalResourceExtension> c) {
    defaultCreationExtension = c;
  }

  public void defaultResourceFinishExtension(Class<GlobalResourceExtension> c) {
    defaultFinishExtension = c;
  }

  public Class<GlobalResourceExtension> getDefaultCreationExtension() {
    return defaultCreationExtension;
  }

  public Class<GlobalResourceExtension> getDefaultFinishExtension() {
    return defaultFinishExtension;
  }
}
