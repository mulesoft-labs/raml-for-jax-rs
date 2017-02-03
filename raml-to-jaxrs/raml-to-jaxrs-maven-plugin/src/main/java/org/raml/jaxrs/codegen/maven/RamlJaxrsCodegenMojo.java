/*
 * Copyright ${licenseYear} (c) MuleSoft, Inc.
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
import org.jsonschema2pojo.AnnotationStyle;
import org.raml.jaxrs.generator.Configuration;
import org.raml.jaxrs.generator.RamlScanner;
import org.raml.jaxrs.generator.builders.extensions.resources.TrialResourceClassExtension;
import org.raml.jaxrs.generator.extension.resources.GlobalResourceExtension;
import org.raml.jaxrs.generator.extension.types.TypeExtension;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

@Mojo(name = "generate", requiresProject = true, threadSafe = false,
    requiresDependencyResolution = COMPILE_PLUS_RUNTIME,
    defaultPhase = LifecyclePhase.GENERATE_SOURCES)
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
  @Parameter(property = "outputDirectory",
      defaultValue = "${project.build.directory}/generated-sources/raml-jaxrs")
  private File outputDirectory;

  /**
   * An array of locations of the RAML file(s).
   */
  @Parameter(property = "ramlFile", required = true)
  private File ramlFile;


  /**
   * Resource package name used for generated Java classes.
   */
  @Parameter(property = "resourcePackage", required = true)
  private String resourcePackage;

  /**
   * Model package name used for generated Java classes.
   */
  @Parameter(property = "modelPackage")
  private String modelPackage;

  /**
   * Model package name used for generated Java classes. Models will be placed in the package:
   */
  @Parameter(property = "supportPackage")
  private String supportPackage;


  /**
   * Whether to empty the output directory before generation occurs, to clear out all source files that have been generated
   * previously.
   */
  @Parameter(property = "removeOldOutput", defaultValue = "false")
  private boolean removeOldOutput;


  /**
   * Annotate raml 1.0 types with either jackson, jaxb or gson
   */
  @Parameter(property = "configureTypesFor")
  private String[] configureTypesFor;

  /**
   * The JSON object mapper to generate annotations to: either "jackson1", "jackson2" or "gson" or "none"
   */
  @Parameter(property = "jsonMapper", defaultValue = "jackson2")
  private String jsonMapper;

  /**
   * Optional extra configuration provided to the JSON mapper. Supported keys are: "generateBuilders", "includeHashcodeAndEquals",
   * "includeToString", "useLongIntegers"
   */
  @Parameter(property = "jsonMapperConfiguration")
  private Map<String, String> jsonMapperConfiguration;

  /**
   * The name of a generator extension class (implements org.raml.jaxrs.generator.extension.resources.GlobalResourceExtension)
   */
  @Parameter(property = "resourceCreationExtension")
  private String resourceCreationExtension;

  /**
   * The name of a generator extension class (implements org.raml.jaxrs.generator.extension.resources.GlobalResourceExtension)
   */
  @Parameter(property = "resourceCreationExtension")
  private String resourceFinishExtension;

  /**
   * The name of a generator extension class (implements org.raml.jaxrs.codegen.core.ext.GeneratorExtension)
   */
  @Parameter(property = "typeExtensions")
  private String[] typeExtensions;


  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (skip) {
      getLog().info("Skipping execution...");
      return;
    }

    if (ramlFile == null) {
      throw new MojoExecutionException("ramlFile is not defined");
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
      configuration.setModelPackage(modelPackage);
      configuration.setResourcePackage(resourcePackage);
      configuration.setSupportPackage(supportPackage);
      configuration.setOutputDirectory(outputDirectory);
      configuration.setJsonMapper(AnnotationStyle.valueOf(jsonMapper.toUpperCase()));
      configuration.setJsonMapperConfiguration(jsonMapperConfiguration);
      configuration.setTypeConfiguration(configureTypesFor);
      if (resourceCreationExtension != null) {

        Class<GlobalResourceExtension> c =
            (Class<GlobalResourceExtension>) Class.forName(resourceCreationExtension);
        configuration.defaultResourceCreationExtension(c);

      }

      if (resourceFinishExtension != null) {

        Class<GlobalResourceExtension> c =
            (Class<GlobalResourceExtension>) Class.forName(resourceCreationExtension);
        configuration.defaultResourceFinishExtension(c);
      }

      if (typeExtensions != null) {
        for (String className : typeExtensions) {
          Class c = Class.forName(className);
          if (c == null) {
            throw new MojoExecutionException("typeExtension " + className + " cannot be loaded."
                + "Have you installed the correct dependency in the plugin configuration?");
          }
          if (!((c.newInstance()) instanceof TypeExtension)) {
            throw new MojoExecutionException("typeExtension " + className + " does not implement"
                + TrialResourceClassExtension.class.getPackage() + "."
                + TrialResourceClassExtension.class.getName());

          }

          configuration.getTypeExtensions().add((TypeExtension) c.newInstance());
        }
      }

    } catch (final Exception e) {
      throw new MojoExecutionException("Failed to configure plug-in", e);
    }


    project.addCompileSourceRoot(outputDirectory.getPath());

    File currentSourcePath = null;

    try {

      RamlScanner scanner = new RamlScanner(configuration);
      scanner.handle(ramlFile);
    } catch (final Exception e) {
      throw new MojoExecutionException("Error generating Java classes from: " + currentSourcePath,
                                       e);
    }
  }
}
