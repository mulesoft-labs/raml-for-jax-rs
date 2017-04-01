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
package org.raml.jaxrs.parser;

import org.raml.jaxrs.parser.source.SourceParser;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class JaxRsParsers {

  private JaxRsParsers() {}

  public static JaxRsParser usingJerseyWith(Path classesPath, SourceParser sourceParser,
                                            Set<Class<? extends Annotation>> translatedAnnotations) {
    return JerseyJaxRsParser.create(classesPath, sourceParser, translatedAnnotations);
  }
}
