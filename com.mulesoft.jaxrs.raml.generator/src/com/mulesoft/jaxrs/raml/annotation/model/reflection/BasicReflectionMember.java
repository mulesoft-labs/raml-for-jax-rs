package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;

public abstract class BasicReflectionMember<T extends AnnotatedElement> implements IBasicModel{

	private static final String VALUE = "value";
	protected T element;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
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
		@SuppressWarnings("rawtypes")
		BasicReflectionMember other = (BasicReflectionMember) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		return true;
	}

	public BasicReflectionMember(T element) {
		super();
		this.element = element;
	}

	@Override
	public abstract String getName() ;

	@Override
	public String getDocumentation() {
		return "type some documentation here";
	}

	@Override
	public IAnnotationModel[] getAnnotations() {
		Annotation[] annotations = element.getAnnotations();
		IAnnotationModel[] ml=new IAnnotationModel[annotations.length];
		for (int a=0;a<annotations.length;a++){
			ml[a]=new AnnotationModel(annotations[a]);
		}
		return ml;
	}

	@Override
	public String getAnnotationValue(String annotation) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel q:annotations){
			if (q.getName().equals(annotation)){
				return q.getValue(VALUE);
			}
		}
		return null;
	}

	@Override
	public String[] getAnnotationValues(String annotation) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel q:annotations){
			if (q.getName().equals(annotation)){
				return q.getValues(VALUE);
			}
		}
		return null;
	}

	@Override
	public boolean hasAnnotation(String name) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel q:annotations){
			if (q.getName().equals(name)){
				return true;
			}
		}
		return false;
	}

	public T getElement() {
		return element;
	}
}