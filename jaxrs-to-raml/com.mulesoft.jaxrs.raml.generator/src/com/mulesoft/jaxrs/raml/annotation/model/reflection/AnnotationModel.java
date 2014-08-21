package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;

public class AnnotationModel implements IAnnotationModel {

	Annotation annotation;
	
	public AnnotationModel(Annotation annotation2) {
		this.annotation=annotation2;
	}

	
	public String getName() {
		return annotation.annotationType().getSimpleName();
	}

	
	public String getValue(String pairName) {
		try {
			Method method = annotation.getClass().getMethod(pairName);
			Object invoke = method.invoke(annotation);
			if (invoke!=null){
				return invoke.toString();
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

}
