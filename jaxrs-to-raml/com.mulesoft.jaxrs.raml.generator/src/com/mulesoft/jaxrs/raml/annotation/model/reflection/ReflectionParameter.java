package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.annotation.Annotation;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;

/**
 * <p>ReflectionParameter class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class ReflectionParameter implements IParameterModel{

	private static final String VALUE = "value";
	protected ReflectionType type;
	protected String name;	
	/**
	 * <p>Constructor for ReflectionParameter.</p>
	 *
	 * @param type a {@link com.mulesoft.jaxrs.raml.annotation.model.reflection.ReflectionType} object.
	 * @param model an array of {@link com.mulesoft.jaxrs.raml.annotation.model.reflection.AnnotationModel} objects.
	 */
	public ReflectionParameter(ReflectionType type, AnnotationModel[] model,String name) {
		super();
		this.type = type;
		this.model = model;
		this.name = name;
	}
	
	/**
	 * <p>Constructor for ReflectionParameter.</p>
	 *
	 * @param cl a {@link java.lang.Class} object.
	 * @param annotations an array of {@link java.lang.annotation.Annotation} objects.
	 * @param name 
	 */
	public ReflectionParameter(Class<?> cl, Annotation[] annotations, String name) {
		this.type=new ReflectionType(cl);
		this.name = name;
		model=new AnnotationModel[annotations.length];
		int i=0;
		for (Annotation a:annotations){
			model[i++]=new AnnotationModel(a);
		}
	}
	
	protected AnnotationModel[] model;

	
	/**
	 * <p>getDocumentation.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDocumentation() {
		return "";
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

	
	/** {@inheritDoc} */
	public String[] getAnnotationValues(String annotation) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel q:annotations){
			if (getAnnotationName(q).equals(annotation)){
				return q.getValues(annotation);
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

	private String getAnnotationName(IAnnotationModel q) {
		String n = q.getName();
		int ind = n.lastIndexOf('.');
		ind++;
		return n.substring(ind);
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
	
	/**
	 * <p>getName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * <p>Getter for the field <code>type</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getParameterType() {
		String name = type.getName();		
		return name;
	}
	
	/**
	 * <p>required.</p>
	 *
	 * @return a boolean.
	 */
	public boolean required() {
		return type.element.isPrimitive();
	}
	
	/**
	 * <p>getAnnotations.</p>
	 *
	 * @return an array of {@link com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel} objects.
	 */
	public IAnnotationModel[] getAnnotations() {
		return model;
	}
}
