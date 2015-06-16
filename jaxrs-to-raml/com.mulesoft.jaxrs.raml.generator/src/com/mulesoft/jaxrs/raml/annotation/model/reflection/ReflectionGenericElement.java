package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

import com.mulesoft.jaxrs.raml.annotation.model.IGenericElement;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeParameter;

abstract public class ReflectionGenericElement<T extends AnnotatedElement>
		extends BasicReflectionMember<T> implements IGenericElement {

	public ReflectionGenericElement(T element) {
		super(element);
	}
	
	@Override
	public List<ITypeParameter> getTypeParameters() {
		
		ArrayList<ITypeParameter> list = new ArrayList<ITypeParameter>();
		if(this.element instanceof GenericDeclaration){
			TypeVariable<?>[] typeParameters = ((GenericDeclaration)element).getTypeParameters();
			for(TypeVariable<?> tv : typeParameters){
				list.add(new ReflectionTypeParameter(tv));
			}
		}
		return list;
	}
}
