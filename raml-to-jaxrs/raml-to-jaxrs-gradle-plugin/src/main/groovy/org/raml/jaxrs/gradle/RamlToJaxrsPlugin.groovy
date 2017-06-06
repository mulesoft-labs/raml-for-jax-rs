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
package org.raml.jaxrs.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.raml.jaxrs.gradle.codegen.CodeGeneratorTask

/**
 * Gradle plugin implementation class that configures all custom tasks
 * that are provided by the RAML Gradle plugin.  This class is referenced
 * by the {@code ramltojaxrs.properties} file in the {@code META-INF/gradle-plugins}
 * folder.
 *
 * @author Jonathan Pearlin
 * @since 1.0
 */
class RamlToJaxrsPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.logger?.info("Applying RAML JAX-RS codegen plugin to ${project.name}...")
		// This plugin requires the Java plugin, so make sure that is is applied to the project.
		project.plugins.apply(JavaPlugin)

		// Register the custom ramlExtension extension so that the DSL can parse the ramlExtension.
		RamlExtension extension = project.extensions.create('ramltojaxrs', RamlExtension, project)
		if (!project.configurations.asMap['ramltojaxrs']) {
			project.configurations.create('ramltojaxrs')
		}

		// Create the JAX-RS code generate task and register it with the project.
		project.tasks.create(name: 'ramltojaxrs', type: CodeGeneratorTask, {
			ramlExtension = extension
		})
		Task generateTask = project.tasks.getByName('ramltojaxrs')
		generateTask.setGroup('Source Generation')
		generateTask.setDescription('Generates JAX-RS annotated Java classes from the provided RAML ramlExtension file(s).')
	}
}
