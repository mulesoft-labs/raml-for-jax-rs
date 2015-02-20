/**
 * Copyright 2013-2015 (c) MuleSoft, Inc.
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
package org.raml.jaxrs.gradle

import org.gradle.api.Project
import org.jsonschema2pojo.AnnotationStyle
import org.junit.ClassRule
import org.junit.rules.TemporaryFolder
import org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion

import spock.lang.Shared
import spock.lang.Specification

class RamlExtensionSpec extends Specification {

    @Shared
    @ClassRule
    TemporaryFolder projectDirectory = new TemporaryFolder(new File('build'))

    def setupSpec() {
        new File(projectDirectory.getRoot(), RamlExtension.DEFAULT_CONFIGURATION_FILES_DIRECTORY).mkdirs()
    }

    def "test the construction of the RamlExtension configuration from a Gradle project"() {
        setup:
            Project project = Mock(Project) {
                getBuildDir() >> { new File('build') }
                getRootDir() >> { projectDirectory.getRoot() }
            }
        when:
            def extension = new RamlExtension(project)
        then:
            extension != null
            extension.basePackageName == null
            extension.jaxrsVersion == JaxrsVersion.JAXRS_1_1.alias
            extension.jsonMapper == AnnotationStyle.JACKSON1.name()
            extension.outputDirectory == new File(project.getBuildDir(), RamlExtension.DEFAULT_OUTPUT_DIRECTORY)
            extension.ramlFiles.size() == 0
            extension.useJsr303Annotations == false
    }
}