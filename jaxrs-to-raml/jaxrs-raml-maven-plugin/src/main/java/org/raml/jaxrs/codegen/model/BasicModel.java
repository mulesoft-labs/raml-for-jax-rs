package org.raml.jaxrs.codegen.model;

import java.util.LinkedHashMap;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;

public abstract class BasicModel implements IBasicModel{

	private static final String VALUE_METHOD_ID = "value"; //$NON-NLS-1$
	
	private LinkedHashMap<String,IAnnotationModel> annotations = new LinkedHashMap<String, IAnnotationModel>();
	
	private String simpleName;
	
	private String documentation;
	
	
	public String getName() {
		return simpleName;
	}
	
	public void setName(String simpleName) {
		this.simpleName = simpleName;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public IAnnotationModel[] getAnnotations() {		
		return annotations.values().toArray(new IAnnotationModel[annotations.size()]);
	}
	
	public void addAnnotation(IAnnotationModel annotation){
		String name = annotation.getName();
		annotations.put(name, annotation);
	}

	public String getAnnotationValue(String annotation) {
		
		IAnnotationModel annotationModel = annotations.get(annotation);
		if(annotationModel==null){
			return null;
		}
		
		return annotationModel.getValue(VALUE_METHOD_ID);		
	}

	public String[] getAnnotationValues(String annotation) {
		IAnnotationModel annotationModel = annotations.get(annotation);
		if(annotationModel==null){
			return null;
		}
		
		return annotationModel.getValues(VALUE_METHOD_ID);
	}

	public boolean hasAnnotation(String annotationName) {
		return annotations.containsKey(annotationName);
	}
	
	public IAnnotationModel getAnnotation(String name) {
		return annotations.get(name);
	}

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
}
