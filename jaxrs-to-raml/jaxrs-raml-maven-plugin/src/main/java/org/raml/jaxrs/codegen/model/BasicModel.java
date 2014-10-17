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
}
