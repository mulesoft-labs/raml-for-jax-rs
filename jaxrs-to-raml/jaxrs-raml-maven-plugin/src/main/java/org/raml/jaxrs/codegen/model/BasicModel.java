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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.reflection.ReflectionType;

/**
 * <p>Abstract BasicModel class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public abstract class BasicModel implements IBasicModel{

	private static final String VALUE_METHOD_ID = "value"; //$NON-NLS-1$
	
	private LinkedHashMap<String,IAnnotationModel> annotations
			= new LinkedHashMap<String, IAnnotationModel>();
	
	private LinkedHashMap<String,IAnnotationModel> annotationsByCanonicalName
			= new LinkedHashMap<String, IAnnotationModel>();
	
	private String simpleName;
	
	private String documentation;
	
	private boolean  publicM;
	private boolean  staticM;
	private Class<?> actualClass;
	private List<ITypeModel> jaxbTypes;
	private boolean isCollection;
	private boolean isMap;
	
	/**
	 * <p>getJAXBType.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	public List<ITypeModel> getJAXBTypes() {
		return this.jaxbTypes;
	}

	/**
	 * <p>getJavaType.</p>
	 *
	 * @return a {@link java.lang.Class} object.
	 */
	public Class<?> getJavaType() {
		return actualClass;
	}
	
	public ITypeModel getType() {
		return new ReflectionType(actualClass);
	}


	/**
	 * <p>isStatic.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isStatic() {
		return staticM;
	}

	/**
	 * <p>isPublic.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isPublic() {
		return publicM;
	}
	
	
	/**
	 * <p>getName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return simpleName;
	}
	
	/**
	 * <p>setName.</p>
	 *
	 * @param simpleName a {@link java.lang.String} object.
	 */
	public void setName(String simpleName) {
		this.simpleName = simpleName;
	}

	/**
	 * <p>Getter for the field <code>documentation</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDocumentation() {
		return documentation;
	}

	/**
	 * <p>Setter for the field <code>documentation</code>.</p>
	 *
	 * @param documentation a {@link java.lang.String} object.
	 */
	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	/**
	 * <p>Getter for the field <code>annotations</code>.</p>
	 *
	 * @return an array of {@link com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel} objects.
	 */
	public IAnnotationModel[] getAnnotations() {		
		return annotations.values().toArray(new IAnnotationModel[annotations.size()]);
	}
	
	/**
	 * <p>addAnnotation.</p>
	 *
	 * @param annotation a {@link com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel} object.
	 */
	public void addAnnotation(IAnnotationModel annotation){
		annotations.put(annotation.getName(), annotation);
		annotationsByCanonicalName.put(annotation.getCanonicalName(), annotation);
	}

	/** {@inheritDoc} */
	public String getAnnotationValue(String annotation) {
		
		IAnnotationModel annotationModel = annotations.get(annotation);
		if(annotationModel==null){
			return null;
		}
		
		return annotationModel.getValue(VALUE_METHOD_ID);		
	}

	/** {@inheritDoc} */
	public String[] getAnnotationValues(String annotation) {
		IAnnotationModel annotationModel = annotations.get(annotation);
		if(annotationModel==null){
			return null;
		}
		
		return annotationModel.getValues(VALUE_METHOD_ID);
	}

	/** {@inheritDoc} */
	public boolean hasAnnotation(String annotationName) {
		return annotations.containsKey(annotationName);
	}
	
	/** {@inheritDoc} */
	public IAnnotationModel getAnnotation(String name) {
		return annotations.get(name);
	}
	
	/** {@inheritDoc} */
	public boolean hasAnnotationWithCanonicalName(String annotationName) {
		return annotationsByCanonicalName.containsKey(annotationName);
	}
	
	/** {@inheritDoc} */
	public IAnnotationModel getAnnotationByCanonicalName(String name) {
		return annotationsByCanonicalName.get(name);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((annotations == null) ? 0 : annotations.hashCode());
		result = prime * result
				+ ((documentation == null) ? 0 : documentation.hashCode());
		result = prime * result
				+ ((simpleName == null) ? 0 : simpleName.hashCode());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicModel other = (BasicModel) obj;
		if (annotations == null) {
			if (other.annotations != null)
				return false;
		} else if (!annotations.equals(other.annotations))
			return false;
		if (documentation == null) {
			if (other.documentation != null)
				return false;
		} else if (!documentation.equals(other.documentation))
			return false;
		if (simpleName == null) {
			if (other.simpleName != null)
				return false;
		} else if (!simpleName.equals(other.simpleName))
			return false;
		return true;
	}

	/**
	 * <p>setStatic.</p>
	 *
	 * @param b a boolean.
	 */
	public void setStatic(boolean b) {
		this.staticM=true;
	}
	/**
	 * <p>setPublic.</p>
	 *
	 * @param b a boolean.
	 */
	public void setPublic(boolean b) {
		this.publicM=true;
	}

	/**
	 * <p>setJavaClass.</p>
	 *
	 * @param actualClass a {@link java.lang.Class} object.
	 */
	public void setJavaClass(Class<?> actualClass) {
		this.actualClass=actualClass;
	}

	/**
	 * <p>Setter for the field <code>jaxbType</code>.</p>
	 *
	 * @param processTypeReference a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	public void addJaxbType(ITypeModel processTypeReference) {
		if(this.jaxbTypes==null){
			this.jaxbTypes = new ArrayList<ITypeModel>();
		}
		this.jaxbTypes.add(processTypeReference);
	}

	public boolean isCollection() {
		return isCollection;
	}

	public void setCollection(boolean isCollection) {
		this.isCollection = isCollection;
	}

	public boolean isMap() {
		return isMap;
	}

	public void setMap(boolean isMap) {
		this.isMap = isMap;
	}
}
