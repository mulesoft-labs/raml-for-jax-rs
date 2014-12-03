package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class ReflectionField extends BasicReflectionMember<Field> implements
		IFieldModel {

	public ReflectionField(Field element) {
		super(element);
	}

	@Override
	public String getName() {
		return element.getName();
	}

	@Override
	public ITypeModel getType() {
		Class<?> returnType = element.getType();
		return new ReflectionType(returnType);
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
	public ITypeModel getJAXBType() {
		return null;
	}
}
