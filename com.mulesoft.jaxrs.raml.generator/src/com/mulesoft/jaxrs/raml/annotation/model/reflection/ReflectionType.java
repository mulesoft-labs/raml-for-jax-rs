package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.reflect.Method;

import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class ReflectionType extends BasicReflectionMember<Class<?>> implements ITypeModel{

	public ReflectionType(Class<?> element) {
		super(element);
	
	}

	@Override
	public IMethodModel[] getMethods() {
		Method[] declaredMethods = element.getDeclaredMethods();
		IMethodModel[] methods=new IMethodModel[declaredMethods.length];
		int a=0;
		for (Method m:declaredMethods){
			methods[a++]=new ReflectionMethod(m);
		}
		return methods;
	}

	@Override
	public String getName() {
		return element.getSimpleName();
	}

	@Override
	public String getFullyQualifiedName() {
		return element.getCanonicalName();
	}

}
