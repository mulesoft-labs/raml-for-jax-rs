package com.mulesoft.jaxrs.raml.generator.annotations.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.parameter.FormParameter;
import org.raml.model.parameter.Header;
import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.visitor.RamlDocumentBuilder;

import com.google.common.io.Files;
import com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor;
import junit.framework.TestCase;

public class BasicTest extends TestCase{

	/**
	 * General test on annotation processing working
	 */
	public void test10() {
		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		final File f = new File(RAMLAnnotationProcessor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String classPath = System.getProperty("java.class.path") + ";" + f.getAbsolutePath(); //$NON-NLS-1$ //$NON-NLS-2$
		String tempDir = Files.createTempDir().getAbsolutePath();
		int rc = javac.run(System.in, System.out, System.err, "tests/com/mulesoft/jaxrs/raml/generator/annotations/tests/TestResource1.java", "-cp", classPath, "-processor", "com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor", "-XprintProcessorInfo", "-Aramlpath=" + tempDir); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		System.out.println("Result: " + rc); //$NON-NLS-1$
	}
	
	public void test11(){
		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		final File f = new File(RAMLAnnotationProcessor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String classPath = System.getProperty("java.class.path") + ";" + f.getAbsolutePath(); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			File file = File.createTempFile("test11_",".raml"); //$NON-NLS-1$ //$NON-NLS-2$
			int rc = javac.run(System.in, System.out, System.err, "tests/com/mulesoft/jaxrs/raml/generator/annotations/tests/TestResource1.java", "-cp", classPath, "-processor", "com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor", "-XprintProcessorInfo", "-Aramlpath=" + file.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			TestCase.assertEquals(0,rc);
			String raml;
			raml = FileUtils.readFileToString(file);
			Raml build = new RamlDocumentBuilder().build(raml);
			Resource resource = build.getResource("/users/{username}");		 //$NON-NLS-1$
			TestCase.assertNotNull(resource);
			Resource resource2 = resource.getResource("/qqq/{someBoolean}"); //$NON-NLS-1$
			Action action = resource2.getAction(ActionType.POST);
			TestCase.assertNotNull(action);
			Header header = action.getHeaders().get("h"); //$NON-NLS-1$
			TestCase.assertNotNull(header);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void test12() throws Exception {
		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		final File f = new File(RAMLAnnotationProcessor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String classPath = System.getProperty("java.class.path") + ";" + f.getAbsolutePath(); //$NON-NLS-1$ //$NON-NLS-2$
		File file = File.createTempFile("test12_",".raml"); //$NON-NLS-1$ //$NON-NLS-2$
		int rc = javac.run(System.in, System.out, System.err, "tests/com/mulesoft/jaxrs/raml/generator/annotations/tests/TestResource2.java", "-cp", classPath, "-processor", "com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor", "-XprintProcessorInfo", "-Aramlpath=" + file.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		TestCase.assertEquals(0,rc);
		String raml;
		raml = FileUtils.readFileToString(file);
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/test2/qqq");		 //$NON-NLS-1$
		Action action = resource.getAction(ActionType.PUT);
		TestCase.assertNotNull(action);
		Response response = action.getResponses().get("200"); //$NON-NLS-1$
		TestCase.assertNotNull(response);
		TestCase.assertNotNull(response.getBody().get("application/json")); //$NON-NLS-1$
		TestCase.assertNotNull(action.getBody().get("application/xml")); //$NON-NLS-1$
	}
	
	public void test13() throws Exception {
		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		final File f = new File(RAMLAnnotationProcessor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String classPath = System.getProperty("java.class.path") + ";" + f.getAbsolutePath(); //$NON-NLS-1$ //$NON-NLS-2$
		File file = File.createTempFile("test13_",".raml"); //$NON-NLS-1$ //$NON-NLS-2$
		int rc = javac.run(System.in, System.out, System.err, "tests/com/mulesoft/jaxrs/raml/generator/annotations/tests/TestResource2.java", "-cp", classPath, "-processor", "com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor", "-XprintProcessorInfo", "-Aramlpath=" + file.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		TestCase.assertEquals(0,rc);
		String raml;
		raml = FileUtils.readFileToString(file);
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/test2/qqq");		 //$NON-NLS-1$
		Action action = resource.getAction(ActionType.POST);
		TestCase.assertNotNull(action);
		Response response = action.getResponses().get("200"); //$NON-NLS-1$
		TestCase.assertNotNull(response);
		TestCase.assertNotNull(response.getBody().get("application/json")); //$NON-NLS-1$
		TestCase.assertNotNull(action.getBody().get("application/xml")); //$NON-NLS-1$
	}
	
	public void test14() throws Exception {
		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		final File f = new File(RAMLAnnotationProcessor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String classPath = System.getProperty("java.class.path") + ";" + f.getAbsolutePath(); //$NON-NLS-1$ //$NON-NLS-2$
		File file = File.createTempFile("test14_",".raml"); //$NON-NLS-1$ //$NON-NLS-2$
		int rc = javac.run(System.in, System.out, System.err, "tests/com/mulesoft/jaxrs/raml/generator/annotations/tests/TestResource4.java", "-cp", classPath, "-processor", "com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor", "-XprintProcessorInfo", "-Aramlpath=" + file.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		TestCase.assertEquals(0,rc);
		String raml;
		raml = FileUtils.readFileToString(file);
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/forms2");		 //$NON-NLS-1$
		Action action = resource.getAction(ActionType.POST);
		TestCase.assertNotNull(action);
		MimeType mimeType = action.getBody().get("multipart/form-data"); //$NON-NLS-1$
		TestCase.assertNotNull(mimeType);
		List<FormParameter> list = mimeType.getFormParameters().get("enabled"); //$NON-NLS-1$
		TestCase.assertNotNull(list);
		TestCase.assertNotNull(list.get(0));
		TestCase.assertNotNull(list.get(0).getDefaultValue());
	}
	
	public void test15() throws Exception {
		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		final File f = new File(RAMLAnnotationProcessor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String classPath = System.getProperty("java.class.path") + ";" + f.getAbsolutePath(); //$NON-NLS-1$ //$NON-NLS-2$
		final File file = File.createTempFile("test15_",".raml"); //$NON-NLS-1$ //$NON-NLS-2$
		int rc = javac.run(System.in, System.out, System.err, "tests/com/mulesoft/jaxrs/raml/generator/annotations/tests/TestResource3.java", "-cp", classPath, "-processor", "com.mulesoft.jaxrs.raml.annotation.model.apt.RAMLAnnotationProcessor", "-XprintProcessorInfo", "-Aramlpath=" + file.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		TestCase.assertEquals(0,rc);
		Raml raml = readRaml(file);
		Resource resource = raml.getResource("/world");		 //$NON-NLS-1$
		resource = resource.getResource("/countries"); //$NON-NLS-1$
		Action action = resource.getAction(ActionType.POST);
		TestCase.assertNotNull(action);
	}

	protected Raml readRaml(final File file) throws IOException {
		String raml = FileUtils.readFileToString(file);
		Raml build = new RamlDocumentBuilder(new DefaultResourceLoader() {
			
			@Override
			public InputStream fetchResource(String resourceName) {
				File resource = new File(file.getParent(), resourceName);
				if (resource.exists()) {
					try {
						return new FileInputStream(resource);
					} catch (FileNotFoundException e) {
						throw new RuntimeException(e);
					}
				}
				
				return super.fetchResource(resourceName);
			}
		}).build(raml);
		return build;
	}
}
