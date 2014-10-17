package org.raml.jaxrs.codegen.model;

import java.util.HashMap;
import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;

public class AnnotationModel implements IAnnotationModel{

	private String name;
	
	private String value;
	
	private HashMap<String,String[]> values;
	
	public AnnotationModel() {
	}

	
	public String getName() {		
//		DeclaredType annotationType = mirror.getAnnotationType();
//		return annotationType.asElement().getSimpleName().toString();
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	
	public String getValue(String pairName) {
//		Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror.getElementValues();
//		for (ExecutableElement q:elementValues.keySet()){
//			String name=q.getSimpleName().toString();
//			if (name.equals(pairName)){
//				return mirror.getElementValues().get(q).getValue().toString();
//			}
//		}
//		return null;
		return value;
	}

	
	public String[] getValues(String value) {
//		Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror.getElementValues();
//		for (ExecutableElement q:elementValues.keySet()){
//			String name=q.getSimpleName().toString();
//			if (name.equals(value)){
//				Object value2 = mirror.getElementValues().get(q).getValue();
//				if (value2 instanceof String){
//					return new String[]{(String) value2};
//				}
//				return (String[]) value2;
//			}
//		}
//		return null;
		return values != null ? values.get(value) : null;
	}


	public void addValues(String key, String[] valuesArray) {
		if(key==null||valuesArray==null){
			return;
		}
		if(this.values == null){
			this.values = new HashMap<String, String[]>(); 
		}
		values.put(key,valuesArray);		
	}

	
	public IAnnotationModel[] getSubAnnotations(String pairName) {
		return new IAnnotationModel[0];
	}


}
