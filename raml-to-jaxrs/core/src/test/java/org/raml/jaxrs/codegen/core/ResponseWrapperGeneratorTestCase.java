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

import static org.apache.commons.lang.ArrayUtils.EMPTY_STRING_ARRAY;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion.JAXRS_1_1;
import static org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion.JAXRS_2_0;

import java.io.File;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.ws.ResponseWrapper;

import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.compilers.JavaCompilerSettings;
import org.apache.commons.jci.problems.CompilationProblem;
import org.apache.commons.jci.readers.FileResourceReader;
import org.apache.commons.jci.stores.FileResourceStore;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion;

/**
 * 
 * Check that generated response method signatures do not collide with 
 * existing superclass methods for certain HTTP responses.
 * 
 */

public class ResponseWrapperGeneratorTestCase
{
    private static final String TEST_BASE_PACKAGE = "org.raml.jaxrs.test";

    @Rule
    public TemporaryFolder codegenOutputFolder = new TemporaryFolder();

    @Rule
    public TemporaryFolder compilationOutputFolder = new TemporaryFolder();

    /*@Test
    public void runForJaxrs11WithoutJsr303() throws Exception
    {
        run(JAXRS_1_1, false);
    }

    @Test
    public void runForJaxrs11WithJsr303() throws Exception
    {
        run(JAXRS_1_1, true);
    }*/

    @Ignore("Can only be run with JAX-RS 2.0 API on classpath")
    @Test
    public void runForJaxrs20WithoutJsr303() throws Exception
    {
        run(JAXRS_2_0, false);
    }

    @Ignore("Can only be run with JAX-RS 2.0 API on classpath")
    @Test
    public void runForJaxrs20WithJsr303() throws Exception
    {
        run(JAXRS_2_0, true);
    }

    private void run(final JaxrsVersion jaxrsVersion, final boolean useJsr303Annotations) throws Exception
    {
        final Set<String> generatedSources = new HashSet<String>();

        final Configuration configuration = new Configuration();
        configuration.setJaxrsVersion(jaxrsVersion);
        configuration.setUseJsr303Annotations(useJsr303Annotations);
        configuration.setOutputDirectory(codegenOutputFolder.getRoot());

        configuration.setBasePackageName(TEST_BASE_PACKAGE);
        String dirPath = getClass().getResource("/org/raml").getPath();

        configuration.setSourceDirectory( new File(dirPath) );
        generatedSources.addAll(new Generator().run(
            new InputStreamReader(getClass().getResourceAsStream("/org/raml/responses/wrapper.yaml")),
            configuration));

        // test compile the classes
        final JavaCompiler compiler = new JavaCompilerFactory().createCompiler("eclipse");

        final JavaCompilerSettings settings = compiler.createDefaultSettings();
        settings.setSourceVersion("1.5");
        settings.setTargetVersion("1.5");
        settings.setDebug(true);

        final String[] sources = generatedSources.toArray(EMPTY_STRING_ARRAY);

        final FileResourceReader sourceReader = new FileResourceReader(codegenOutputFolder.getRoot());
        final FileResourceStore classWriter = new FileResourceStore(compilationOutputFolder.getRoot());
        final CompilationResult result = compiler.compile(sources, sourceReader, classWriter,
            Thread.currentThread().getContextClassLoader(), settings);
        CompilationProblem[] errors = result.getErrors();
		assertThat(ToStringBuilder.reflectionToString(errors, ToStringStyle.SHORT_PREFIX_STYLE),
            errors, is(emptyArray()));
        assertThat(
            ToStringBuilder.reflectionToString(result.getWarnings(), ToStringStyle.SHORT_PREFIX_STYLE),
            result.getWarnings(), is(emptyArray()));
    }
}