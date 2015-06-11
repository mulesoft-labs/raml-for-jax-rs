package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

import com.mulesoft.jaxrs.raml.annotation.model.IGenericElement;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeParameter;

abstract public class ReflectionGenericElement<T extends GenericDeclaration>
		extends BasicReflectionMember<T> implements IGenericElement {

	public ReflectionGenericElement(T element) {
		super(element);
	}
	
	@Override
	public List<ITypeParameter> getTypeParameters() {
		TypeVariable<?>[] typeParameters = element.getTypeParameters();
		ArrayList<ITypeParameter> list = new ArrayList<ITypeParameter>();
		for(TypeVariable<?> tv : typeParameters){
			list.add(new ReflectionTypeParameter(tv));
		}
		return list;
	}
}
