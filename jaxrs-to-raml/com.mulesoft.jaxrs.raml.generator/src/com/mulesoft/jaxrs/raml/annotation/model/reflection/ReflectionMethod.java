package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.mulesoft.jaxrs.raml.annotation.model.IDocInfo;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

/**
 * <p>ReflectionMethod class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class ReflectionMethod extends BasicReflectionMember<Method> implements IMethodModel {

	/**
	 * <p>Constructor for ReflectionMethod.</p>
	 *
	 * @param element a {@link java.lang.reflect.Method} object.
	 */
	public ReflectionMethod(Method element) {
		super(element);
	}

	
	/**
	 * <p>getParameters.</p>
	 *
	 * @return an array of {@link com.mulesoft.jaxrs.raml.annotation.model.IParameterModel} objects.
	 */
	public IParameterModel[] getParameters() {
		Class<?>[] parameterTypes = element.getParameterTypes();
		Annotation[][] parameterAnnotations = element.getParameterAnnotations();
		IParameterModel[] models=new IParameterModel[parameterTypes.length];
		int a=0;
		for (Class<?>cl:parameterTypes){
			models[a++]=new ReflectionParameter(cl, parameterAnnotations[a-1]);
		}
		return models;
	}

	
	/**
	 * <p>getBasicDocInfo.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.IDocInfo} object.
	 */
	public IDocInfo getBasicDocInfo() {
		return new IDocInfo() {
			
			
			public String getReturnInfo() {
				return "";
			}
			
			
			public String getDocumentation(String pName) {
				return "";
			}
			
			
			public String getDocumentation() {
				return "";
			}
		};
	}

	
	/**
	 * <p>getReturnedType.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	public ITypeModel getReturnedType() {
		Class<?> returnType = element.getReturnType();
		return new ReflectionType(returnType);
	}

	
	/**
	 * <p>getName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return element.getName();
	}

	
	/**
	 * <p>getBodyType.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	public ITypeModel getBodyType() {
		return null;
	}


	/** {@inheritDoc} */
	@Override
	public boolean isStatic() {
		return Modifier.isStatic(element.getModifiers());
	}


	/** {@inheritDoc} */
	@Override
	public boolean isPublic() {
		return Modifier.isPublic(element.getModifiers());		
	}


	/** {@inheritDoc} */
	@Override
	public ITypeModel getType() {
		return getReturnedType();
	}


	/** {@inheritDoc} */
	@Override
	public ITypeModel getJAXBType() {
		return null;
	}


	/** {@inheritDoc} */
	@Override
	public Class<?> getJavaType() {
		return element.getReturnType();
	}
}
