package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;

public class APTAnnotation implements IAnnotationModel{

	AnnotationMirror mirror;
	
	public APTAnnotation(AnnotationMirror m) {
		this.mirror=m;
	}

	
	public String getName() {
		DeclaredType annotationType = mirror.getAnnotationType();
		return annotationType.asElement().getSimpleName().toString();
	}

	
	public String getValue(String pairName) {
		Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror.getElementValues();
		for (ExecutableElement q:elementValues.keySet()){
			String name=q.getSimpleName().toString();
			if (name.equals(pairName)){
				return mirror.getElementValues().get(q).getValue().toString();
			}
		}
		return null;
	}

	
	public String[] getValues(String value) {
		Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror.getElementValues();
		for (ExecutableElement q:elementValues.keySet()){
			String name=q.getSimpleName().toString();
			if (name.equals(value)){
				Object value2 = mirror.getElementValues().get(q).getValue();
				if (value2 instanceof String){
					return new String[]{(String) value2};
				}
				return (String[]) value2;
			}
		}
		return null;
	}

}
