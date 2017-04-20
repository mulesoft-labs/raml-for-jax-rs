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
package org.raml.jaxrs.raml.core;

import org.raml.api.RamlApi;
import org.raml.emitter.Emitter;
import org.raml.emitter.FileEmitter;
import org.raml.emitter.RamlEmissionException;
import org.raml.jaxrs.converter.JaxRsToRamlConversionException;
import org.raml.jaxrs.converter.JaxRsToRamlConverter;
import org.raml.jaxrs.converter.RamlConfiguration;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.parser.JaxRsParsers;
import org.raml.jaxrs.parser.JaxRsParsingException;
import org.raml.jaxrs.parser.source.SourceParser;
import org.raml.jaxrs.parser.source.SourceParsers;
import org.raml.utilities.builder.NonNullableField;

import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.raml.utilities.builder.Preconditions.checkSet;
import static org.raml.utilities.builder.Preconditions.checkUnset;

public class OneStopShop {

  private final Path jaxRsUrl;
  private final Path ramlOutputFile;
  private final NonNullableField<Path> sourceCodeRoot;
  private final RamlConfiguration ramlConfiguration;

  private OneStopShop(Path jaxRsUrl, Path ramlOutputFile, NonNullableField<Path> sourceCodeRoot,
                      RamlConfiguration ramlConfiguration) {
    this.jaxRsUrl = jaxRsUrl;
    this.ramlOutputFile = ramlOutputFile;
    this.sourceCodeRoot = sourceCodeRoot;
    this.ramlConfiguration = ramlConfiguration;
  }

  private static OneStopShop create(Path jaxRsUrl, Path ramlOutputFile,
                                    NonNullableField<Path> sourceCodeRoot, RamlConfiguration ramlConfiguration) {
    checkNotNull(jaxRsUrl);
    checkNotNull(ramlOutputFile);
    checkNotNull(sourceCodeRoot);
    checkNotNull(ramlConfiguration);

    return new OneStopShop(jaxRsUrl, ramlOutputFile, sourceCodeRoot, ramlConfiguration);
  }

  public void parseJaxRsAndOutputRaml() throws JaxRsToRamlConversionException,
      JaxRsParsingException, RamlEmissionException {

    SourceParser sourceParser =
        sourceCodeRoot.isSet() ? SourceParsers.usingRoasterParser(sourceCodeRoot.get())
            : SourceParsers.nullParser();

    JaxRsApplication application =
        JaxRsParsers.usingJerseyWith(jaxRsUrl, sourceParser, ramlConfiguration.getTranslatedAnnotations()).parse();

    RamlApi ramlApi = JaxRsToRamlConverter.create().convert(ramlConfiguration, application);

    Emitter emitter = FileEmitter.forFile(ramlOutputFile);

    emitter.emit(ramlApi);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    NonNullableField<Path> jaxRsClassesRoot = NonNullableField.unset();
    NonNullableField<Path> sourceCodeRoot = NonNullableField.unset(); // Optional field.
    NonNullableField<Path> ramlOutputFile = NonNullableField.unset();
    NonNullableField<RamlConfiguration> ramlConfiguration = NonNullableField.unset();
    private List<Class<? extends Annotation>> translatedAnnotations;

    private Builder() {}

    public Builder withJaxRsClassesRoot(Path jaxRsClassesRoot) {
      checkUnset(this.jaxRsClassesRoot, "classes root");

      this.jaxRsClassesRoot = NonNullableField.of(jaxRsClassesRoot);

      return this;
    }

    public Builder withSourceCodeRoot(Path sourceCodeRoot) {
      checkUnset(this.sourceCodeRoot, "source code root");

      this.sourceCodeRoot = NonNullableField.of(sourceCodeRoot);

      return this;
    }

    public Builder withRamlOutputFile(Path ramlOutputFile) {
      checkUnset(this.ramlOutputFile, "raml output file");

      this.ramlOutputFile = NonNullableField.of(ramlOutputFile);

      return this;
    }

    public Builder withRamlConfiguration(RamlConfiguration ramlConfiguration) {
      checkUnset(this.ramlConfiguration, "raml configuration");

      this.ramlConfiguration = NonNullableField.of(ramlConfiguration);

      return this;
    }

    public Builder withTranslatedAnnotations(List<Class<? extends Annotation>> translatedAnnotations) {

      this.translatedAnnotations = translatedAnnotations;
      return this;
    }

    public OneStopShop build() {
      checkSet(this.jaxRsClassesRoot, "classes root");
      checkSet(this.ramlOutputFile, "raml output file");
      checkSet(this.ramlConfiguration, "raml configuration");

      Path classesRoot = jaxRsClassesRoot.get();
      checkArgument(Files.isDirectory(classesRoot), "classes root %s is not a valid directory",
                    classesRoot);

      if (sourceCodeRoot.isSet()) {
        Path path = sourceCodeRoot.get();
        checkArgument(Files.isDirectory(path), "source code root %s is not a valid directory", path);
      }


      return OneStopShop.create(classesRoot, this.ramlOutputFile.get(), this.sourceCodeRoot,
                                this.ramlConfiguration.get());
    }

  }

}
