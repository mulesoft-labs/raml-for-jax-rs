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

import com.google.common.base.Function
import com.google.common.collect.FluentIterable
import org.apache.commons.io.FilenameUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.raml.jaxrs.converter.RamlConfiguration
import org.raml.jaxrs.raml.core.DefaultRamlConfiguration
import org.raml.jaxrs.raml.core.OneStopShop

import javax.annotation.Nullable
import java.lang.annotation.Annotation
import java.nio.file.Path

/**
 * Custom Gradle task that handles the generation of Java code from RAML
 * pluginConfiguration files.  This task is automatically registered with Gradle
 * by the plugin when included in a build script.
 *
 * @author Jonathan Pearlin
 * @since 1.0
 */
class RamlGeneratorTask extends DefaultTask {

    PluginConfiguration pluginConfiguration

    @OutputDirectory
    File getOutputDirectory() {
        pluginConfiguration.getOutputDirectory()
    }

    @InputDirectory
    File getSourceDirectory() {
        return pluginConfiguration.getSourceDirectory()
    }

    @InputDirectory
    File getInputPath() {
        return pluginConfiguration.getInputPath()
    }

    @Input
    @Optional
    List<String> getTranslatedAnnotations() {
        return pluginConfiguration.getTranslatedAnnotations()
    }

    @Input
    String getRamlFileName() {

        return pluginConfiguration.getRamlFileName();
    }

    @TaskAction
    void generate() {

        File jaxRsUrl = pluginConfiguration.getInputPath()
        File sourceCodeRoot = pluginConfiguration.getSourceDirectory()

        pluginConfiguration.getOutputDirectory().mkdirs()

        Path finalOutputFile =
                pluginConfiguration.getOutputDirectory().toPath().resolve(pluginConfiguration.getRamlFileName())


        String applicationName =
                FilenameUtils.removeExtension(pluginConfiguration.getRamlFileName())

        RamlConfiguration ramlConfiguration =
                DefaultRamlConfiguration.forApplication(applicationName, FluentIterable.from(pluginConfiguration.getTranslatedAnnotations())
                        .transform(new Function<String, Class<? extends Annotation>>() {

                    @Nullable
                    @Override
                    Class<? extends Annotation> apply(@Nullable String input) {
                        try {
                            return (Class<? extends Annotation>) Class.forName(input)
                        } catch (ClassNotFoundException e) {
                            throw new IllegalArgumentException("invalid class " + input, e)
                        }
                    }
                }).toSet())

        OneStopShop oneStopShop =
                OneStopShop.builder()
                        .withJaxRsClassesRoot(jaxRsUrl.toPath())
                        .withSourceCodeRoot(sourceCodeRoot.toPath())
                        .withRamlOutputFile(finalOutputFile)
                        .withRamlConfiguration(ramlConfiguration).build()


        oneStopShop.parseJaxRsAndOutputRaml()
    }

}
