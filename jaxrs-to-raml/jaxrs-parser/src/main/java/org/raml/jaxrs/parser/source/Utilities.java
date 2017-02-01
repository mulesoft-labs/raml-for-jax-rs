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

import com.google.common.base.Splitter;

import java.nio.file.Path;
import java.nio.file.Paths;

class Utilities {

  public static Path getSourceFileRelativePath(Class<?> declaringClass) {
    Path packageRelativePath = transformIntoPath(declaringClass);
    String declaringFileName = getSourceFileName(declaringClass);
    return packageRelativePath.resolve(declaringFileName);
  }

  private static String getSourceFileName(Class<?> declaringClass) {
    final String SOURCE_FILE_EXTENSION = ".java";
    return declaringClass.getSimpleName() + SOURCE_FILE_EXTENSION;
  }

  private static Path transformIntoPath(Class<?> clazz) {
    Splitter splitter = Splitter.on('.').omitEmptyStrings();
    Iterable<String> subPackages = splitter.split(clazz.getPackage().getName());

    Path current = Paths.get("");
    for (String subPackage : subPackages) {
      current = current.resolve(subPackage);
    }

    return current;
  }
}
