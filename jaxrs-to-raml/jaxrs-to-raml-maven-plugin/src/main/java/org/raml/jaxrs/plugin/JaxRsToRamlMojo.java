/*
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
package org.raml.jaxrs.plugin;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.raml.emitter.RamlEmissionException;
import org.raml.jaxrs.converter.JaxRsToRamlConversionException;
import org.raml.jaxrs.converter.RamlConfiguration;
import org.raml.jaxrs.parser.JaxRsParsingException;
import org.raml.jaxrs.raml.core.DefaultRamlConfiguration;
import org.raml.jaxrs.raml.core.OneStopShop;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Mojo(name = "jaxrstoraml", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
    defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class JaxRsToRamlMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}")
  private MavenProject project;

  @Parameter(property = "jaxrs.to.raml.input", defaultValue = "${project.build.outputDirectory}")
  private File input;

  @Parameter(property = "jaxrs.to.raml.sourceDirectory",
      defaultValue = "${project.build.sourceDirectory}")
  private File sourceDirectory;

  // defaultValue = "${project.build.directory}/generated-sources/raml-jaxrs"
  @Parameter(property = "jaxrs.to.raml.outputFileName", defaultValue = "${project.artifactId}.raml")
  private String outputFileName;

  @Parameter(property = "jaxrs.to.raml.outputDirectory",
      defaultValue = "${project.build.directory}/generated-sources/raml-jaxrs")
  private File outputDirectory;

  @Parameter(property = "jaxrs.to.raml.translatedAnnotations")
  private List<String> translatedAnnotations = new ArrayList<>();


  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    PluginConfiguration configuration = createConfiguration();
    confinedExecute(configuration, getLog());

    project.addCompileSourceRoot(outputDirectory.getPath());
  }

  private static void confinedExecute(PluginConfiguration configuration, Log logger)
      throws MojoExecutionException {
    checkConfiguration(configuration);
    printConfiguration(configuration, logger);

    Path jaxRsUrl = configuration.getInput();
    Path sourceCodeRoot = configuration.getSourceDirectory();

    configuration.getOutputDirectory().toFile().mkdirs();

    Path finalOutputFile =
        configuration.getOutputDirectory().resolve(configuration.getRamlFileName());

    String applicationName =
        FilenameUtils.removeExtension(configuration.getRamlFileName().getFileName().toString());
    RamlConfiguration ramlConfiguration =
        DefaultRamlConfiguration.forApplication(applicationName, FluentIterable.from(configuration.getTranslatedAnnotations())
            .transform(new Function<String, Class<? extends Annotation>>() {

              @Nullable
              @Override
              public Class<? extends Annotation> apply(@Nullable String input) {
                try {
                  return (Class<? extends Annotation>) Class.forName(input);
                } catch (ClassNotFoundException e) {
                  throw new IllegalArgumentException("invalid class " + input, e);
                }
              }
            }).toSet());

    OneStopShop oneStopShop =
        OneStopShop.builder()
            .withJaxRsClassesRoot(jaxRsUrl)
            .withSourceCodeRoot(sourceCodeRoot)
            .withRamlOutputFile(finalOutputFile)
            .withRamlConfiguration(ramlConfiguration).build();

    try {
      oneStopShop.parseJaxRsAndOutputRaml();
    } catch (JaxRsToRamlConversionException | JaxRsParsingException | RamlEmissionException e) {
      throw new MojoExecutionException(format("unable to generate output raml file: %s",
                                              finalOutputFile), e);
    }
  }

  private PluginConfiguration createConfiguration() {
    return PluginConfiguration.create(getInputPath(), getSourceDirectoryPath(),
                                      getOutputDirectoryPath(), getRamlFileName(), this.translatedAnnotations);
  }

  private static void printConfiguration(PluginConfiguration configuration, Log logger) {
    logger.info("Configuration");
    logger.info(format("input: %s", configuration.getInput()));
    logger.info(format("source directory: %s", configuration.getSourceDirectory()));
    logger.info(format("output directory: %s", configuration.getOutputDirectory()));
    logger.info(format("output file name: %s", configuration.getRamlFileName()));
  }

  private static void checkConfiguration(PluginConfiguration configuration)
      throws MojoExecutionException {
    checkInputFile(configuration.getInput());
  }

  private static void checkInputFile(Path inputPath) throws MojoExecutionException {
    // Check that input is an existing file, otherwise fail.
    if (!Files.isRegularFile(inputPath) && !Files.isDirectory(inputPath)) {
      throw new MojoExecutionException(format("invalid input file: %s", inputPath));
    }
  }

  private Path getInputPath() {
    return input.toPath();
  }

  private Path getSourceDirectoryPath() {
    return sourceDirectory.toPath();
  }

  public Path getOutputDirectoryPath() {
    return outputDirectory.toPath();
  }

  public Path getRamlFileName() {
    return Paths.get(outputFileName);
  }
}
