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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin

/**
 * Gradle plugin implementation class that configures all custom tasks
 * that are provided by the RAML Gradle plugin.  This class is referenced
 * by the {@code raml.properties} file in the {@code META-INF/gradle-plugins}
 * folder.
 *
 * @author Jonathan Pearlin
 * @since 1.0
 */
class JaxRsToRamlPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {
		project.logger?.info("Applying RAML JAX-RS jaxrstoraml plugin to ${project.name}...")
		// This plugin requires the Java plugin, so make sure that is is applied to the project.
		project.plugins.apply(JavaPlugin)

		// Register the custom pluginConfiguration extension so that the DSL can parse the pluginConfiguration.
		PluginConfiguration extension = project.extensions.create('jaxrstoraml', PluginConfiguration, project)
		if (!project.configurations.asMap['jaxrstoraml']) {
			project.configurations.create('jaxrstoraml')
		}

		// Create the JAX-RS code generate task and register it with the project.
		project.tasks.create(name: 'jaxrstoraml', type: RamlGeneratorTask, {
			pluginConfiguration = extension
		})
		Task generateTask = project.tasks.getByName('jaxrstoraml')
		generateTask.setGroup('Source Generation')
		generateTask.setDescription('Generates RAML from a JAX-RS project.')
	}
}
