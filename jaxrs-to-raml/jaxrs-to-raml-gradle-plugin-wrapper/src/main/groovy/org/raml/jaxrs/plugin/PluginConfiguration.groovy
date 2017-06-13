/**
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
package org.raml.jaxrs.plugin

import org.gradle.api.Project

import java.nio.file.Path
import java.util.List

import static com.google.common.base.Preconditions.checkNotNull

class PluginConfiguration {

  File inputPath
  File sourceDirectory
  File outputDirectory
  String ramlFileName
  List<String> translatedAnnotations

  /**
   * Constructs a new pluginConfiguration extension for the RAML properties.
   * @param project The Gradle {@link org.gradle.api.Project} for the currently executing build.
   */
  PluginConfiguration(Project project) {
    project.logger?.info "Creating RAML file generation extension for project ${project.name}..."
  }

  List<String> getTranslatedAnnotations() {
    if ( translatedAnnotations == null ) {
      return Collections.emptyList()
    }
    return translatedAnnotations
  }
}
