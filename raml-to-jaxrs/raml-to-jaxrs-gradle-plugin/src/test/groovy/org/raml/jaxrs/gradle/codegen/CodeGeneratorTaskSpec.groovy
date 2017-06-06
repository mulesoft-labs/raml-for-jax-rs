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
package org.raml.jaxrs.gradle.codegen

import org.gradle.api.Project
import org.gradle.api.internal.file.collections.SimpleFileCollection
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.raml.jaxrs.gradle.RamlExtension
import org.raml.jaxrs.gradle.RamlToJaxrsPlugin
import spock.lang.Specification

class CodeGeneratorTaskSpec extends Specification {

    @Rule TemporaryFolder outputDirectory = new TemporaryFolder(new File('build'))
    @Rule TemporaryFolder projectDirectory = new TemporaryFolder(new File('build'))

    Project project

    def setup() {
        new File(projectDirectory.root, 'src/main/raml').mkdirs()
        project = ProjectBuilder.builder().withProjectDir(projectDirectory.root).build()
        project.plugins.apply(RamlToJaxrsPlugin)
    }

    def "test the generation of JAX-RS annotated resources from a .raml file"() {
        setup:
            File ramlConfigFile = new File(getClass().getResource('/test.raml').toURI())
            RamlExtension configuration = new RamlExtension(project)
            configuration.basePackageName = 'org.raml.test'
            configuration.supportPackageName = 'org.raml.test.gen'
            configuration.resourcePackageName = 'org.raml.test.res'
            configuration.sourcePaths = new SimpleFileCollection([ramlConfigFile])
            configuration.outputDirectory = outputDirectory.root
            configuration.generateTypeWith = new String[0]
            CodeGeneratorTask generatorTask = project.getTasksByName('raml-generate', false).iterator()[0]
//            generatorTask.generator = Mock(Generator)
        when:
            generatorTask.ramlExtension = configuration
            generatorTask.generate()
        then:
            true
//            1 * generatorTask.run(_,_,_)
    }
}
