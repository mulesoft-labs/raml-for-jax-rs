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
package org.raml.jaxrs.parser.source;

import com.google.common.base.Optional;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaDoc;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.MethodHolder;
import org.jboss.forge.roaster.model.TypeHolder;
import org.jboss.forge.roaster.model.source.JavaDocSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.raml.utilities.format.Joiners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

class RoasterSourceParser implements SourceParser {

  private static final Logger logger = LoggerFactory.getLogger(RoasterSourceParser.class);

  private final Path sourceRoot;

  private RoasterSourceParser(Path sourceRoot) {
    this.sourceRoot = sourceRoot;
  }

  public static RoasterSourceParser create(Path sourceRoot) {
    checkNotNull(sourceRoot);

    return new RoasterSourceParser(sourceRoot);
  }

  @Override
  public Optional<String> getDocumentationFor(Method method) {
    Class<?> declaringClass = method.getDeclaringClass();
    Path classFileRelativePath = Utilities.getSourceFileRelativePath(declaringClass);
    Path relativeFromRoot = sourceRoot.resolve(classFileRelativePath);

    if (!Files.isRegularFile(relativeFromRoot)) {
      logger.warn("could not find source file {} for method {}", relativeFromRoot, method);
      return Optional.absent();
    }

    return parseDocumentationFor(method, relativeFromRoot);
  }

  @Override
  public Optional<String> getDocumentationFor(Type clazz) {

    Path classFileRelativePath = Utilities.getSourceFileRelativePath((Class) clazz);
    Path relativeFromRoot = sourceRoot.resolve(classFileRelativePath);

    if (!Files.isRegularFile(relativeFromRoot)) {
      logger.warn("could not find source file {} for class {}", relativeFromRoot, clazz);
      return Optional.absent();
    }

    return parseDocumentationFor((Class<?>) clazz, relativeFromRoot);
  }

  private Optional<String> parseDocumentationFor(Method method, Path file) {
    try {
      JavaType<?> parsed = Roaster.parse(file.toFile());
      if (!(parsed instanceof MethodHolder)) {
        logger.warn("unexpected type returned from roaster: {}", parsed.getClass());
        return Optional.absent();
      }

      return extractMethodJavadoc(method, (MethodHolder<?>) parsed);

    } catch (FileNotFoundException e) {
      logger.warn("exception occurred while attempting to parse file {} for method {}", file,
                  method, e);
      return Optional.absent();
    }
  }

  private Optional<String> parseDocumentationFor(Class<?> clazz, Path file) {
    try {
      JavaType<?> parsed = Roaster.parse(file.toFile());
      JavaDoc<?> s = parsed.getJavaDoc();
      return Optional.of(s.getText());

    } catch (FileNotFoundException e) {
      logger.warn("exception occurred while attempting to parse file {} for class {}", file,
                  clazz, e);
      return Optional.absent();
    }
  }

  private static Optional<String> extractMethodJavadoc(Method method, MethodHolder<?> parsed) {
    final String methodName = method.getName();
    List<? extends org.jboss.forge.roaster.model.Method<?, ?>> methods = parsed.getMethods();

    for (org.jboss.forge.roaster.model.Method<?, ?> parsedMethod : methods) {
      if (parsedMethod.getName().equals(methodName)) {

        if (!(parsedMethod instanceof MethodSource)) {
          logger.warn("unexpected method type: {}", parsedMethod.getClass());
          return Optional.absent();
        }
        MethodSource methodSource = (MethodSource) parsedMethod;
        return javadocToContent(methodSource.getJavaDoc());
      }
    }

    logger.warn("could not find method {} in parsed methods: {}", method, Joiners
        .squareBracketsSameLineJoiner().join(methods));
    return Optional.absent();
  }


  private static Optional<String> javadocToContent(JavaDocSource javaDoc) {
    return Optional.fromNullable(javaDoc.getText());
  }


}
