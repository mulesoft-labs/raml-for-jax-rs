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
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import spock.lang.Specification

class RamlPluginSpec extends Specification {

    @Rule TemporaryFolder outputDirectory = new TemporaryFolder(new File('build'))
    @Rule TemporaryFolder projectDirectory = new TemporaryFolder(new File('build'))

    Project project

    def setup() {
        new File(projectDirectory.root, 'src/main/raml').mkdirs()
        project = ProjectBuilder.builder().withProjectDir(projectDirectory.root).build()
    }

    def "test applying the plugin changes to the Gradle project"() {
        when:
            project.plugins.apply(RamlPlugin)
        then:
            project.tasks.getByName('raml-generate') != null
    }

    def "test applying the plugin changes to the Gradle project with an existing 'raml' configuration"() {
        setup:
            project.configurations.create('raml')
        when:
            project.plugins.apply(RamlPlugin)
        then:
            project.tasks.getByName('raml-generate') != null
    }
}