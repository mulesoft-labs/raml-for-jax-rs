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
package raml.jaxrs.eclipse.plugin;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.jsonschema2pojo.AnnotationStyle;
import org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion;

public class UIConfiguration {
	
	ObjectReference<String> basePackageName = new ObjectReference<String>();
	
	ObjectReference<IResource> ramlFile = new ObjectReference<IResource>();
	
	ObjectReference<IResource> srcFolder = new ObjectReference<IResource>();
	
	ObjectReference<IResource> dstFolder = new ObjectReference<IResource>();
	
	ObjectReference<String> jaxrsVersion = new ObjectReference<String>();
	
    ObjectReference<Boolean> useJsr303Annotations = new ObjectReference<Boolean>();
    
    ObjectReference<Boolean> isEmptyResponseUsesVoid = new ObjectReference<Boolean>();
    
    ObjectReference<Boolean> generateClientProxy = new ObjectReference<Boolean>();
    
    public boolean getGenerateClientProxy(){
    	Boolean boolean1 = generateClientProxy.get();
		return boolean1!=null?boolean1:false;
    }
    public void setGenerateClientProxy(boolean gcp){
    	generateClientProxy.set(gcp);
    }
    
    public Boolean getEmptyResponseUsesVoid() {
		return isEmptyResponseUsesVoid.get();
	}

	public void setEmptyResponseUsesVoid(boolean object) {
		isEmptyResponseUsesVoid.set(object);
	}

	ObjectReference<String> jsonMapper = new ObjectReference<String>();
	
	public String getBasePackageName() {		
		return basePackageName.get();
	}

	public void setBasePackageName(String basePackageName) {
		this.basePackageName.set(basePackageName);
	}
	
	public void setRamlFile( IFile ramlFile ){
		this.ramlFile.set(ramlFile) ;		
	}
	
	public void setSrcFolder( IContainer srcFolder ){
		this.srcFolder.set(srcFolder) ;
	}
	
	public void setDstFolder( IContainer dstFolder ){
		this.dstFolder.set(dstFolder) ;
	}
	
	public IFile getRamlFile() {
		return (IFile) ramlFile.get();
	}

	public IContainer getSrcFolder() {
		return (IContainer) srcFolder.get();
	}

	public IContainer getDstFolder() {
		return (IContainer) dstFolder.get();
	}

	public String getJaxrsVersion() {
		return jaxrsVersion.get();
	}

	public void setJaxrsVersion(String jaxrsVersion) {
		this.jaxrsVersion.set(jaxrsVersion);
	}

	public Boolean getUseJsr303Annotations() {
		return useJsr303Annotations.get();
	}

	public void setUseJsr303Annotations(Boolean useJsr303Annotations){
		this.useJsr303Annotations.set(useJsr303Annotations);
	}

	public String getJsonMapper() {
		return jsonMapper.get();
	}

	public void setJsonMapper(String jsonMapper) {
		this.jsonMapper.set(jsonMapper);
	}

	public boolean isValid() {
		
		if(basePackageName.get() == null){
			return false;
		}
		if(ramlFile.get() == null){
			return false;
		}
		if(dstFolder.get()==null){
			return false;
		}
		return true;
	}
}
