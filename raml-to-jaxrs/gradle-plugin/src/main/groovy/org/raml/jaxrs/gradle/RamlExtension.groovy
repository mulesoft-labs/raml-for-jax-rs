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

import org.apache.commons.io.FileUtils
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.jsonschema2pojo.AnnotationStyle
import org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion
import org.raml.jaxrs.codegen.core.ext.GeneratorExtension
import org.jsonschema2pojo.NoopAnnotator

/**
 * Custom Gradle configuration extension that is populated by the {@code raml}
 * configuration DSL in a Gradle build script.
 *
 * @author Jonathan Pearlin
 * @since 1.0
 */
class RamlExtension {

	/**
	 * The default location to look for RAML configuration files.
	 */
	static String DEFAULT_CONFIGURATION_FILES_DIRECTORY = 'src/main/raml'

	/**
	 * The default extensions to use when searching for RAML configuration files.
	 */
	static String[] DEFAULT_CONFIGURATION_FILE_TYPES = ['raml', 'yaml'] as String[]

	/**
	 * The default output directory for the generated source files.
	 */
	static String DEFAULT_OUTPUT_DIRECTORY = 'generated-sources/raml-jaxrs'

	/**
	 * The base package name of the generated source.
	 * @see org.raml.jaxrs.codegen.core.Configuration#getBasePackageName()
	 */
	String basePackageName

	/**
	 * The target JAX-RS version for the generated source.  The default is
	 * {@code JAX-RS 1.1}.
	 * @see org.raml.jaxrs.codegen.core.Configuration#getJaxrsVersion()
	 */
	String jaxrsVersion = JaxrsVersion.JAXRS_1_1.alias

	/**
	 * The JSON mapper implementation to be during source generation.  The
	 * default is {@code Jackson 1}.
	 * @see org.raml.jaxrs.codegen.core.Configuration#getJsonMapper()
	 */
	String jsonMapper = AnnotationStyle.JACKSON1.name()
	
	/**
	 * Configuration parameters for JSON mapper
	 * @see org.raml.jaxrs.codegen.core.Configuration#getJsonMapperConfiguration()
	 */	
	Map<String, String> jsonMapperConfiguration

	/**
	 * The output directory that will be the target of the generated source.  The default is
	 * {@code $projectBuildDir/generated-sources/raml}.
	 * @see org.raml.jaxrs.codegen.core.Configuration#getOutputDirectory()
	 */
	File outputDirectory

	/**
	 * The path to the directory that contains RAML configuration files.
	 * The default is {@code $projectRootDir/src/main/raml}.
	 */
	File sourceDirectory

	/**
	 * The set of RAML configuration files that are the input to the source
	 * generation process.
	 */
	FileCollection sourcePaths

	/**
	 * Determines whether or not JSR-303 annotations should be used in the generated source.
	 * Defaults to {@code false}.
	 * @see org.raml.jaxrs.codegen.core.Configuration#isUseJsr303Annotations()
	 */
	boolean useJsr303Annotations = false
	
	/**
	 * Name of package containing model classes
	 * @see org.raml.jaxrs.codegen.core.Configuration#isUseJsr303Annotations()
	 */
	String modelPackageName = "model";
	
    Class methodThrowException = Exception.class;
    
    String asyncResourceTrait;
    
	boolean emptyResponseReturnVoid;
	
	boolean generateClientInterface;
	
    Class customAnnotator = NoopAnnotator.class;
    
    ArrayList<String>ignoredParameterNames=new ArrayList<String>();
    
    boolean useTitlePropertyWhenPossible;
    
	List<String> extensions = new ArrayList<String>();

	/**
	 * Constructs a new configuration extension for the RAML properties.
	 * @param project The Gradle {@link Project} for the currently executing build.
	 */
	RamlExtension(Project project) {
		project.logger?.info "Creating RAML JAX-RS code generation extension for project ${project.name}..."
		sourceDirectory = new File(project.getRootDir(), DEFAULT_CONFIGURATION_FILES_DIRECTORY)
		outputDirectory = new File(project.getBuildDir(), DEFAULT_OUTPUT_DIRECTORY)
	}

	/**
	 * Returns the set of RAML configuration files.  This method checks the values of the
	 * following configuration properties in the order listed:
	 * <br />
	 * <ol>
	 *   <li>sourceDirectory</li>
	 *   <li>sourcePath</li>
	 * </ol>
	 * <br />
	 * Note that if the {@code sourceDirectory} is not set, the default value of
	 * {@code $projectRootDir/src/main/raml} will be scanned for RAML configuration files.
	 * @return The set of valid RAML configuration files.
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

		ramlFiles
	}
}