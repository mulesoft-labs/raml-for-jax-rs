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
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.martiansoftware.jsap.JSAPException;

import spoon.Launcher;

/**
 * When invoked, this goals read one or more <a href="http://raml.org">RAML</a> files and produces
 * JAX-RS annotated Java classes.
 */
@Mojo(name = "generate_raml", requiresProject = true, threadSafe = false, requiresDependencyResolution = COMPILE_PLUS_RUNTIME, defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class JaxrsRamlCodegenMojo extends AbstractMojo
{
//    @Parameter(defaultValue = "${project}")
//    private MavenProject project;
//
//    /**
//     * Skip plug-in execution.
//     */
//    @Parameter(property = "skip", defaultValue = "false")
//    private boolean skip;
//
//    /**
//     * Target directory for generated Java source files.
//     */
//    @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/generated-sources/raml-jaxrs")
//    private File outputDirectory;
//
//    /**
//     * An array of locations of the RAML file(s).
//     */
//    @Parameter(property = "sourcePaths")
//    private File[] sourcePaths;
//
    /**
     * Directory location of the JAX-RS file(s).
     */
    @Parameter(property = "sourceDirectory", defaultValue = "${basedir}/src/main/java")
    private File sourceDirectory;
//
//    /**
//     * The targeted JAX-RS version: either "1.1" or "2.0" .
//     */
//    @Parameter(property = "jaxrsVersion", defaultValue = "1.1")
//    private String jaxrsVersion;
//
//    /**
//     * Base package name used for generated Java classes.
//     */
//    @Parameter(property = "basePackageName", required = true)
//    private String basePackageName;
//
//    /**
//     * Should JSR-303 annotations be used?
//     */
//    @Parameter(property = "useJsr303Annotations", defaultValue = "false")
//    private boolean useJsr303Annotations;
//    
//    
//    /**
//     * The targeted JAX-RS version: either "1.1" or "2.0" .
//     */
//    @Parameter(property = "mapToVoid", defaultValue = "false")
//    private boolean mapToVoid;
//
//    /**
//     * Whether to empty the output directory before generation occurs, to clear out all source files
//     * that have been generated previously.
//     */
//    @Parameter(property = "removeOldOutput", defaultValue = "false")
//    private boolean removeOldOutput;
//
//    /**
//     * The JSON object mapper to generate annotations to: either "jackson1", "jackson2" or "gson" or
//     * "none"
//     */
//    @Parameter(property = "jsonMapper", defaultValue = "jackson1")
//    private String jsonMapper;
//    
//    
//    @Parameter(property = "asyncResourceTrait")
//    private String asyncResourceTrait;
//    /**
//    * Optional extra configuration provided to the JSON mapper. Supported keys are:
//    * "generateBuilders", "includeHashcodeAndEquals", "includeToString", "useLongIntegers"
//    */
//    @Parameter(property = "jsonMapperConfiguration")
//    private Map<String, String> jsonMapperConfiguration;
//    
//    /**
//    * Throw exception on Resource Method
//    */
//    //@Parameter(property = "methodThrowException")
    //private String methodThrowException;


    public void execute() throws MojoExecutionException, MojoFailureException
    {
//        if (skip)
//        {
//            getLog().info("Skipping execution...");
//            return;
//        }
//
//        if ((sourceDirectory == null) && (sourcePaths == null))
//        {
//            throw new MojoExecutionException("One of sourceDirectory or sourcePaths must be provided");
//        }
        
    	String cp = System.getProperty("java.class.path");
		System.out.println(cp);
    	
        String[] args = new String[]{
        		"-i",
        		"C:/workspaces/RAML-new/GIT/raml-for-jax-rs/jaxrs-to-raml/examples/contacts/src/main",
        		"-p",
        		"org.raml.jaxrs.codegen.spoon.JaxrsSpoonProcessor"
        };
        
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
}