package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;

/**
 * <p>Abstract BasicReflectionMember class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public abstract class BasicReflectionMember<T extends AnnotatedElement> implements IBasicModel{

	private static final String VALUE = "value";
	protected T element;

	
	/**
	 * <p>hashCode.</p>
	 *
	 * @return a int.
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		return result;
	}

	
	/** {@inheritDoc} */
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

	/**
	 * <p>Constructor for BasicReflectionMember.</p>
	 *
	 * @param element a T object.
	 */
	public BasicReflectionMember(T element) {
		super();
		this.element = element;
	}

	
	/**
	 * <p>getName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public abstract String getName() ;

	
	/**
	 * <p>getDocumentation.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDocumentation() {
		return "type some documentation here";
	}

	
	/**
	 * <p>getAnnotations.</p>
	 *
	 * @return an array of {@link com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel} objects.
	 */
	public IAnnotationModel[] getAnnotations() {
		Annotation[] annotations = element.getAnnotations();
		IAnnotationModel[] ml=new IAnnotationModel[annotations.length];
		for (int a=0;a<annotations.length;a++){
			ml[a]=new AnnotationModel(annotations[a]);
		}
		return ml;
	}

	
	/** {@inheritDoc} */
	public String getAnnotationValue(String annotation) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel q:annotations){
			if (getAnnotationName(q).equals(annotation)){
				return q.getValue(VALUE);
			}
		}
		return null;
	}


	private String getAnnotationName(IAnnotationModel q) {
		String name = q.getName();
		int ind = name.lastIndexOf('.');
		ind++;
		return name.substring(ind);
	}

	
	/** {@inheritDoc} */
	public String[] getAnnotationValues(String annotation) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel q:annotations){
			if (getAnnotationName(q).equals(annotation)){
				return q.getValues(VALUE);
			}
		}
		return null;
	}

	
	/** {@inheritDoc} */
	public boolean hasAnnotation(String name) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel q:annotations){
			if (getAnnotationName(q).equals(name)){
				return true;
			}
		}
		return false;
	}
	
	/** {@inheritDoc} */
	public IAnnotationModel getAnnotation(String name) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel m:annotations){
			if (getAnnotationName(m).equals(name)){
				return m;
			}
		}
		return null;
	}
	
	/** {@inheritDoc} */
	public boolean hasAnnotationWithCanonicalName(String name) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel q:annotations){
			if (q.getCanonicalName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	/** {@inheritDoc} */
	public IAnnotationModel getAnnotationByCanonicalName(String name) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel m:annotations){
			if (m.getCanonicalName().equals(name)){
				return m;
			}
		}
		return null;
	}

	/**
	 * <p>Getter for the field <code>element</code>.</p>
	 *
	 * @return a T object.
	 */
	public T getElement() {
		return element;
	}
}
