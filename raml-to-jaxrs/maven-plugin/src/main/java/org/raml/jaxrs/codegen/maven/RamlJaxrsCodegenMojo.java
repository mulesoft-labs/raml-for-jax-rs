/*
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
package org.raml.jaxrs.codegen.maven;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.jsonschema2pojo.AnnotationStyle;
import org.raml.jaxrs.codegen.core.Configuration;
import org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion;
import org.raml.jaxrs.codegen.core.GeneratorProxy;
import org.raml.jaxrs.codegen.core.Names;
import org.raml.jaxrs.codegen.core.ext.GeneratorExtension;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

/**
 * When invoked, this goals read one or more <a href="http://raml.org">RAML</a>
 * files and produces JAX-RS annotated Java classes.
 *
 * @author kor
 * @version $Id: $Id
 */
@Mojo(name = "generate", requiresProject = true, threadSafe = false, requiresDependencyResolution = COMPILE_PLUS_RUNTIME, defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class RamlJaxrsCodegenMojo extends AbstractMojo {
	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	/**
	 * Skip plug-in execution.
	 */
	@Parameter(property = "skip", defaultValue = "false")
	private boolean skip;

	/**
	 * Target directory for generated Java source files.
	 */
	@Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/generated-sources/raml-jaxrs")
	private File outputDirectory;

	/**
	 * An array of locations of the RAML file(s).
	 */
	@Parameter(property = "sourcePaths")
	private File[] sourcePaths;

	/**
	 * Directory location of the RAML file(s).
	 */
	@Parameter(property = "sourceDirectory", defaultValue = "${basedir}/src/main/raml")
	private File sourceDirectory;
	
	
	/**
	 * Should generator add the source directory hierarchy to the base package name?
	 * Only applies if using sourceDirectory
	 */
	 @Parameter(property = "useSourceHierarchyInPackageName", defaultValue = "false")
	 private boolean useSourceHierarchyInPackageName;

	/**
	 * The targeted JAX-RS version: either "1.1" or "2.0" .
	 */
	@Parameter(property = "jaxrsVersion", defaultValue = "1.1")
	private String jaxrsVersion;

	/**
	 * Base package name used for generated Java classes.
	 */
	@Parameter(property = "basePackageName", required = true)
	private String basePackageName;

	/**
	 * Model package name used for generated Java classes.
	 * Models will be placed in the package:
	 *
	 *   basePackageName + "." + modelPackageName
	 *
	 * The default location is set to:
	 *
	 *   basePackageName + ".model"
	 */
	@Parameter(property = "modelPackageName", defaultValue = "model")
	private String modelPackageName;

	/**
	 * Should JSR-303 annotations be used?
	 */
	@Parameter(property = "useJsr303Annotations", defaultValue = "false")
	private boolean useJsr303Annotations;

	/**
	 * Should client proxy code be generated
	 */
	@Parameter(property = "generateClientProxy", defaultValue = "false")
	private boolean generateClientProxy;

	/**
	 * The targeted JAX-RS version: either "1.1" or "2.0" .
	 */
	@Parameter(property = "mapToVoid", defaultValue = "false")
	private boolean mapToVoid;

	/**
	 * Whether to empty the output directory before generation occurs, to clear
	 * out all source files that have been generated previously.
	 */
	@Parameter(property = "removeOldOutput", defaultValue = "false")
	private boolean removeOldOutput;

	/**
	 * If set to true generator will try to use title property of JSON schemas
	 * as a name for Java class
	 *
	 */
	@Parameter(property = "useTitlePropertyForSchemaNames", defaultValue = "false")
	private boolean useTitlePropertyForSchemaNames=false;
	/**
	 * The JSON object mapper to generate annotations to: either "jackson1",
	 * "jackson2" or "gson" or "none"
	 */
	@Parameter(property = "jsonMapper", defaultValue = "jackson1")
	private String jsonMapper;

	@Parameter(property = "asyncResourceTrait")
	private String asyncResourceTrait;
	/**
	 * Optional extra configuration provided to the JSON mapper. Supported keys
	 * are: "generateBuilders", "includeHashcodeAndEquals", "includeToString",
	 * "useLongIntegers"
	 */
	@Parameter(property = "jsonMapperConfiguration")
	private Map<String, String> jsonMapperConfiguration;

	/**
	 * The name of a generator extension class (implements
	 * org.raml.jaxrs.codegen.core.ext.GeneratorExtension)
	 */
	@Parameter(property = "extensions")
	private String[] extensions;

	/**
	 * The names of parameters to ignore
	 */
	@Parameter(property = "ignoredParameters")
	private String[] ignoredParameters;

	@Parameter(property ="customAnnotator",defaultValue = "")
	private String customAnnotator;
	/**
	 * {@inheritDoc}
	 *
	 * Throw exception on Resource Method
	 */
	// @Parameter(property = "methodThrowException")
	// private String methodThrowException;
	
	

	public void setSourceDirectory(File sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public void setUseSourceHiearchyInPackageName(boolean useSourceHierarchyInPackageName) {
		this.useSourceHierarchyInPackageName = useSourceHierarchyInPackageName;
	}

	public void setBasePackageName(String basePackageName) {
		this.basePackageName = basePackageName;
	}	
	
	
	
	
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skip) {
			getLog().info("Skipping execution...");
			return;
		}

		if ((sourceDirectory == null) && (sourcePaths == null)) {
			throw new MojoExecutionException("One of sourceDirectory or sourcePaths must be provided");
		}

		try {
			FileUtils.forceMkdir(outputDirectory);
		} catch (final IOException ioe) {
			throw new MojoExecutionException("Failed to create directory: " + outputDirectory, ioe);
		}

		if (removeOldOutput) {
			try {
				FileUtils.cleanDirectory(outputDirectory);
			} catch (final IOException ioe) {
				throw new MojoExecutionException("Failed to clean directory: " + outputDirectory, ioe);
			}
		}

		final Configuration configuration = new Configuration();

		try {
			configuration.setUseTitlePropertyWhenPossible(useTitlePropertyForSchemaNames);
			configuration.setBasePackageName(basePackageName);
			configuration.setModelPackageName(modelPackageName);
			configuration.setJaxrsVersion(JaxrsVersion.fromAlias(jaxrsVersion));
			configuration.setOutputDirectory(outputDirectory);
			configuration.setUseJsr303Annotations(useJsr303Annotations);
			configuration.setAsyncResourceTrait(asyncResourceTrait);
			configuration.setGenerateClientInterface(generateClientProxy);
            configuration.setJsonMapper(AnnotationStyle.valueOf(jsonMapper.toUpperCase()));
			configuration.setSourceDirectory(sourceDirectory);
			configuration.setJsonMapperConfiguration(jsonMapperConfiguration);
			configuration.setEmptyResponseReturnVoid(mapToVoid);
			if(ignoredParameters!=null){
				for (String s:ignoredParameters){
					configuration.getIgnoredParameterNames().add(s);
				}
			}
			if(this.customAnnotator!=null&& StringUtils.isNotEmpty(customAnnotator)){
				configuration.setCustomAnnotator((Class)Class.forName(customAnnotator));
			}

			if (extensions != null) {
				for (String className : extensions) {
					Class c = Class.forName(className);
					if (c == null) {
						throw new MojoExecutionException("generatorExtensionClass " + className
								+ " cannot be loaded."
								+ "Have you installed the correct dependency in the plugin configuration?");
					}
					if (!((c.newInstance()) instanceof GeneratorExtension)) {
						throw new MojoExecutionException("generatorExtensionClass " + className
								+ " does not implement" + GeneratorExtension.class.getPackage() + "."
								+ GeneratorExtension.class.getName());

					}
					configuration.getExtensions().add((GeneratorExtension) c.newInstance());


				}
			}
			/*
			 * if (methodThrowException != null) {
			 * configuration.setMethodThrowException
			 * (Class.forName(methodThrowException)); }
			 */
		} catch (final Exception e) {
			throw new MojoExecutionException("Failed to configure plug-in", e);
		}

		project.addCompileSourceRoot(outputDirectory.getPath());

		File currentSourcePath = null;

		try {
			final GeneratorProxy generator = new GeneratorProxy();

			for (final File ramlFile : getRamlFiles()) {
				currentSourcePath = ramlFile;
				//lets test if it is root raml
				BufferedReader reader=new BufferedReader(new FileReader(ramlFile));
				String line=reader.readLine();
				reader.close();
				if(line.startsWith("#%RAML")) {
					getLog().info("Generating Java classes from: " + ramlFile);
					configuration.setBasePackageName(basePackageName.concat(computeSubPackageName(ramlFile)));
					configuration.setSourceDirectory(ramlFile.getParentFile());
					generator.run(new FileReader(ramlFile), configuration, ramlFile.getAbsolutePath());
				}
				else{
					getLog().info(ramlFile+" does not seem to be RAML root file -skipped(first line should start from #%RAML ${raml version number}");

				}
			}
		} catch (final Exception e) {
			throw new MojoExecutionException("Error generating Java classes from: " + currentSourcePath, e);
		}
	}


	private Collection<File> getRamlFiles() throws MojoExecutionException {
		if (sourcePaths != null && sourcePaths.length > 0) {
			final List<File> sourceFiles = Arrays.asList(sourcePaths);
			getLog().info("Using RAML files: " + sourceFiles);
			return sourceFiles;
		} else {
			if (!sourceDirectory.isDirectory()) {
				throw new MojoExecutionException("The provided path doesn't refer to a valid directory: "
						+ sourceDirectory);
			}

			getLog().info("Looking for RAML files in and below: " + sourceDirectory);

			return FileUtils.listFiles(sourceDirectory, new String[] { "raml", "yaml" }, true);
		}
	}
	
	
	/**
	 * If useSourceHierarchy is true and sourceDirectory is set, return a sub package name that reflects the
	 * relative position of the ramlFile to sourceDirectory.  Otherwise return an empty string.
	 * 
	 * @param ramlFile
	 * @return computed sub package name
	 */
	protected String computeSubPackageName(File ramlFile) {
		String subName = "";
		if (useSourceHierarchyInPackageName && sourceDirectory != null) {
			Path rel = sourceDirectory.toPath().relativize(ramlFile.toPath());
			if (rel != null && rel.getParent() != null) {
				String parent = rel.getParent().toString();
				String rpn = parent.replace(File.separator, ".");
				if (StringUtils.isNotBlank(rpn)) {
					subName = ".".concat(Names.buildVariableName(rpn).toLowerCase());
				}
			}
		}
		return subName;
	}

	
}
