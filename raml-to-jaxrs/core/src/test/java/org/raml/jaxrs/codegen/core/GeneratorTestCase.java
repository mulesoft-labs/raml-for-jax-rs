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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion.JAXRS_1_1;
import static org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion.JAXRS_2_0;

import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.compilers.JavaCompilerSettings;
import org.apache.commons.jci.readers.FileResourceReader;
import org.apache.commons.jci.stores.FileResourceStore;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion;
import org.raml.jaxrs.codegen.core.ext.TestAnnotation;
import org.raml.jaxrs.codegen.core.ext.TestGeneratorExtension;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

public class GeneratorTestCase {
	private static final String TEST_BASE_PACKAGE = "org.raml.jaxrs.test";

	@Rule
	public TemporaryFolder codegenOutputFolder = new TemporaryFolder();

	@Rule
	public TemporaryFolder compilationOutputFolder = new TemporaryFolder();

	/*
	 * @Test public void runForJaxrs11WithoutJsr303() throws Exception {
	 * run(JAXRS_1_1, false); }
	 * 
	 * @Test public void runForJaxrs11WithJsr303() throws Exception {
	 * run(JAXRS_1_1, true); }
	 */

	@Ignore("Can only be run with JAX-RS 2.0 API on classpath")
	@Test
	public void runForJaxrs20WithoutJsr303() throws Exception {
		run(JAXRS_2_0, false, false);
	}

	@Ignore("Can only be run with JAX-RS 2.0 API on classpath")
	@Test
	public void runForJaxrs20WithJsr303() throws Exception {
		run(JAXRS_2_0, true, false);
	}


	@Test
	public void runWithExtension() throws Exception {
		run(JAXRS_1_1, false, true);
	}


	
	
	/**
	 * 
	 * @param jaxrsVersion
	 * @param useJsr303Annotations
	 * @param testWithExtension - test with the generator extension installed see org.raml.jaxrs.codegen.core.ext
	 * @throws Exception
	 */

	private void run(final JaxrsVersion jaxrsVersion, final boolean useJsr303Annotations,
			final boolean testWithExtension) throws Exception {
		final Set<String> generatedSources = new HashSet<String>();

		final Configuration configuration = new Configuration();
		configuration.setJaxrsVersion(jaxrsVersion);
		configuration.setUseJsr303Annotations(useJsr303Annotations);
		configuration.setOutputDirectory(codegenOutputFolder.getRoot());
		if (testWithExtension) {
			configuration.getExtensions().clear();
			configuration.getExtensions().add(new TestGeneratorExtension());
		}

		configuration.setBasePackageName(TEST_BASE_PACKAGE);
		String dirPath = getClass().getResource("/org/raml").getPath();
		configuration.setSourceDirectory(new File(dirPath));
		generatedSources.addAll(new Generator().run(
				new InputStreamReader(getClass().getResourceAsStream("/org/raml/full-config-with-patch.yaml")),
				configuration));

		configuration.setBasePackageName(TEST_BASE_PACKAGE + ".params");
		generatedSources.addAll(new Generator().run(
				new InputStreamReader(getClass().getResourceAsStream("/org/raml/params/param-types-with-repeat.yaml")),
				configuration));

		configuration.setBasePackageName(TEST_BASE_PACKAGE + ".integration");
		generatedSources.addAll(new Generator().run(
				new InputStreamReader(getClass().getResourceAsStream(
						"/org/raml/integration/sales-enablement-api-with-collections.yaml")), configuration));

		configuration.setBasePackageName(TEST_BASE_PACKAGE + ".rules");
		generatedSources.addAll(new Generator().run(
				new InputStreamReader(getClass().getResourceAsStream("/org/raml/rules/resource-full-ok.yaml")),
				configuration));
		generatedSources.addAll(new Generator().run(
				new InputStreamReader(getClass().getResourceAsStream(
						"/org/raml/rules/resource-with-description-ok.yaml")), configuration));
		generatedSources.addAll(new Generator().run(
				new InputStreamReader(getClass().getResourceAsStream("/org/raml/rules/resource-with-uri.yaml")),
				configuration));

		configuration.setBasePackageName(TEST_BASE_PACKAGE + ".schema");
		generatedSources.addAll(new Generator().run(
				new InputStreamReader(getClass().getResourceAsStream("/org/raml/schema/valid-xml-global.yaml")),
				configuration));
		generatedSources.addAll(new Generator()
				.run(new InputStreamReader(getClass().getResourceAsStream("/org/raml/schema/valid-xml2.yaml")),
						configuration,dirPath+"/schema/valid-xml2.yaml"));

		// test compile the classes
		final JavaCompiler compiler = new JavaCompilerFactory().createCompiler("eclipse");

		final JavaCompilerSettings settings = compiler.createDefaultSettings();
		settings.setSourceVersion("1.5");
		settings.setTargetVersion("1.5");
		settings.setDebug(true);

		final String[] sources = generatedSources.toArray(EMPTY_STRING_ARRAY);
		int i=0;
		for (String source:sources){
			sources[i++]=source.replace('\\', '.');
			
		}
		System.out.println("Test compiling: " + Arrays.toString(sources));

		final FileResourceReader2 sourceReader = new FileResourceReader2(codegenOutputFolder.getRoot());
		final FileResourceStore classWriter = new FileResourceStore(compilationOutputFolder.getRoot());
		final CompilationResult result = compiler.compile(sources, sourceReader, classWriter, Thread.currentThread()
				.getContextClassLoader(), settings);

		assertThat(ToStringBuilder.reflectionToString(result.getErrors(), ToStringStyle.SHORT_PREFIX_STYLE),
				result.getErrors(), is(emptyArray()));

		assertThat(ToStringBuilder.reflectionToString(result.getWarnings(), ToStringStyle.SHORT_PREFIX_STYLE),
				result.getWarnings(), is(emptyArray()));

		// test load the classes with Jersey
		final URLClassLoader resourceClassLoader = new URLClassLoader(new URL[] { compilationOutputFolder.getRoot()
				.toURI().toURL() });

		final ClassLoader initialClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(resourceClassLoader);
			final ResourceConfig config = new PackagesResourceConfig(TEST_BASE_PACKAGE);

			assertThat("Found: " + config.getRootResourceClasses(), config.getRootResourceClasses(), hasSize(13));

			if (testWithExtension) {
				// the extension adds TestAnnotation to all classes and methods
				for (Class c : config.getRootResourceClasses()) {	
					assertThat(c.getAnnotation(TestAnnotation.class), notNullValue());
					for (Method m : c.getMethods()) {
						assertThat(m.getAnnotation(TestAnnotation.class), notNullValue());
					}}
				}

			// TODO testing: actually send HTTP requests at the resources
		} finally {
			Thread.currentThread().setContextClassLoader(initialClassLoader);
		}
	}
}
