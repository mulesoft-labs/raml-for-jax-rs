package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.mulesoft.jaxrs.raml.annotation.model.IDocInfo;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class ReflectionMethod extends BasicReflectionMember<Method> implements IMethodModel {

	public ReflectionMethod(Method element) {
		super(element);
	}

	@Override
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

	@Override
	public IDocInfo getBasicDocInfo() {
		return new IDocInfo() {
			
			@Override
			public String getReturnInfo() {
				return "";
			}
			
			@Override
			public String getDocumentation(String pName) {
				return "";
			}
			
			@Override
			public String getDocumentation() {
				return "";
			}
		};
	}

	@Override
	public ITypeModel getReturnedType() {
		Class<?> returnType = element.getReturnType();
		return new ReflectionType(returnType);
	}

	@Override
	public String getName() {
		return element.getName();
	}
}
