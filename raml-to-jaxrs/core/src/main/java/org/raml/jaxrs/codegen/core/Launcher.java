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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jsonschema2pojo.AnnotationStyle;
import org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion;
import org.raml.jaxrs.codegen.core.ext.GeneratorExtension;

/**
 * <p>Launcher class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class Launcher {

	/**
	 * <p>main.</p>
	 *
	 * @param args an array of {@link java.lang.String} objects.
	 */
	public static void main(String[] args) {
		
		Map<String,String> argMap = createArgMap(args);
		
		Configuration configuration = createConfiguration(argMap);
		
		boolean removeOldOutput = false;		
		String removeOldOutputStringValue = argMap.get("removeOldOutput");
		if(removeOldOutputStringValue!=null){
			removeOldOutput = Boolean.parseBoolean(removeOldOutputStringValue);
		}

		boolean generateClient = false;	
		String generateClientStringValue = argMap.get("generateClientProxy");
		if(generateClientStringValue!=null){
			generateClient = Boolean.parseBoolean(generateClientStringValue);
		}
		configuration.setGenerateClientInterface(generateClient);
		
		Collection<File> ramlFiles = getRamlFiles(argMap);
		if(ramlFiles.isEmpty()){
			return;
		}
		
		if (removeOldOutput)
        {
			try {
				FileUtils.cleanDirectory(configuration.getOutputDirectory());
			} catch (IOException e) {
				e.printStackTrace();
			}            
        }
		final GeneratorProxy generator = new GeneratorProxy();
		for (final File ramlFile : ramlFiles)
        {
            try {
				generator.run(new FileReader(ramlFile), configuration,ramlFile.getAbsolutePath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}

	private static Collection<File> getRamlFiles(Map<String, String> argMap) {
		
		String sourcePaths = argMap.get("sourcePaths");
		String sourceDirectoryPath = argMap.get("sourceDirectory");
		if ( !isEmptyString(sourcePaths) )
		{
			List<File> sourceFiles = new ArrayList<File>();
			String[] split = sourcePaths.split(System.getProperty("path.separator"));            
            for(String str : split){
            	sourceFiles.add(new File(str));
            }
            return sourceFiles;
		}
		else{
			File sourceDirectory = new File(sourceDirectoryPath);
			if (!sourceDirectory.isDirectory()) {
                throw new RuntimeException("The provided path doesn't refer to a valid directory: "+ sourceDirectory);
            }
            return FileUtils.listFiles(sourceDirectory, new String[]{"raml", "yaml"}, false);
        }
	}

	private static Configuration createConfiguration(Map<String, String> argMap) {
		
		Configuration configuration = new Configuration();
		
		File rootDirectory = new File(System.getProperty("user.dir"));
		File outputDirectory = new File(rootDirectory,"generated-sources/raml-jaxrs");
		File sourceDirectory = new File(rootDirectory,"src/main/raml");
		
		String basePackageName = null;
		String jaxrsVersion = "1.1";
		boolean useJsr303Annotations = false;
		boolean mapToVoid = false;
		String jsonMapper = "jackson1";

		boolean generateClientProxy = false;
		boolean useTitlePropertyForSchemaNames=false;
		String modelPackageName = "model";
		String asyncResourceTrait = null;
		String customAnnotator = null;
		List<GeneratorExtension> extensions = null;
		Map<String,String> jsonMapperConfiguration = new HashMap<String, String>();
				
		String restIFPackageName = "resource";
		String interfaceNameSuffix = "Resource";
	

		for( Map.Entry<String,String> entry : argMap.entrySet() ){
			
			String argName = entry.getKey();			
			String argValue = entry.getValue();
			
			if(argName.equals("outputDirectory")){				
				outputDirectory = new File(argValue);
			}
			else if(argName.equals("sourceDirectory")){
				sourceDirectory = new File(argValue);
			}
			else if(argName.equals("jaxrsVersion")){
				jaxrsVersion = argValue;
			}
			else if(argName.equals("mapToVoid")){
				mapToVoid = Boolean.parseBoolean(argValue);
			}
			else if(argName.equals("basePackageName")){
				basePackageName = argValue;
			}
			else if(argName.equals("useJsr303Annotations")){
				useJsr303Annotations = Boolean.parseBoolean(argValue);
			}
			else if(argName.equals("jsonMapper")){
				jsonMapper = argValue;
			}
			else if(argName.equals("generateClientProxy")){
				generateClientProxy = Boolean.parseBoolean(argValue);
			}
			else if(argName.equals("useTitlePropertyForSchemaNames")){
				useTitlePropertyForSchemaNames = Boolean.parseBoolean(argValue);
			}
			else if(argName.equals("modelPackageName")){
				modelPackageName = argValue;
			}
			else if(argName.equals("restIFPackageName")){
				restIFPackageName = argValue;
			}
			else if(argName.equals("interfaceNameSuffix")){
				interfaceNameSuffix = argValue;
			}
			else if(argName.equals("asyncResourceTrait")){
				asyncResourceTrait = argValue;
			}
			else if(argName.equals("customAnnotator")){
				customAnnotator = argValue;
			}
			else if(argName.equals("extensions")){
				extensions = new ArrayList<GeneratorExtension>();
				String[] extensionClasses = argValue.split(",");
				for(String s: extensionClasses) {
					s = s.trim();
					try {
						extensions.add((GeneratorExtension) Class.forName(s).newInstance());
					} catch (Exception e) {
						throw new RuntimeException("unknown extension " + s);
					}
				}
			}
			else if(argName.startsWith("jsonschema2pojo.")) {
				String name = argName.substring("jsonschema2pojo.".length());
				jsonMapperConfiguration.put(name, argValue);
			}
		}
		if(basePackageName==null){
			throw new RuntimeException("Base package must be specified.");
		}
		
		if(!outputDirectory.exists()){
			outputDirectory.mkdirs();
		}
		if(!outputDirectory.isDirectory()){
			throw new RuntimeException("Output destination must be a directory: " + outputDirectory);
		}
		
		configuration.setBasePackageName(basePackageName);
        configuration.setJaxrsVersion(JaxrsVersion.fromAlias(jaxrsVersion));
        configuration.setOutputDirectory(outputDirectory);
        configuration.setUseJsr303Annotations(useJsr303Annotations);
        configuration.setJsonMapper(AnnotationStyle.valueOf(jsonMapper.toUpperCase()));
        configuration.setSourceDirectory(sourceDirectory);

        configuration.setGenerateClientInterface(generateClientProxy);
        configuration.setEmptyResponseReturnVoid(mapToVoid);        
        configuration.setUseTitlePropertyWhenPossible(useTitlePropertyForSchemaNames);
        configuration.setModelPackageName(modelPackageName);
        configuration.setRestIFPackageName(restIFPackageName);
        configuration.setInterfaceNameSuffix(interfaceNameSuffix);
		configuration.setAsyncResourceTrait(asyncResourceTrait);
		if (extensions!=null) configuration.setExtensions(extensions);
		if(!jsonMapperConfiguration.isEmpty()) configuration.setJsonMapperConfiguration(jsonMapperConfiguration);

		if(customAnnotator!=null && !customAnnotator.trim().isEmpty()){
			try {
				configuration.setCustomAnnotator((Class)Class.forName(customAnnotator));
			} catch (ClassNotFoundException e) {}
		}

        
        return configuration;
	}

	private static Map<String, String> createArgMap(String[] args) {
		
		HashMap<String,String> map = new HashMap<String, String>(); 
		for(int i = 0 ; i < args.length ; i++ ){
					
			String argName = args[i];
			if(argName.startsWith("-")){
				argName = argName.substring(1);			
				if(i+1 < args.length)
				{
					String argValue = args[i+1];
					if(!argValue.startsWith("-")){					
						map.put(argName, argValue);
						i++;
					}
				}
			}
		}
		return map;
	}

	private static boolean isEmptyString(String str) {		
		
		return str == null || str.trim().isEmpty();
	}
}
