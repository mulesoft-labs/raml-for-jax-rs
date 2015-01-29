/*
 * Copyright 2013 (c) MuleSoft, Inc.
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
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jsonschema2pojo.AnnotationStyle
import org.raml.jaxrs.codegen.core.Configuration
import org.raml.jaxrs.codegen.core.Generator
import org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion
import org.raml.jaxrs.gradle.RamlExtension

/**
 * Custom Gradle task that handles the generation of Java code from RAML
 * configuration files.  This task is automatically registered with Gradle
 * by the plugin when included in a build script.
 *
 * @author Jonathan Pearlin
 * @since 1.0
 */
class CodeGeneratorTask extends DefaultTask {

	Generator generator = new Generator()

	RamlExtension configuration

	@Input
	String getBasePackageName() {
		configuration.getBasePackageName()
	}

	@InputFiles
	Collection<File> getRamlFiles() {
		configuration.getRamlFiles()
	}

	@Input
	JaxrsVersion getJaxrsVersion() {
		JaxrsVersion.fromAlias(configuration.getJaxrsVersion())
	}

	@Input
	AnnotationStyle getJsonMapper() {
		AnnotationStyle.valueOf(configuration.getJsonMapper().toUpperCase())
	}

	@OutputDirectory
	File getOutputDirectory() {
		configuration.getOutputDirectory()
	}

	@Input
	boolean useJsr303Annotations() {
		configuration.useJsr303Annotations
	}

	@TaskAction
	void generate() {
		Configuration ramlConfiguration = new Configuration()
		ramlConfiguration.setBasePackageName(getBasePackageName())
		ramlConfiguration.setJaxrsVersion(getJaxrsVersion())
		ramlConfiguration.setJsonMapper(getJsonMapper())
		ramlConfiguration.setOutputDirectory(getOutputDirectory())
		ramlConfiguration.setUseJsr303Annotations(useJsr303Annotations())

		getRamlFiles().each { configurationFile ->
			generator.run(new FileReader(configurationFile), ramlConfiguration)
		}
	}
}