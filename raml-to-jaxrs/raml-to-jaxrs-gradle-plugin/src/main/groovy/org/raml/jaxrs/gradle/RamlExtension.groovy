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

import org.apache.commons.io.FileUtils
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.jsonschema2pojo.AnnotationStyle

/**
 * Custom Gradle ramlExtension extension that is populated by the {@code raml}
 * ramlExtension DSL in a Gradle build script.
 *
 * @author Jonathan Pearlin
 * @since 1.0
 */
class RamlExtension {

	/**
	 * The default location to look for RAML ramlExtension files.
	 */
	static String DEFAULT_CONFIGURATION_FILES_DIRECTORY = 'src/main/raml'

	/**
	 * The default extensions to use when searching for RAML ramlExtension files.
	 */
	static String[] DEFAULT_CONFIGURATION_FILE_TYPES = ['raml', 'yaml'] as String[]

	/**
	 * The default output directory for the generated source files.
	 */
	static String DEFAULT_OUTPUT_DIRECTORY = 'generated-sources/raml-jaxrs'

	/**
	 * The JSON mapper implementation to be during source generation.  The
	 * default is {@code Jackson 1}.
	 * @see org.raml.jaxrs.generator.Configuration#jsonMapper
	 */
	String jsonMapper = AnnotationStyle.JACKSON1.name()
	
	/**
	 * Configuration parameters for JSON mapper
	 * @see org.raml.jaxrs.generator.Configuration#jsonMapperConfiguration
	 */	
	Map<String, String> jsonMapperConfiguration

	/**
	 * The output directory that will be the target of the generated source.  The default is
	 * {@code $projectBuildDir/generated-sources/raml}.
	 * @see org.raml.jaxrs.generator.Configuration#getOutputDirectory()
	 */
	File outputDirectory

	/**
	 * The path to the directory that contains RAML ramlExtension files.
	 * The default is {@code $projectRootDir/src/main/raml}.
	 */
	File sourceDirectory

	/**
	 * The set of RAML ramlExtension files that are the input to the source
	 * generation process.
	 */
	FileCollection sourcePaths

	/**
	 * Name of package containing generated model classes
	 */
	String modelPackageName = "model"

	/**
	 * Name of package containing generated model resources
	 */
	String resourcePackageName

	/**
	 * Name of package containing generated support classes
	 */
	String supportPackageName

	/**
	 * Annotate raml 1.0 types with either jackson, jaxb or gson
	 */
	String[] generateTypesWith = new String[0]

	/**
	 * The name of a generator extension class (implements org.raml.jaxrs.generator.extension.resources.GlobalResourceExtension)
	 */
	String resourceCreationExtension

	/**
	 * The name of a generator extension class (implements org.raml.jaxrs.generator.extension.resources.GlobalResourceExtension)
	 */
	String[] typeExtensions = new String[0]

	/**
	 * The name of a generator extension class (implements org.raml.jaxrs.generator.extension.resources.LegacyTypeExtension)
	 */
	String resourceFinishExtension


	/**
	 * Constructs a new ramlExtension extension for the RAML properties.
	 * @param project The Gradle {@link Project} for the currently executing build.
	 */
	RamlExtension(Project project) {
		project.logger?.info "Creating RAML JAX-RS code generation extension for project ${project.name}..."
		sourceDirectory = new File(project.getRootDir(), DEFAULT_CONFIGURATION_FILES_DIRECTORY)
		outputDirectory = new File(project.getBuildDir(), DEFAULT_OUTPUT_DIRECTORY)
	}

	/**
	 * Returns the set of RAML ramlExtension files.  This method checks the values of the
	 * following ramlExtension properties in the order listed:
	 * <br />
	 * <ol>
	 *   <li>sourceDirectory</li>
	 *   <li>sourcePath</li>
	 * </ol>
	 * <br />
	 * Note that if the {@code sourceDirectory} is not set, the default value of
	 * {@code $projectRootDir/src/main/raml} will be scanned for RAML ramlExtension files.
	 * @return The set of valid RAML ramlExtension files.
	 */
	Collection<File> getRamlFiles() {
		Set<File> ramlFiles = [] as Set

		if (sourceDirectory) {
			if(!sourceDirectory.isDirectory()) {
				throw new InvalidUserDataException("The provided path doesn't refer to a valid directory: ${sourceDirectory}")
			}

			ramlFiles.addAll(FileUtils.listFiles(sourceDirectory, DEFAULT_CONFIGURATION_FILE_TYPES, true))
		}

		if(sourcePaths) {
			sourcePaths.each { File path ->
				if(path.exists() && path.isFile()) {
					ramlFiles.add(path)
				} else {
					throw new InvalidUserDataException("The provided source file is either not a file or does not exist: ${path}")
				}
			}
		}

		return ramlFiles
	}
}
