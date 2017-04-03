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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Path;

public class SourceParsers {

  private static final SourceParser NO_OP_PARSER = new SourceParser() {

    @Override
    public Optional<String> getDocumentationFor(Method method) {
      return Optional.absent(); // Do nothing on purpose.
    }

    @Override
    public Optional<String> getDocumentationFor(Type method) {
      return Optional.absent();
    }
  };


  public static SourceParser usingRoasterParser(Path path) {
    return RoasterSourceParser.create(path);
  }

  public static SourceParser nullParser() {
    return NO_OP_PARSER;
  }
}
