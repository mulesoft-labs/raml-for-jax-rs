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
package org.raml.jaxrs.gradle.codegen

import org.gradle.api.tasks.TaskExecutionException
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
import org.raml.jaxrs.codegen.core.ext.GeneratorExtension

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
	
	@Input
	Map<String, String> getJsonMapperConfiguration(){
		configuration.jsonMapperConfiguration
	}
	
	@Input
	String getModelPackageName(){
		configuration.modelPackageName
	}
	
	@Input
    Class getMethodThrowException(){
    	configuration.methodThrowException
    }
    
    @Input
    String getAsyncResourceTrait(){
    	configuration.asyncResourceTrait
    }
    
    @Input
	boolean isEmptyResponseReturnVoid(){
		configuration.emptyResponseReturnVoid
    }
	
	@Input
	boolean isGenerateClientInterface(){
		configuration.generateClientInterface
    }
	
	@Input
    Class getCustomAnnotator(){
    	configuration.customAnnotator
    }
    
    @Input
    ArrayList<String> getIgnoredParameterNames(){
    	configuration.ignoredParameterNames
    }
    
    @Input
    boolean isUseTitlePropertyWhenPossible(){
    	configuration.useTitlePropertyWhenPossible
    }
    
    @Input
	List<String> getGeneratorExtensions(){
		configuration.extensions
	}

	@TaskAction
	void generate() {
		Configuration ramlConfiguration = new Configuration()
		ramlConfiguration.setBasePackageName(getBasePackageName())
		ramlConfiguration.setJaxrsVersion(getJaxrsVersion())
		ramlConfiguration.setJsonMapper(getJsonMapper())
		ramlConfiguration.setOutputDirectory(getOutputDirectory())
		ramlConfiguration.setUseJsr303Annotations(useJsr303Annotations())
		ramlConfiguration.setJsonMapperConfiguration(getJsonMapperConfiguration())

		ramlConfiguration.setModelPackageName(getModelPackageName())
	    ramlConfiguration.setMethodThrowException(getMethodThrowException())
	    ramlConfiguration.setAsyncResourceTrait(getAsyncResourceTrait())
		ramlConfiguration.setEmptyResponseReturnVoid(isEmptyResponseReturnVoid())
		ramlConfiguration.setGenerateClientInterface(isGenerateClientInterface())
	    ramlConfiguration.setCustomAnnotator(getCustomAnnotator())
	    ramlConfiguration.setIgnoredParameterNames(getIgnoredParameterNames())
	    ramlConfiguration.setUseTitlePropertyWhenPossible(isUseTitlePropertyWhenPossible())
		
		if (getGeneratorExtensions() != null) {
			for (String className : getGeneratorExtensions()) {
				Class c = Class.forName(className);
				if (c == null) {
					throw new TaskExecutionException("generatorExtensionClass " + className
							+ " cannot be loaded."
							+ "Have you installed the correct dependency in the plugin configuration?");
				}
				if (!((c.newInstance()) instanceof GeneratorExtension)) {
					throw new TaskExecutionException("generatorExtensionClass " + className
							+ " does not implement" + GeneratorExtension.class.getPackage() + "."
							+ GeneratorExtension.class.getName());

				}
				ramlConfiguration.getExtensions().add((GeneratorExtension) c.newInstance());


			}
		}

		getRamlFiles().each { configurationFile ->
			ramlConfiguration.sourceDirectory = configurationFile.parentFile
			generator.run(new FileReader(configurationFile), ramlConfiguration, configurationFile.absolutePath)
		}
	}
}