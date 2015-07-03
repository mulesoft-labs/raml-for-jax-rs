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
package org.raml.jaxrs.codegen.core.ext;

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.raml.jaxrs.codegen.core.Names;
import org.raml.model.Action;
import org.raml.model.MimeType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.parameter.AbstractParam;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;

public class ResourceDisplayNameBasedMethodNameBuilder implements
		MethodNameBuilderExtension {

	public void onCreateResourceInterface(JDefinedClass resourceInterface,
			Resource resource) {}

	public void onAddResourceMethod(JMethod method, Action action,
			MimeType bodyMimeType, Collection<MimeType> uniqueResponseMimeTypes) {}

	public boolean AddParameterFilter(String name, AbstractParam parameter,
			Class<? extends Annotation> annotationClass, JMethod method) {
		return true;
	}

	public void setRaml(Raml raml) {}

	public String buildResourceMethodName(Action action, MimeType bodyMimeType,Resource resource) {
		if(action==null||resource==null){
			return null;
		}
		String displayName = resource.getDisplayName();
		if(displayName==null||displayName.trim().isEmpty()){
			return null;
		}
		StringBuilder bld = new StringBuilder();
		for(String s : displayName.split("\\s")){
			if(s.isEmpty()){
				continue;
			}
			char ch0 = s.charAt(0);
			if(bld.length()==0){							
				bld.append(Character.isJavaIdentifierStart(ch0)?Character.toUpperCase(ch0):'_');
			}
			else{
				bld.append(Character.isJavaIdentifierPart(ch0)?Character.toUpperCase(ch0):'_');
			}
			for(int i = 1 ; i < s.length() ; i++){
				char ch = s.charAt(i);
				bld.append(Character.isJavaIdentifierPart(ch)?ch:'_');
			}
		}
		String type = action.getType().toString().toLowerCase();
		
		String mti = Names.buildMimeTypeInfix(bodyMimeType);
		if(mti!=null&&!mti.isEmpty()){
			mti = mti.substring(0,1).toUpperCase() + mti.substring(1).toLowerCase();
		}
		String result = type + bld.toString() + mti;
		return result;
	}

	public void setCodeModel(JCodeModel codeModel) {}
}
