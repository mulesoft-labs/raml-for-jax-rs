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

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.api.InvalidUserDataException
import org.gradle.api.GradleException
import org.jsonschema2pojo.AnnotationStyle
import org.raml.jaxrs.generator.Configuration
import org.raml.jaxrs.generator.RamlScanner
import org.raml.jaxrs.generator.extension.resources.GlobalResourceExtension
import org.raml.jaxrs.gradle.RamlExtension

/**
 * Custom Gradle task that handles the generation of Java code from RAML
 * ramlExtension files.  This task is automatically registered with Gradle
 * by the plugin when included in a build script.
 *
 * @author Jonathan Pearlin
 * @since 1.0
 */
class CodeGeneratorTask extends DefaultTask {

    RamlExtension ramlExtension


    @Input
    AnnotationStyle getJsonMapper() {
        AnnotationStyle.valueOf(ramlExtension.getJsonMapper().toUpperCase())
    }

    @OutputDirectory
    File getOutputDirectory() {
        ramlExtension.getOutputDirectory()
    }

    @Input
    @Optional
    Map<String, String> getJsonMapperConfiguration() {
        ramlExtension.jsonMapperConfiguration
    }

    @Input
    String getModelPackageName() {
        ramlExtension.modelPackageName
    }

    @Input
    String getResourcePackageName() {
        ramlExtension.resourcePackageName
    }

    @Input
    String getSupportPackageName() {
        ramlExtension.supportPackageName
    }

    @Input
    String[] getGenerateTypesWith() {
        ramlExtension.generateTypesWith
    }

    @Input
    @Optional
    String getResourceCreationExtension() {
        ramlExtension.resourceCreationExtension
    }

    @Input
    @Optional
    String[] getTypeExtensions() {
        ramlExtension.typeExtensions
    }

    @Input
    @Optional
    String getResourceFinishExtension() {
        ramlExtension.resourceFinishExtension
    }

    @TaskAction
    void generate() {
        Configuration ramlConfiguration = new Configuration()
        try {

            ramlConfiguration.setModelPackage modelPackageName
            ramlConfiguration.setResourcePackage resourcePackageName
            ramlConfiguration.setSupportPackage supportPackageName
            ramlConfiguration.setOutputDirectory outputDirectory
            ramlConfiguration.setJsonMapper jsonMapper
            ramlConfiguration.setJsonMapperConfiguration jsonMapperConfiguration
            ramlConfiguration.setTypeConfiguration generateTypesWith

            if (resourceCreationExtension != null) {

                Class<GlobalResourceExtension> c = Class.forName(resourceCreationExtension) as Class<GlobalResourceExtension>
                ramlConfiguration.defaultResourceCreationExtension(c)

            }

            if (resourceFinishExtension != null) {

                Class<GlobalResourceExtension> c = Class.forName(resourceFinishExtension) as Class<GlobalResourceExtension>
                ramlConfiguration.defaultResourceFinishExtension(c)
            }

/*
            if (getTypeExtensions() != null) {
                typeExtensions.each { String className ->
                    Class<LegacyTypeExtension> c = Class.forName(className) as Class<LegacyTypeExtension>
                    ramlConfiguration.typeExtensions.add c.newInstance() as LegacyTypeExtension
                }
            }
*/

        } catch (final Exception e) {
            throw new InvalidUserDataException("Failed to configure plug-in", e)
        }


        File currentSourcePath = null

        try {

            RamlScanner scanner = new RamlScanner(ramlConfiguration)
            ramlExtension.ramlFiles.each { File ramlFile ->
                scanner.handle(ramlFile)
            }
        } catch (final Exception e) {
            throw new GradleException("Error generating Java classes from: " + currentSourcePath, e)
        }
    }

}
