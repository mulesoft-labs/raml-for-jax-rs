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
package org.raml.jaxrs.codegen.model;

import java.util.HashMap;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;

/**
 * <p>AnnotationModel class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class AnnotationModel implements IAnnotationModel{

	private String name;
	
	private String fullyQualifiedName;
	
	private HashMap<String,String[]> stringArrayValues;
	
	private HashMap<String,Object> stringValues;
	
	private HashMap<String,IAnnotationModel[]> annotationArrayValues;

	/**
	 * <p>Constructor for AnnotationModel.</p>
	 */
	public AnnotationModel() {
	}

	
	/**
	 * <p>Getter for the field <code>name</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {		

		return name;
	}
	
	/**
	 * <p>Setter for the field <code>name</code>.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public void setName(String name){
		this.name = name;
	}

	
	/** {@inheritDoc} */
	public String getValue(String pairName) {
		if (stringValues!=null){
			Object object = stringValues.get(pairName);
			if (object!=null){
				return object.toString();
			}
		}
		return null;
	}

	
	/** {@inheritDoc} */
	public String[] getValues(String key) {

		String[] values = stringArrayValues != null ? stringArrayValues.get(key) : null;
		if(values==null){
			Object value = stringValues.get(key);
			if(value instanceof String){
				values = new String[]{(String) value};
			}
		}
		return values;
	}

	
	/** {@inheritDoc} */
	public IAnnotationModel[] getSubAnnotations(String pairName) {
		
		return annotationArrayValues != null ? annotationArrayValues.get(pairName) : null;
	}
	
	/**
	 * <p>addValue.</p>
	 *
	 * @param key a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Object} object.
	 */
	public void addValue(String key, Object value) {
		
		if(value==null||key==null){
			return;
		}
		if (value instanceof String[]){
			if(stringArrayValues==null){
				stringArrayValues = new HashMap<String, String[]>();
			}
			stringArrayValues.put(key, (String[]) value);			
		}
		else if(value instanceof IAnnotationModel[]){
			if(annotationArrayValues==null){
				annotationArrayValues = new HashMap<String, IAnnotationModel[]>();
			}
			annotationArrayValues.put(key, (IAnnotationModel[]) value);
		}
		else{
			if(stringValues==null){
				stringValues = new HashMap<String, Object>();
			}
			stringValues.put(key, value);
		}
	}


	public String getCanonicalName() {
		return fullyQualifiedName;
	}


	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}

}
