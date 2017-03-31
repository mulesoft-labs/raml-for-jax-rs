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
package org.raml.jaxrs.codegen.core;

import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.compilers.JavaCompilerSettings;
import org.apache.commons.jci.stores.FileResourceStore;
import org.apache.commons.lang3.ArrayUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.internal.scanning.FilesScanner;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractGeneratorTestCase {
    private static final String TEST_BASE_PACKAGE = "org.raml.jaxrs.test";
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGenerator.class);

    @Rule
    public TemporaryFolder codegenOutputFolder = new TemporaryFolder();

    @Rule
    public TemporaryFolder compilationOutputFolder = new TemporaryFolder();

    Set<String> generatedSources;

    @Before
    public void initGeneratedSources() throws Exception {
        generatedSources = new HashSet<String>();
    }

    protected Configuration createConfigurationForSourceDirectory(String sourceDirectory) {
        final Configuration configuration = new Configuration();
        configuration.setOutputDirectory(codegenOutputFolder.getRoot());

        configuration.setBasePackageName(TEST_BASE_PACKAGE);
        String dirPath = getClass().getResource(sourceDirectory).getPath();
        configuration.setSourceDirectory(new File(dirPath));
        return configuration;
    }

    protected void generateSourceAndAddToSet(final Configuration configuration, String ramlPath) throws Exception {
        Set<String> generatedSources = new Generator().run(
                new InputStreamReader(getClass().getResourceAsStream(ramlPath)),
                configuration);
        for (String source : generatedSources) {
            LOGGER.info("Generated {}{}{}", codegenOutputFolder.getRoot(), File.separator, source);
        }
        this.generatedSources.addAll(generatedSources);
    }

    protected CompilationResult compileSources(String codeVersion, String compilerName) {
        final JavaCompiler compiler = new JavaCompilerFactory().createCompiler(compilerName);

        final JavaCompilerSettings settings = compiler.createDefaultSettings();
        settings.setSourceVersion(codeVersion);
        settings.setTargetVersion(codeVersion);
        settings.setDebug(true);

        final String[] sources = generatedSources.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        int i = 0;
        for (String source : sources) {
            sources[i++] = source.replace('\\', '.');

        }
        LOGGER.info("Compiling into folder: " + codegenOutputFolder.getRoot());
        LOGGER.info("Test compiling: " + Arrays.toString(sources));

        final FileResourceReader2 sourceReader = new FileResourceReader2(codegenOutputFolder.getRoot());
        final FileResourceStore classWriter = new FileResourceStore(compilationOutputFolder.getRoot());
        return compiler.compile(sources, sourceReader, classWriter, Thread.currentThread()
                .getContextClassLoader(), settings);
    }

    protected URLClassLoader createClassLoaderForGeneratedClasses() throws MalformedURLException {
        final ClassLoader parentCL = Thread.currentThread().getContextClassLoader();
        return new URLClassLoader(
                new URL[]{compilationOutputFolder.getRoot().toURI().toURL()},
                parentCL);
    }

    /**
     * test load the classes with Jersey.
     * this cannot work, need concrete resource implementation, only interfaces are generated.
     */
    protected ResourceConfig loadJerseyResourceConfigWithResources(Set<String> classNames) {
        try {
            final URLClassLoader resourceClassLoader = createClassLoaderForGeneratedClasses();
            return new ResourceConfig()
                    .setClassLoader(resourceClassLoader)
                    .packages(TEST_BASE_PACKAGE);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
