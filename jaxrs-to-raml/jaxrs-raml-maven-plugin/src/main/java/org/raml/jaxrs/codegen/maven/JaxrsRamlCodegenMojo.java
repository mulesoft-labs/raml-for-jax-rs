/*
 * Copyright 2013 (c) MuleSoft, Inc.
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

import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.raml.jaxrs.codegen.spoon.SpoonProcessor;

import com.martiansoftware.jsap.JSAPException;

import spoon.Launcher;
import spoon.OutputType;

/**
 * When invoked, this goals read one or more <a href="http://raml.org">RAML</a>
 * files and produces JAX-RS annotated Java classes.
 */
@Mojo(name = "generate_raml", requiresProject = true, threadSafe = false, requiresDependencyResolution = COMPILE_PLUS_RUNTIME, defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class JaxrsRamlCodegenMojo extends AbstractMojo {
	
	private static final String pathSeparator = System.getProperty("path.separator");	
	
	private static final Class<?>[] processorClasses = new Class<?>[]{
		SpoonProcessor.class
	}; 

	/**
	 * Directory location of the JAX-RS file(s).
	 */
	@Parameter(property = "sourceDirectory", defaultValue = "${basedir}/src/main/java")
	private File sourceDirectory;

	@Component
	private MavenProject project;


	public void execute() throws MojoExecutionException, MojoFailureException {

		String[] args = prepareArguments();		
		Launcher launcher;
		try {
			launcher = new Launcher();			
			launcher.setArgs(args);			
			if (args.length != 0) {
				launcher.run();
			} else {
				launcher.printUsage();
			}
		} catch (JSAPException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String[] prepareArguments() {
		
		ArrayList<String> lst = new ArrayList<String>();
		
		lst.add("--input");
		lst.add(sourceDirectory.getAbsolutePath());
		lst.add("--output-type");
		lst.add("nooutput");
		
		StringBuilder bld = new StringBuilder();		
		for(Class<?> clazz : processorClasses){
			bld.append(clazz.getCanonicalName());
			bld.append(pathSeparator);
		}
		lst.add("--processors");
		lst.add(bld.substring(0,bld.length()-pathSeparator.length()));
		
		String sourceClasspath = getSourceClassPath();
		if(!isEmptyString(sourceClasspath)){
			lst.add("--source-classpath");
			lst.add(sourceClasspath);
		}		
		String[] arr = lst.toArray(new String[lst.size()]);
		return arr;
	}

	private String getSourceClassPath() {
		
		StringBuilder bld = new StringBuilder();
		List<?> compileClasspathElements = null;
		try {
			compileClasspathElements = project.getCompileClasspathElements();
		} catch (DependencyResolutionRequiredException e1) {
			e1.printStackTrace();
		}
		if(compileClasspathElements==null||compileClasspathElements.isEmpty()){
			return null;
		}		
		for(Object obj : compileClasspathElements){
			bld.append(obj.toString());
			bld.append(pathSeparator);
		}
		String result = bld.substring(0, bld.length() - pathSeparator.length());
		return result;
	}
	
	private boolean isEmptyString(String str){
		return str==null||str.trim().length()==0;
	}
}