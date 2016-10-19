package org.raml.jaxrs.codegen.core;

import static org.apache.commons.lang.ArrayUtils.EMPTY_STRING_ARRAY;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.compilers.JavaCompilerSettings;
import org.apache.commons.jci.readers.FileResourceReader;
import org.apache.commons.jci.stores.FileResourceStore;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

import junit.framework.TestCase;

public class RAML1Test {

	private static final String TEST_BASE_PACKAGE = "org.raml.jaxrs.test";

	@Rule
	public TemporaryFolder codegenOutputFolder = new TemporaryFolder();

	@Rule
	public TemporaryFolder compilationOutputFolder = new TemporaryFolder();

	@Test
	public void runForJaxrs20WithJsr303() throws Exception {
		run(JaxrsVersion.JAXRS_2_0, true, "/org/raml/t9.raml");
	}
	
	@Test
	public void runComplete() throws Exception {
		run(JaxrsVersion.JAXRS_2_0, true, "/testComplete1.raml");
	}

	private void run(final JaxrsVersion jaxrsVersion, final boolean useJsr303Annotations,String resource) throws Exception {
		final Set<String> generatedSources = new HashSet<String>();

		final Configuration configuration = new Configuration();
		configuration.setJaxrsVersion(jaxrsVersion);
		configuration.setUseJsr303Annotations(useJsr303Annotations);
		configuration.setOutputDirectory(codegenOutputFolder.getRoot());

		configuration.setBasePackageName(TEST_BASE_PACKAGE);
		String dirPath = getClass().getResource("/org/raml").getPath();

		configuration.setSourceDirectory(new File(dirPath));
		String name = resource;
		Set<String> run = new Generator()
				.run(new InputStreamReader(getClass().getResourceAsStream(name)), configuration);
		for (String s : run) {
			generatedSources.add(s.replace('\\', '/'));
		}

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

		assertThat(ToStringBuilder.reflectionToString(result.getErrors(), ToStringStyle.SHORT_PREFIX_STYLE),
				result.getErrors(), is(emptyArray()));

		assertThat(ToStringBuilder.reflectionToString(result.getWarnings(), ToStringStyle.SHORT_PREFIX_STYLE),
				result.getWarnings(), is(emptyArray()));

		// test load the classes with Jersey
		final URLClassLoader resourceClassLoader = new URLClassLoader(
				new URL[] { compilationOutputFolder.getRoot().toURI().toURL() });

		final ClassLoader initialClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(resourceClassLoader);
			final ResourceConfig config = new PackagesResourceConfig(TEST_BASE_PACKAGE);

			Set<Class<?>> classes = config.getClasses();

			Iterator<Class<?>> it = classes.iterator();
			Class<?> something = it.next();
			Method[] methods = something.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals("postQ")) {
					TestCase.assertTrue(method.getParameterTypes()[0].getSimpleName().equals("Person"));
				} else {
					TestCase.assertTrue(method.getParameterTypes()[0].getSimpleName().equals("String"));
				}
			}
			// assertEquals(InputStream.class.getName(),
			// methodWithInputStreamParam.getParameterTypes()[0].getName());
		} finally {
			Thread.currentThread().setContextClassLoader(initialClassLoader);
		}
	}
}
