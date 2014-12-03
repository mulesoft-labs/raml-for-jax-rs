package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.mulesoft.jaxrs.raml.annotation.model.IDocInfo;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class ReflectionMethod extends BasicReflectionMember<Method> implements IMethodModel {

	public ReflectionMethod(Method element) {
		super(element);
	}

	
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

	
	public ITypeModel getReturnedType() {
		Class<?> returnType = element.getReturnType();
		return new ReflectionType(returnType);
	}

	
	public String getName() {
		return element.getName();
	}

	
	public ITypeModel getBodyType() {
		return null;
	}


	@Override
	public boolean isStatic() {
		return Modifier.isStatic(element.getModifiers());
	}


	@Override
	public boolean isPublic() {
		return Modifier.isPublic(element.getModifiers());		
	}


	@Override
	public ITypeModel getType() {
		return getReturnedType();
	}


	@Override
	public ITypeModel getJAXBType() {
		return null;
	}
}
