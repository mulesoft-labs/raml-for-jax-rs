package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;

/**
 * <p>AnnotationModel class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class AnnotationModel implements IAnnotationModel {

	Annotation annotation;
	
	/**
	 * <p>Constructor for AnnotationModel.</p>
	 *
	 * @param annotation2 a {@link java.lang.annotation.Annotation} object.
	 */
	public AnnotationModel(Annotation annotation2) {
		this.annotation=annotation2;
	}

	
	/**
	 * <p>getName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return annotation.annotationType().getSimpleName();
	}

	
	/** {@inheritDoc} */
	public String getValue(String pairName) {
		try {
			Method method = annotation.getClass().getMethod(pairName);
			Object invoke = method.invoke(annotation);
			if (invoke!=null){
				String value = invoke.toString();
				Object defaultValue = annotation.annotationType().getMethod(pairName).getDefaultValue();
				if(value.equals(defaultValue)){
					return null;
				}
				return value;
			}
			return null;
		} catch (NoSuchMethodException e) {
			return null;
		} catch (SecurityException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}

	
	/** {@inheritDoc} */
	public String[] getValues(String value) {
		try {
			Method method = annotation.getClass().getMethod(value);
			Object invoke = method.invoke(annotation);
			if (invoke!=null){
				if (invoke instanceof String[]){
					return (String[]) invoke;
				}
				return null;
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}


	
	/** {@inheritDoc} */
	public IAnnotationModel[] getSubAnnotations(String pairName) {
		try {
		Method method = annotation.getClass().getMethod(pairName);
		Object invoke = method.invoke(annotation);
		if (invoke!=null){
			if (invoke instanceof Annotation[]){
				Annotation[] anns = (Annotation[]) invoke;
				int length = anns.length;
				IAnnotationModel[] result=new IAnnotationModel[length];
				for (int a=0;a<length;a++){
					result[a]=new AnnotationModel(anns[a]);
				}
				return result;
			}
			return null;
		}
		return null;
		} catch (Exception e) {
			return null;
		}
	}


	@Override
	public String getCanonicalName() {
		return this.annotation.annotationType().getCanonicalName();
	}

}
