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
package org.raml.jaxrs.codegen.maven;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jsonschema2pojo.AnnotationStyle;
import org.raml.jaxrs.generator.Configuration;
import org.raml.jaxrs.generator.RamlScanner;
import org.raml.jaxrs.generator.extension.resources.GlobalResourceExtension;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

@Mojo(name = "generate", requiresProject = true, threadSafe = false, requiresDependencyResolution = COMPILE_PLUS_RUNTIME,
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
      defaultValue = "${project.build.directory}/generated-sources/raml-to-jaxrs-maven-plugin")
  private File outputDirectory;

  /**
   * An array of locations of the RAML file(s).
   */
  @Parameter(property = "ramlFile", required = true)
  private File ramlFile;

  @Parameter(property = "includes", required = false)
  private String[] includes;

  @Parameter(property = "excludes", required = false)
  private String[] excludes;

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
  @Parameter(property = "generateTypesWith")
  private String[] generateTypesWith;

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
      configuration.setTypeConfiguration(generateTypesWith);
      if (resourceCreationExtension != null) {

        Class<GlobalResourceExtension> c = (Class<GlobalResourceExtension>) Class.forName(resourceCreationExtension);
        configuration.defaultResourceCreationExtension(c);

      }

      if (resourceFinishExtension != null) {

        Class<GlobalResourceExtension> c = (Class<GlobalResourceExtension>) Class.forName(resourceFinishExtension);
        configuration.defaultResourceFinishExtension(c);
      }

    } catch (final Exception e) {
      throw new MojoExecutionException("Failed to configure plug-in", e);
    }

    project.addCompileSourceRoot(outputDirectory.getPath());

    File currentSourcePath = null;

    try {
      final RamlScanner scanner = new RamlScanner(configuration);
      if (ramlFile.isDirectory()) {
        final MatchPatternsFileFilter filter =
            new MatchPatternsFileFilter.Builder().addIncludes(includes).addExcludes(excludes).addDefaultExcludes()
                .withSourceDirectory(ramlFile.getCanonicalPath()).withCaseSensitive(false).build();
        Files.walkFileTree(ramlFile.toPath(), new PathFileVisitor(scanner, filter));
      } else {
        runFile(scanner, ramlFile);
      }
      getLog().info("Files generated in " + outputDirectory.getAbsolutePath());
    } catch (final Exception e) {
      throw new MojoExecutionException("Error generating Java classes from: " + currentSourcePath, e);
    }
  }

  private void runFile(RamlScanner scanner, File ramlFile) throws IOException, DependencyResolutionRequiredException {

    ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    ClassLoader newClassLoader = getClassLoader(project, oldClassLoader, getLog(), ramlFile.getParentFile().getAbsoluteFile());
    try {
      Thread.currentThread().setContextClassLoader(newClassLoader);
      scanner.handle(ramlFile);
    } finally {
      Thread.currentThread().setContextClassLoader(oldClassLoader);
    }
  }

  private ClassLoader getClassLoader(MavenProject project, final ClassLoader parent, Log log, File file)
      throws DependencyResolutionRequiredException, MalformedURLException {

    @SuppressWarnings("unchecked")
    List<String> classpathElements = project.getCompileClasspathElements();

    final List<URL> classpathUrls = new ArrayList<>(classpathElements.size());
    classpathUrls.add(file.toURL());
    for (String classpathElement : classpathElements) {

      try {
        log.debug("Adding project artifact to classpath: " + classpathElement);
        classpathUrls.add(new File(classpathElement).toURI().toURL());
      } catch (MalformedURLException e) {
        log.debug("Unable to use classpath entry as it could not be understood as a valid URL: " + classpathElement, e);
      }
    }

    return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {

      @Override
      public ClassLoader run() {
        return new URLClassLoader(classpathUrls.toArray(new URL[classpathUrls.size()]), parent);
      }
    });

  }

  private class PathFileVisitor implements FileVisitor<Path> {

    private final RamlScanner scanner;
    private final MatchPatternsFileFilter filter;

    public PathFileVisitor(RamlScanner scanner, MatchPatternsFileFilter filter) {
      this.scanner = scanner;
      this.filter = filter;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

      if (filter.accept(file.toFile())) {

        try {
          runFile(scanner, file.toFile());
        } catch (DependencyResolutionRequiredException e) {
          throw new IOException(e);
        }
      }

      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
      return FileVisitResult.CONTINUE;
    }
  }
}
