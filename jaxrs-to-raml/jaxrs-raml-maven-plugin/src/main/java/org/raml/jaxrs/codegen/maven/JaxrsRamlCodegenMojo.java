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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
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

import com.mulesoft.jaxrs.raml.annotation.model.IRamlConfig;
import com.mulesoft.jaxrs.raml.annotation.model.IResourceVisitorExtension;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;
import com.mulesoft.jaxrs.raml.annotation.model.reflection.RuntimeResourceVisitor;

import spoon.Launcher;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.PackageFactory;

/**
 * When invoked, this goals read one or more JAX-RS annotated Java
 * classes and produces a <a href="http://raml.org">RAML</a> file.
 *
 * @author kor
 * @version $Id: $Id
 */
@Mojo(name = "generate-raml", requiresProject = true, threadSafe = false, requiresDependencyResolution = COMPILE_PLUS_RUNTIME, defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class JaxrsRamlCodegenMojo extends AbstractMojo {
	
	
	private static final String DEFAULT_RAML_FILENAME = "api.raml";

	private static final String RAML_EXTENSION = ".raml";

	private static final String pathSeparator = System.getProperty("path.separator");	

	/**
	 * Directory location of the JAX-RS file(s).
	 */
	@Parameter(property = "sourceDirectory", defaultValue = "${basedir}/src/main/java")
	private File sourceDirectory;
	
	/**
     * An array of locations of the JAX-RS file(s).
     */
    @Parameter(property = "sourcePaths")
    private File[] sourcePaths;
    
    /**
     * Generated RAML file.
     */
    @Parameter(property = "outputFile", defaultValue = "${project.build.directory}/generated-sources/jaxrs-raml/api.raml")
    private File outputFile;
    
    private File outputDirectory;
	
	/**
     * Whether to empty the output directory before generation occurs, to clear out all source files
     * that have been generated previously.
     */
    @Parameter(property = "removeOldOutput", defaultValue = "false")
    private boolean removeOldOutput;
    
    

	/**
     * API title
     */
    @Parameter(property = "title", defaultValue = "${project.artifactId}")
    private String title;

	/**
     * API base URL
     */
    @Parameter(property = "baseUrl")
    private String baseUrl;

	/**
     * API version
     */
    @Parameter(property = "version")
    private String version;

	@Component
	private MavenProject project;
	
	@Parameter(property = "extensions")
	private List<String> extensions;


	/**
	 * <p>execute.</p>
	 *
	 * @throws org.apache.maven.plugin.MojoExecutionException if any.
	 * @throws org.apache.maven.plugin.MojoFailureException if any.
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {

		
		checkAndPrepareDirectories();
		
		String[] args = prepareArguments();

		Launcher launcher = null;
		try {
			launcher = new Launcher();
			launcher.setArgs(args);
			launcher.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(launcher == null){
			return;
		}
		
		Factory factory = launcher.getFactory();
		PackageFactory packageFactory = factory.Package();
		Collection<CtPackage> allRoots = packageFactory.getAllRoots();
		
		SpoonProcessor spoonProcessor = new SpoonProcessor(factory);
		spoonProcessor.process(allRoots);
		
		ClassLoader classLoader = launcher.getFactory().getEnvironment().getClassLoader();
		IRamlConfig config = new MavenRamlConfig(title, baseUrl, version);
		
		if (extensions != null) {
			for (String className : extensions) {
				try{
					Class c = Class.forName(className);
					if (c == null) {
						throw new MojoExecutionException("generatorExtensionClass " + className
								+ " cannot be loaded."
								+ "Have you installed the correct dependency in the plugin configuration?");
					}
					if (!((c.newInstance()) instanceof IResourceVisitorExtension)) {
						throw new MojoExecutionException("generatorExtensionClass " + className
								+ " does not implement" + IResourceVisitorExtension.class.getCanonicalName());
	
					}
					config.getExtensions().add((IResourceVisitorExtension) c.newInstance());
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}

		ResourceVisitor rv = new RuntimeResourceVisitor(outputFile, classLoader, config);
		for(ITypeModel type : spoonProcessor.getRegistry().getTargetTypes()){
			rv.visit(type);
		}
		
		saveRaml(rv.getRaml(),allRoots);
		
	}

	private void saveRaml(String raml, Collection<CtPackage> allRoots) {
		
		if(outputFile.isDirectory()){
			String defaultFileName = DEFAULT_RAML_FILENAME;
l0:			for(CtPackage pkg : allRoots){
				for(CtType<?> type : pkg.getTypes()){
					defaultFileName = type.getSimpleName() + RAML_EXTENSION;
					break l0;
				}				
			}
			outputFile = new File(outputFile,defaultFileName);
		}
		else{
			if(!outputFile.getName().toLowerCase().endsWith(RAML_EXTENSION)){
				outputFile = new File(outputFile.getAbsolutePath()+RAML_EXTENSION);
			}
		}

		try {
			if(!outputFile.exists()){
				outputFile.createNewFile();
			}
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
			fileOutputStream.write(raml.getBytes("UTF-8")); //$NON-NLS-1$
			fileOutputStream.close();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private void checkAndPrepareDirectories() throws MojoExecutionException
	{
		if(outputFile==null||outputFile.getAbsolutePath().isEmpty()){
			throw new MojoExecutionException("The outputDirectory must not be empty.");
		}
		if(outputFile.isDirectory()){
			outputDirectory = outputFile;
		}
		else{			
			outputDirectory = outputFile.getParentFile();
		}
		outputDirectory.mkdirs();
		if(removeOldOutput){
			try
            {
                FileUtils.cleanDirectory(outputDirectory);
            }
            catch (final IOException ioe)
            {
                throw new MojoExecutionException("Failed to clean directory: " + outputFile, ioe);
            }
		}
	}

	private String[] prepareArguments() throws MojoExecutionException {
		
		ArrayList<String> lst = new ArrayList<String>();
		
		String inputValue = getInputValue();
		if(isEmptyString(inputValue)){
			throw new MojoExecutionException("One of sourceDirectory or sourcePaths parameters must not be empty.");
		}		
		lst.add("--input");
		lst.add(inputValue);
		lst.add("--output-type");
		lst.add("nooutput");
		
		String sourceClasspath = getSourceClassPath();
		if(!isEmptyString(sourceClasspath)){
			lst.add("--source-classpath");
			lst.add(sourceClasspath);
		}		
		String[] arr = lst.toArray(new String[lst.size()]);
		return arr;
	}

	private String getInputValue() {
		
		if(sourcePaths!=null&&sourcePaths.length!=0){
			StringBuilder bld = new StringBuilder(); 
			for(File f : sourcePaths){
				bld.append(f.getAbsolutePath()).append(pathSeparator);
			}
			String result = bld.substring(0, bld.length()-pathSeparator.length());
			return result;			
		}
		String result = sourceDirectory.getAbsolutePath();		
		return result;
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

	public File getSourceDirectory() {
		return sourceDirectory;
	}

	public void setSourceDirectory(File sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public File[] getSourcePaths() {
		return sourcePaths;
	}

	public void setSourcePaths(File[] sourcePaths) {
		this.sourcePaths = sourcePaths;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public boolean isRemoveOldOutput() {
		return removeOldOutput;
	}

	public void setRemoveOldOutput(boolean removeOldOutput) {
		this.removeOldOutput = removeOldOutput;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public MavenProject getProject() {
		return project;
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}
}
